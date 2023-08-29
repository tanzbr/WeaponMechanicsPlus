package com.cjcrafter.weaponmechanicsplus.listeners

import com.cjcrafter.armormechanics.events.ArmorMechanicsDequipEvent
import com.cjcrafter.armormechanics.events.ArmorMechanicsEquipEvent
import com.cjcrafter.armormechanics.events.ArmorUpdateEvent
import com.cjcrafter.armormechanics.events.ResistBulletDamageEvent
import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusAPI
import com.cjcrafter.weaponmechanicsplus.listeners.ModifierListeners.Companion.updateMechanics
import me.deecaad.weaponmechanics.weapon.damage.DamagePoint
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
        val damagePoint = event.weaponDamageEvent.point ?: return
        val equipment = event.weaponDamageEvent.victim.equipment ?: return
        val armor = when (damagePoint) {
            DamagePoint.HEAD -> equipment.helmet
            DamagePoint.BODY, DamagePoint.ARMS -> equipment.chestplate
            DamagePoint.LEGS -> equipment.leggings
            DamagePoint.FEET -> equipment.boots
        } ?: return

        val modifiers = WeaponMechanicsPlusAPI.getModifiers(armor)
        for (modifier in modifiers) {
            val bulletResistanceModifier = modifier.armor?.bulletResistance ?: return

            var bulletResistance = 1.0 - event.rate
            bulletResistance = bulletResistanceModifier.apply(bulletResistance)
            event.rate = 1.0 - bulletResistance
        }
    }
}