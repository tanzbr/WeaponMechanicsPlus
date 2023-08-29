package com.cjcrafter.weaponmechanicsplus.weapon.guidedprojectile

import me.deecaad.weaponmechanics.weapon.projectile.ProjectileScript
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.WeaponProjectile
import org.bukkit.entity.LivingEntity
import org.bukkit.plugin.Plugin

class GuidedProjectileScript(owner: Plugin, projectile: WeaponProjectile) :
    ProjectileScript<WeaponProjectile>(owner, projectile) {

    private var guidedProjectile: GuidedProjectile

    private var currentTarget : LivingEntity? = null


    init {
        guidedProjectile = GuidedProjectile()//.getConfigurations().getObject(projectile.weaponTitle + ".Projectile.Guided_Projectile", GuidedProjectile::class.java)!!
    }

    override fun onTickStart() {
        if (currentTarget == null) {
            currentTarget = projectile.world.getNearbyEntities(projectile.location.toLocation(projectile.world), 32.0, 32.0, 32.0) { a -> a.type.isAlive && a.entityId != projectile.shooter?.entityId }
                .first() as LivingEntity?
        }

        if (currentTarget != null) {
            val oldLength = projectile.motionLength
            projectile.motion = guidedProjectile.rotateVector(projectile.normalizedMotion, projectile.location, currentTarget!!.eyeLocation.toVector()).multiply(oldLength)
        }
    }
}