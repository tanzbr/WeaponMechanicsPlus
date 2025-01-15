package com.cjcrafter.weaponmechanicsplus.listeners

import com.cjcrafter.armormechanics.events.ArmorMechanicsDequipEvent
import com.cjcrafter.armormechanics.events.ArmorMechanicsEquipEvent
import com.cjcrafter.armormechanics.events.ArmorUpdateEvent
import com.cjcrafter.armormechanics.events.ResistBulletDamageEvent
import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusAPI
import com.cjcrafter.weaponmechanicsplus.listeners.ModifierListeners.Companion.updateMechanics
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.AttributeModifiers
import me.deecaad.core.compatibility.CompatibilityAPI
import me.deecaad.weaponmechanics.weapon.damage.DamagePoint
import me.deecaad.weaponmechanics.weapon.damage.ExplosionDamageSource
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
        val armorMeta = armor.itemMeta ?: return

        // remove any modifiers from the WeaponMechanicsPlus namespace...
        // prevents any duplication (if it is even possible)
        AttributeModifiers.stripAllAttributeModifiers(armorMeta)

        WeaponMechanicsPlusAPI.forEachAttachment(armor) { attachment ->
            attachment.armorModifier?.attributeModifiers
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
                    if (event.weaponDamageEvent.source is ExplosionDamageSource)
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
            if (event.weaponDamageEvent.source is ExplosionDamageSource)
                attachment.armorModifier?.explosionResistance?.let { resistance = it.apply(resistance) }
            else
                attachment.armorModifier?.bulletResistance?.let { resistance = it.apply(resistance) }
        }
        event.rate = 1.0 - resistance
    }
}