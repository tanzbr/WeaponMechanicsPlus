package com.cjcrafter.weaponmechanicsplus.placeholders

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import me.deecaad.core.events.EntityEquipmentEvent
import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.core.placeholder.PlaceholderMessage
import me.deecaad.core.placeholder.PlaceholderMessageChain
import me.deecaad.core.utils.AdventureUtil
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.utils.CustomTag
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponStopShootingEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class WeaponMechanicsPlaceholderListener : Listener {

    private val weaponMechanicsDisplays: MutableMap<String, PlaceholderMessage> = HashMap()
    private val weaponMechanicsLores: MutableMap<String, PlaceholderMessageChain> = HashMap()
    private val frequentUpdates: MutableSet<String> = HashSet()

    init {
        val tagsWithFrequentUpdates = setOf(
            "ammo_left", "custom_durability", "reload"
        )

        if (WeaponMechanics.getConfigurations() == null) {
            throw IllegalStateException("WeaponMechanicsPlus cannot load placeholders without WeaponMechanics loaded.")
        }

        // WeaponMechanics weapon updating template
        for (title in WeaponMechanics.getWeaponHandler().infoHandler.sortedWeaponList) {
            val display = WeaponMechanics.getConfigurations().getString("$title.Info.Weapon_Item.Name")?.let {
                PlaceholderMessage(it)
            }
            val lores = WeaponMechanics.getConfigurations().getObject("$title.Info.Weapon_Item.Lore", List::class.java)?.map {
                PlaceholderMessage(it as String)
            }

            // Frequent updates are used for when a placeholder changes often.
            val needsFrequentUpdates = tagsWithFrequentUpdates.any { display?.presentPlaceholders?.contains(it) == true }
                    || lores?.any { lore -> tagsWithFrequentUpdates.any { lore.presentPlaceholders.contains(it) } } ?: false
            if (needsFrequentUpdates)
                frequentUpdates.add(title)

            if (display != null) weaponMechanicsDisplays[title] = display
            if (lores != null) weaponMechanicsLores[title] = PlaceholderMessageChain(lores)
        }

        WeaponMechanicsPlus.getDebug().info("Loaded ${weaponMechanicsDisplays.size} weapon mechanics placeholders")
    }

    @EventHandler
    fun onEquip(event: EntityEquipmentEvent) {
        val player = event.entity as? Player ?: return

        val item: ItemStack? = event.equipped
        if (item == null || !item.hasItemMeta() || CustomTag.WEAPON_TITLE.getString(item) == null)
            return

        // We want to update the weapon, but unfortunately we cannot modify the
        // event since it contains a COPY of the weapon. So check 1 tick later.
        WeaponMechanicsPlus.getScheduler().entity(player).runDelayed(Runnable {
            val weaponStack = when (event.slot) {
                EquipmentSlot.HAND -> player.inventory.itemInMainHand
                EquipmentSlot.OFF_HAND -> player.inventory.itemInOffHand
                else -> return@Runnable
            }

            // We make the assumption that we know NOTHING about this item. 1 tick later, the
            // user may have switched weapons, so we need to do every check again.
            if (!weaponStack.hasItemMeta()) return@Runnable
            val weaponTitle = CustomTag.WEAPON_TITLE.getString(weaponStack) ?: return@Runnable

            val display = weaponMechanicsDisplays[weaponTitle]
            val lore = weaponMechanicsLores[weaponTitle]

            val placeholderData = PlaceholderData.of(player, weaponStack, weaponTitle, event.slot)
            display?.replaceAndDeserialize(placeholderData)?.let { AdventureUtil.setName(weaponStack, it) }
            lore?.replaceAndDeserialize(placeholderData)?.let { AdventureUtil.setLore(weaponStack, it) }
        }, 1L)
    }

    @EventHandler
    fun onStopShoot(event: WeaponStopShootingEvent) {
        val player = event.entity as? Player ?: return

        // Very few tags need to be updated after every shot... Only update
        // display/lore tags when needed.
        if (!frequentUpdates.contains(event.weaponTitle))
            return

        val display = weaponMechanicsDisplays[event.weaponTitle]
        val lore = weaponMechanicsLores[event.weaponTitle]

        val placeholderData = PlaceholderData.of(player, event.weaponStack, event.weaponTitle, null)
        display?.replaceAndDeserialize(placeholderData)?.let { AdventureUtil.setName(event.weaponStack, it) }
        lore?.replaceAndDeserialize(placeholderData)?.let { AdventureUtil.setLore(event.weaponStack, it) }
    }
}