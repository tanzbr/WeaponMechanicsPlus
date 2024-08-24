package com.cjcrafter.weaponmechanicsplus.placeholders.handlers

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusAPI
import me.deecaad.core.placeholder.NumericPlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.weaponmechanics.WeaponMechanics

class BaseDamagePlaceholderHandler : NumericPlaceholderHandler("base_damage") {
    override fun requestValue(data: PlaceholderData): Number? {
        val config = WeaponMechanics.getConfigurations()
        var baseDamage = config.getDouble("${data.itemTitle()}.Damage.Base_Damage")
        val player = data.player() ?: return baseDamage
        val weaponTitle = data.itemTitle() ?: return baseDamage

        WeaponMechanicsPlusAPI.forEachModifier(player, data.item()) { modifier ->
            modifier.getWeaponModifier(weaponTitle)?.damage?.baseDamage?.let {
                baseDamage = it.apply(baseDamage)
            }
        }

        return baseDamage
    }
}