package com.cjcrafter.weaponmechanicsplus.placeholders

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import me.deecaad.core.MechanicsCore
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
import org.bukkit.scheduler.BukkitRunnable

class WeaponMechanicsPlaceholderListener : Listener {

    private val weaponMechanicsDisplays: MutableMap<String, PlaceholderMessage> = HashMap()
    private val weaponMechanicsLores: MutableMap<String, PlaceholderMessageChain> = HashMap()
    private val frequentUpdates: MutableSet<String> = HashSet()

    init {
        val tagsWithFrequentUpdates = setOf(
            "ammo_left", "custom_durability", "reload"
        )

        val mini = MechanicsCore.getPlugin().message

        // TODO: Test how lore changes, see if anything breaks
        if (true) {

            // WeaponMechanics weapon updating template
            for (title in WeaponMechanics.getWeaponHandler().infoHandler.sortedWeaponList) {
                val item = WeaponMechanics.getConfigurations().getObject("$title.Info.Weapon_Item", ItemStack::class.java)!!

                val display = PlaceholderMessage(mini.serialize(AdventureUtil.getName(item)))
                val lores = AdventureUtil.getLore(item)?.map { mini.serialize(it) }?.map { PlaceholderMessage(it) }

                // Frequent updates are used for when a placeholder changes often.
                val needsFrequentUpdates = tagsWithFrequentUpdates.any { display.presentPlaceholders.contains(it) }
                        || lores?.any { lore -> tagsWithFrequentUpdates.any { lore.presentPlaceholders.contains(it) } } ?: false
                if (needsFrequentUpdates)
                    frequentUpdates.add(title)

                weaponMechanicsDisplays[title] = display
                if (lores != null) weaponMechanicsLores[title] = PlaceholderMessageChain(lores)
            }
        }
    }

    @EventHandler
    fun onEquip(event: EntityEquipmentEvent) {
        val player = event.entity as? Player ?: return

        val item: ItemStack? = event.equipped
        if (item == null || !item.hasItemMeta() || CustomTag.WEAPON_TITLE.getString(item) == null)
            return

        // We want to update the weapon, but unfortunately we cannot modify the
        // event since it contains a COPY of the weapon. So check 1 tick later.
        object : BukkitRunnable() {
            override fun run() {
                val weaponStack = when (event.slot) {
                    EquipmentSlot.HAND -> player.inventory.itemInMainHand
                    EquipmentSlot.OFF_HAND -> player.inventory.itemInOffHand
                    else -> return
                }

                if (!weaponStack.hasItemMeta()) return
                val weaponTitle = CustomTag.WEAPON_TITLE.getString(weaponStack) ?: return

                // Dupe protection, it is theoretically possible for a client
                // to swap out the item with a different weapon. Not a very useful
                // dupe since it replaces the old weapon, but still a potential bug
                if (item != weaponStack) return

                val display = weaponMechanicsDisplays[weaponTitle]
                val lore = weaponMechanicsLores[weaponTitle]

                val placeholderData = PlaceholderData.of(player, weaponStack, weaponTitle, event.slot)
                display?.replaceAndDeserialize(placeholderData)?.let { AdventureUtil.setName(weaponStack, it) }
                lore?.replaceAndDeserialize(placeholderData)?.let { AdventureUtil.setLore(weaponStack, it) }
            }
        }.runTask(WeaponMechanicsPlus.getPlugin())
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