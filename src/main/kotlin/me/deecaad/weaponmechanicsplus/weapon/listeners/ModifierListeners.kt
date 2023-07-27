package me.deecaad.weaponmechanicsplus.weapon.listeners

import me.deecaad.core.mechanics.Mechanics
import me.deecaad.weaponmechanics.weapon.weaponevents.*
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponScopeEvent.ScopeType
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ModifierListeners : Listener {

    @EventHandler
    fun onDamage(event: WeaponDamageEntityEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val damage = modifier.damage ?: continue

            damage.armorDamage?.let { event.armorDamage = it.apply(event.armorDamage) }
            damage.fireTicks?.let { event.fireTicks = it.apply(event.fireTicks) }

            if (event.isExplosion)
                damage.explosionDamage?.let { event.baseDamage = it.apply(event.baseDamage) }
            else
                damage.baseDamage?.let { event.baseDamage = it.apply(event.baseDamage) }

            if (damage.replaceDefaultDamageModifier)
                event.damageModifiers[0] = damage.damageModifier
            else if (damage.damageModifier != null)
                event.damageModifiers.add(damage.damageModifier)

            // Mechanics stuff
            event.damageMechanics = updateMechanics(event.damageMechanics, damage.damageMechanicsModifier)
            event.killMechanics = updateMechanics(event.killMechanics, damage.killMechanicsModifier)
            event.backstabMechanics = updateMechanics(event.backstabMechanics, damage.backstabMechanicsModifier)
            event.criticalHitMechanics = updateMechanics(event.criticalHitMechanics, damage.criticalHitMechanicsModifier)
            event.headMechanics = updateMechanics(event.headMechanics, damage.headMechanicsModifier)
            event.bodyMechanics = updateMechanics(event.bodyMechanics, damage.bodyMechanicsModifier)
            event.armsMechanics = updateMechanics(event.armsMechanics, damage.armsMechanicsModifier)
            event.legsMechanics = updateMechanics(event.legsMechanics, damage.legsMechanicsModifier)
            event.feetMechanics = updateMechanics(event.feetMechanics, damage.feetMechanicsModifier)
        }
    }

    @EventHandler
    fun onExplode(event: ProjectilePreExplodeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack ?: return)
        for (modifier in modifiers) {
            val explosion = modifier.explosion ?: continue

            explosion.overrideExplosion?.let { event.explosion = explosion.overrideExplosion }
        }
    }

    @EventHandler
    fun onExplode(event: ProjectileExplodeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack ?: return)
        for (modifier in modifiers) {
            val explosion = modifier.explosion ?: continue

            event.mechanics = updateMechanics(event.mechanics, explosion.mechanicsModifier)
        }
    }

    @EventHandler
    fun onReload(event: WeaponReloadEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val reload = modifier.reload ?: continue

            event.mechanics = updateMechanics(event.mechanics, reload.mechanicsModifier)

            reload.reloadDuration?.let { event.reloadTime = it.apply(event.reloadTime) }
            reload.ammoPerReload?.let { event.reloadAmount = it.apply(event.reloadAmount) }
            reload.magazineSize?.let { event.magazineSize = it.apply(event.magazineSize) }

            // TODO reload.getShootDelayAfterReload()
        }
    }

    @EventHandler
    fun onScope(event: WeaponScopeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val scope = modifier.scope ?: continue

            event.mechanics = updateMechanics(event.mechanics, scope.mechanicsModifier)

            if (event.scopeType == ScopeType.IN) {
                scope.zoomAmount?.let { event.zoomAmount = it.apply(event.zoomAmount) }
            } else if (event.scopeType == ScopeType.STACK) {
                if (scope.zoomStacking.size > event.zoomStack)
                    scope.zoomStacking[event.zoomStack]?.let { event.zoomAmount = it.apply(event.zoomAmount) }
            }

            // TODO night vision
        }
    }

    @EventHandler
    fun onPrepareShoot(event: PrepareWeaponShootEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val shoot = modifier.shoot ?: continue

            event.shootMechanics = updateMechanics(event.shootMechanics, shoot.mechanicsModifier)

            shoot.projectileAmount?.let { event.projectileAmount = it.apply(event.projectileAmount) }
            shoot.projectileSpeed?.let { event.projectileSpeed = it.apply(event.projectileSpeed) }
        }
    }

    @EventHandler
    fun onShoot(event: WeaponShootEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val projectile = modifier.projectile ?: continue

            // ProjectileSettings
            if (projectile.overrideProjectileSettings != null) {
                event.projectile.projectileSettings = projectile.overrideProjectileSettings
            } else {
                val projectileSettings = event.projectile.projectileSettings
                projectile.gravity?.let { projectileSettings.gravity = it.apply(projectileSettings.gravity) }
                projectile.minimumSpeed?.let { projectileSettings.minimumSpeed = it.apply(projectileSettings.minimumSpeed) }
                projectile.maximumSpeed?.let { projectileSettings.maximumSpeed = it.apply(projectileSettings.maximumSpeed) }
                projectile.decrease?.let { projectileSettings.decrease = it.apply(projectileSettings.decrease) }
                projectile.decreaseInWater?.let { projectileSettings.decreaseInWater = it.apply(projectileSettings.decreaseInWater) }
                projectile.decreaseWhenRainingOrSnowing?.let { projectileSettings.decreaseWhenRainingOrSnowing = it.apply(projectileSettings.decreaseWhenRainingOrSnowing) }
                projectile.maxAliveTicks?.let { projectileSettings.maximumAliveTicks = it.apply(projectileSettings.maximumAliveTicks) }
            }

            // Sticky
            if (projectile.overrideSticky != null) {
                event.projectile.sticky = projectile.overrideSticky
            }

            // Through
            if (projectile.overrideThrough != null) {
                event.projectile.through = projectile.overrideThrough
            } else {
                val through = event.projectile.through
                if (through != null) {
                    projectile.maximumThroughAmount?.let { through.maximumThroughAmount = it.apply(through.maximumThroughAmount) }
                }
            }

            // Bouncy
            if (projectile.overrideBouncy != null) {
                event.projectile.bouncy = projectile.overrideBouncy
            } else {
                val bouncy = event.projectile.bouncy
                if (bouncy != null) {
                    projectile.maximumBounceAmount?.let { bouncy.maximumBounceAmount = it.apply(bouncy.maximumBounceAmount) }
                }
            }
        }
    }

    companion object {
        fun updateMechanics(currentMechanics: Mechanics?, modifier: MechanicsModifier?): Mechanics? {
            if (currentMechanics == null) {
                return modifier?.mechanics
            } else if (modifier != null) {
                if (modifier.isReplace) {
                    return modifier.mechanics
                } else {
                    currentMechanics.addDirty(modifier.mechanics.mechanics)
                }
            }

            return currentMechanics
        }
    }
}