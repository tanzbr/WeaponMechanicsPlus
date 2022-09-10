package me.deecaad.weaponmechanicsplus.weapon.guidedprojectile

import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.weapon.projectile.ProjectileScript
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.RayTraceResult
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.WeaponProjectile
import org.bukkit.entity.LivingEntity
import org.bukkit.plugin.Plugin

class GuidedProjectileScript(owner: Plugin, projectile: WeaponProjectile) :
    ProjectileScript<WeaponProjectile>(owner, projectile) {

    private var guidedProjectile: GuidedProjectile

    private var currentTarget : LivingEntity? = null

    init {
        guidedProjectile = WeaponMechanics.getConfigurations().getObject(projectile.weaponTitle + ".Projectile.Guided_Projectile", GuidedProjectile::class.java)!!
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onTickStart() {
        super.onTickStart()
    }

    override fun onTickEnd() {
        super.onTickEnd()
    }

    override fun onEnd() {
        super.onEnd()
    }

    override fun onCollide(hit: RayTraceResult) {
        super.onCollide(hit)
    }
}