/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.placeholders.handlers

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusAPI
import me.deecaad.core.placeholder.NumericPlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.NamespacedKey

class BaseDamagePlaceholderHandler : NumericPlaceholderHandler() {
    override fun getKey() = NamespacedKey(WeaponMechanics.getInstance(), "base_damage")

    override fun requestValue(data: PlaceholderData): Number? {
        val config = WeaponMechanics.getInstance().weaponConfigurations
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