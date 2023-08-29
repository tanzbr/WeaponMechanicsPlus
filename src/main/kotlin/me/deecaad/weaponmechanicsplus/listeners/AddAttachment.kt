package me.deecaad.weaponmechanicsplus.listeners

import me.deecaad.core.file.BukkitConfig
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.SerializerException
import me.deecaad.core.mechanics.CastData
import me.deecaad.core.utils.FileUtil
import me.deecaad.core.utils.FileUtil.PathReference
import me.deecaad.core.utils.LogLevel
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.listeners.RepairItemListener.RepairKit
import me.deecaad.weaponmechanics.utils.CustomTag
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlus
import me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments.Attachment
import me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry
import org.bukkit.ChatColor
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.inventory.PlayerInventory
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class AddAttachment : Listener {

    init {
        val attachmentsFolder = File(WeaponMechanics.getPlugin().dataFolder, "attachments")
        try {

            // Ensure the folder exists
            if (!attachmentsFolder.exists()) FileUtil.copyResourcesTo(
                javaClass.classLoader.getResource("WeaponMechanics/attachments"),
                attachmentsFolder.toPath()
            )

            // Read in all files within the folder
            val pathReference = PathReference.of(attachmentsFolder.toURI())
            Files.walkFileTree(pathReference.path(), object : SimpleFileVisitor<Path>() {
                @Throws(IOException::class)
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val stream = Files.newInputStream(file)
                    val config: YamlConfiguration

                    try {
                        config = YamlConfiguration()
                        config.load(InputStreamReader(stream))
                    } catch (ex: InvalidConfigurationException) {
                        WeaponMechanics.debug.log(
                            LogLevel.WARN,
                            "Could not read file '" + file.toFile() + "'... make sure it is a valid YAML file"
                        )
                        return FileVisitResult.CONTINUE
                    }

                    // For each key in the file, treat it as a new repair kit.
                    for (key in config.getKeys(false)) {
                        try {
                            val data = SerializeData(RepairKit(), file.toFile(), key, BukkitConfig(config))
                            val attachment = data.of().serialize(Attachment()) as Attachment
                            if (AttachmentRegistry.INSTANCE.has(attachment)) throw data.exception(
                                null,
                                "Found duplicate Attachment name '$key'"
                            )

                            AttachmentRegistry.INSTANCE.add(attachment)
                        } catch (ex: SerializerException) {
                            ex.log(WeaponMechanics.debug)
                        }
                    }
                    return FileVisitResult.CONTINUE
                }
            })
        } catch (e: Throwable) {
            WeaponMechanics.debug.log(LogLevel.ERROR, "Some error occurred whilst reading repair_kits folder", e)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {

        // Let's only allow attachments in the player's inventory... Hopefully
        // this will avoid most duplication issues with other plugins (like in gui)
        val inventory = event.clickedInventory
        if (inventory !is PlayerInventory)
            return

        // Require a drag and drop action
        val item = inventory.getItem(event.slot)
        val attachmentItem = event.cursor
        if (item == null || attachmentItem == null || !item.hasItemMeta() || !attachmentItem.hasItemMeta())
            return

        // need to drag and drop an attachment onto a weapon
        val itemTitle = CustomTag.WEAPON_TITLE.getString(item) ?: CustomTag.ARMOR_TITLE.getString(item)
        val attachmentTitle = CustomTag.ATTACHMENT_TITLE.getString(attachmentItem)
        if (itemTitle == null || attachmentTitle == null)
            return

        // Users in creative mode get "item creation privilege" which means that
        // items get duplicated.
        if (event is InventoryCreativeEvent) {
            event.whoClicked.sendMessage("${ChatColor.RED}Cannot use attachments while in Creative mode")
            WeaponMechanicsPlus.getDebug().debug("Cannot use InventoryCreativeEvent for attachments")
            return
        }

        // This happens when the admin deleted attachment from config, but
        // players still have the attachment items in their inventory.
        val attachment = AttachmentRegistry.INSTANCE[attachmentTitle]
        if (attachment == null) {
            WeaponMechanicsPlus.getDebug().warn("Attachment '$attachmentTitle' no longer exists in config. (tried adding)")
            return
        }

        if (!attachment.canAttach(item)) {
            attachment.denyMechanics?.use(CastData(event.whoClicked, itemTitle, item))
            return
        }

        // Now we handle the actual attachment part
        attachmentItem.amount -= 1
        attachment.attach(item)
        attachment.equipMechanics?.use(CastData(event.whoClicked, itemTitle, item))

        // Cancel the event, so we don't pick up the weapon
        event.isCancelled = true
    }
}