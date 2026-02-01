/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.firemode

import me.deecaad.core.mechanics.CastData
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.utils.CustomTag
import me.deecaad.weaponmechanics.weapon.info.WeaponInfoDisplay
import me.deecaad.weaponmechanics.weapon.trigger.TriggerListener
import me.deecaad.weaponmechanics.weapon.trigger.TriggerType
import me.deecaad.weaponmechanics.wrappers.EntityWrapper
import me.deecaad.weaponmechanics.wrappers.PlayerWrapper
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class FireModeTriggerListener : TriggerListener {

    override fun allowOtherTriggers() = false

    private fun safeTagTitle(stack: ItemStack?): String? {
        if (stack == null || stack.type == Material.AIR) return null

        // MechanicsCore's NBT_Persistent can throw when meta is null
        return try {
            CustomTag.WEAPON_TITLE.getString(stack)
        } catch (_: Throwable) {
            null
        }
    }

    override fun tryUse(
        entityWrapper: EntityWrapper,
        weaponTitle: String,
        weaponStack: ItemStack,
        slot: EquipmentSlot,
        triggerType: TriggerType,
        dualWield: Boolean,
        victim: LivingEntity?
    ): Boolean {
        val config = WeaponMechanics.getInstance().weaponConfigurations

        val actualTitle = safeTagTitle(weaponStack) ?: weaponTitle

        val fireMode = config.getObject("$actualTitle.Fire_Mode", FireMode::class.java)
            ?: config.getObject("$actualTitle.Firearm_Action.Fire_Mode", FireMode::class.java)
            ?: FireModeRegistry.get(actualTitle)
            ?: return false

        if (!fireMode.trigger.check(triggerType, slot, entityWrapper))
            return false

        entityWrapper.mainHandData.cancelTasks()
        entityWrapper.offHandData.cancelTasks()

        fireMode.switchMechanics?.use(CastData(entityWrapper.entity, actualTitle, weaponStack))
        val newWeaponTitle = fireMode.switch(actualTitle, weaponStack) ?: return true

        val weaponInfoDisplay = config.getObject("$newWeaponTitle.Info.Weapon_Info_Display", WeaponInfoDisplay::class.java)
        if (entityWrapper is PlayerWrapper) {
            weaponInfoDisplay?.send(entityWrapper, slot)
        }

        WeaponMechanics.getInstance().weaponHandler.skinHandler.tryUse(triggerType, entityWrapper, newWeaponTitle, weaponStack, slot)

        val player = entityWrapper.entity as? Player
        if (player != null) {
            when (slot) {
                EquipmentSlot.HAND -> player.inventory.setItemInMainHand(weaponStack)
                EquipmentSlot.OFF_HAND -> player.inventory.setItemInOffHand(weaponStack)
                else -> {}
            }
        }

        return true
    }
}