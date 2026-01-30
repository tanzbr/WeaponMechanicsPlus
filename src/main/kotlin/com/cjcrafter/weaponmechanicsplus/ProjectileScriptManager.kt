/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus

import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.weapon.projectile.AProjectile
import me.deecaad.weaponmechanics.weapon.projectile.ProjectileScriptManager
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.WeaponProjectile
import com.cjcrafter.weaponmechanicsplus.weapon.guidedprojectile.GuidedProjectileScript
import org.bukkit.plugin.Plugin

class ProjectileScriptManager(plugin: Plugin) : ProjectileScriptManager(plugin) {

    override fun attach(aProjectile: AProjectile) {
        if (aProjectile !is WeaponProjectile)
            return

        val config = WeaponMechanics.getInstance().weaponConfigurations
        if (config.contains("${aProjectile.weaponTitle}.Projectile.Guided_Projectile")) {
            aProjectile.addProjectileScript(GuidedProjectileScript(plugin, aProjectile))
        }
    }
}