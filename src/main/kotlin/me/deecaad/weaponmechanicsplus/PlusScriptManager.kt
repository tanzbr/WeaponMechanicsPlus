package me.deecaad.weaponmechanicsplus

import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.weapon.projectile.AProjectile
import me.deecaad.weaponmechanics.weapon.projectile.ProjectileScriptManager
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.WeaponProjectile
import me.deecaad.weaponmechanicsplus.weapon.guidedprojectile.GuidedProjectileScript
import org.bukkit.plugin.Plugin

class PlusScriptManager(plugin: Plugin?) : ProjectileScriptManager(plugin) {

    override fun attach(aProjectile: AProjectile) {
        if (aProjectile !is WeaponProjectile)
            return

        if (WeaponMechanics.getConfigurations().containsKey(aProjectile.weaponTitle + ".Projectile.Guided_Projectile")) {
            aProjectile.addProjectileScript(GuidedProjectileScript(plugin, aProjectile))
        }
    }
}