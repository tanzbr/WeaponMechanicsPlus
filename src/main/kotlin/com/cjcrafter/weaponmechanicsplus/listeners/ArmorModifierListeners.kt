package com.cjcrafter.weaponmechanicsplus.listeners

import com.cjcrafter.armormechanics.events.ArmorMechanicsDequipEvent
import com.cjcrafter.armormechanics.events.ArmorMechanicsEquipEvent
import com.cjcrafter.armormechanics.events.ArmorUpdateEvent
import com.cjcrafter.armormechanics.events.ResistBulletDamageEvent
import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusAPI
import com.cjcrafter.weaponmechanicsplus.listeners.ModifierListeners.Companion.updateMechanics
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.AttributeModifier
import me.deecaad.core.compatibility.CompatibilityAPI
import me.deecaad.weaponmechanics.weapon.damage.DamagePoint
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ArmorModifierListeners : Listener {

    @EventHandler
    fun onEquip(event: ArmorMechanicsEquipEvent) {
        WeaponMechanicsPlusAPI.forEachAttachment(event.armor) {
            val armor = it.armorModifier ?: return@forEachAttachment

            updateMechanics(event.equipMechanics, armor.equipMechanicsModifier)
        }
    }

    @EventHandler
    fun onDequip(event: ArmorMechanicsDequipEvent) {
        WeaponMechanicsPlusAPI.forEachAttachment(event.armor) {
            val armor = it.armorModifier ?: return@forEachAttachment

            updateMechanics(event.dequipMechanics, armor.dequipMechanicsModifier)
        }
    }

    @EventHandler
    fun onUpdate(event: ArmorUpdateEvent) {
        val armor = event.armor

        val lists = ArrayList<List<AttributeModifier>>()
        WeaponMechanicsPlusAPI.forEachAttachment(armor) { attachment ->
            attachment.armorModifier?.attributeModifiers?.let { lists.add(it) }
        }

        val attributes = AttributeModifier.flatten(lists)
        for (attribute in attributes) {
            val current = CompatibilityAPI.getNBTCompatibility().getAttribute(armor, attribute.attribute, attribute.slot)
            val value = (current?.amount ?: 0.0) + attribute.amount
            CompatibilityAPI.getNBTCompatibility().setAttribute(armor, attribute.attribute, attribute.slot, value)
        }
    }

    @EventHandler
    fun onBulletResist(event: ResistBulletDamageEvent) {
        val damagePoint: DamagePoint? = event.weaponDamageEvent.point
        val equipment = event.weaponDamageEvent.victim.equipment ?: return
        val armor = when (damagePoint) {
            DamagePoint.HEAD -> equipment.helmet
            DamagePoint.BODY, DamagePoint.ARMS -> equipment.chestplate
            DamagePoint.LEGS -> equipment.leggings
            DamagePoint.FEET -> equipment.boots
            null -> {
                // No damage point? Use all modifiers for the entity
                var resistance = 1.0 - event.rate
                WeaponMechanicsPlusAPI.forEachModifier(event.weaponDamageEvent.victim) { modifier ->
                    if (event.weaponDamageEvent.isExplosion)
                        modifier.armorModifier?.explosionResistance?.let { resistance = it.apply(resistance) }
                    else
                        modifier.armorModifier?.bulletResistance?.let { resistance = it.apply(resistance) }
                }
                event.rate = 1.0 - resistance
                null
            }
        } ?: return

        // Since we know the damage slot, we only check that specific armor
        var resistance = 1.0 - event.rate
        WeaponMechanicsPlusAPI.forEachAttachment(armor) { attachment ->
            if (event.weaponDamageEvent.isExplosion)
                attachment.armorModifier?.explosionResistance?.let { resistance = it.apply(resistance) }
            else
                attachment.armorModifier?.bulletResistance?.let { resistance = it.apply(resistance) }
        }
        event.rate = 1.0 - resistance
    }
}