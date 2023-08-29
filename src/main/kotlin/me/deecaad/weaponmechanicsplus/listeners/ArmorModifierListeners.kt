package me.deecaad.weaponmechanicsplus.listeners

import com.cjcrafter.armormechanics.events.ArmorMechanicsDequipEvent
import com.cjcrafter.armormechanics.events.ArmorMechanicsEquipEvent
import com.cjcrafter.armormechanics.events.ArmorUpdateEvent
import com.cjcrafter.armormechanics.events.ResistBulletDamageEvent
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI
import me.deecaad.weaponmechanicsplus.listeners.ModifierListeners.Companion.updateMechanics
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ArmorModifierListeners : Listener {

    @EventHandler
    fun onEquip(event: ArmorMechanicsEquipEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.armor)
        for (modifier in modifiers) {
            val armor = modifier.armor ?: return

            updateMechanics(event.equipMechanics, armor.equipMechanicsModifier)
            // todo potion
        }
    }

    @EventHandler
    fun onDequip(event: ArmorMechanicsDequipEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.armor)
        for (modifier in modifiers) {
            val armor = modifier.armor ?: return

            updateMechanics(event.dequipMechanics, armor.dequipMechanicsModifier)
            // todo potion
        }
    }

    @EventHandler
    fun onUpdate(event: ArmorUpdateEvent) {

    }

    @EventHandler
    fun onBulletResist(event: ResistBulletDamageEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers()
        for (modifier in modifiers) {
            val armor = modifier.armor ?: return

        }
    }
}