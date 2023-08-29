package com.cjcrafter.weaponmechanicsplus.weapon.firemode

import me.deecaad.core.mechanics.CastData
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.utils.CustomTag
import me.deecaad.weaponmechanics.weapon.info.WeaponInfoDisplay
import me.deecaad.weaponmechanics.weapon.trigger.TriggerListener
import me.deecaad.weaponmechanics.weapon.trigger.TriggerType
import me.deecaad.weaponmechanics.wrappers.EntityWrapper
import me.deecaad.weaponmechanics.wrappers.PlayerWrapper
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class FireModeTriggerListener : TriggerListener {

    override fun allowOtherTriggers() = false

    override fun tryUse(
        entityWrapper: EntityWrapper,
        weaponTitle: String,
        weaponStack: ItemStack,
        slot: EquipmentSlot,
        triggerType: TriggerType,
        dualWield: Boolean,
        victim: LivingEntity?
    ): Boolean {
        val config = WeaponMechanics.getConfigurations()
        val fireMode = config.getObject("$weaponTitle.Fire_Mode", FireMode::class.java)
        if (fireMode == null || !fireMode.trigger.check(triggerType, slot, entityWrapper)) {
            return false
        }

        entityWrapper.mainHandData.cancelTasks()
        entityWrapper.offHandData.cancelTasks()

        fireMode.switchMechanics?.use(CastData(entityWrapper.entity, weaponTitle, weaponStack))

        val nextWeapon = fireMode.nextMode
        CustomTag.WEAPON_TITLE.setString(weaponStack, nextWeapon)

        val weaponInfoDisplay = config.getObject("$nextWeapon.Info.Weapon_Info_Display", WeaponInfoDisplay::class.java)
        weaponInfoDisplay?.send(entityWrapper as PlayerWrapper, slot)

        WeaponMechanics.getWeaponHandler().skinHandler.tryUse(triggerType, entityWrapper, nextWeapon, weaponStack, slot)
        return true
    }
}