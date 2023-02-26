package me.deecaad.weaponmechanicsplus.weapon.listeners

import me.deecaad.weaponmechanics.weapon.explode.Explosion
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.Projectile
import me.deecaad.weaponmechanics.weapon.weaponevents.*
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponScopeEvent.ScopeType
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class AttachmentListeners : Listener {

    @EventHandler
    fun onDamage(event: WeaponDamageEntityEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val damage = modifier.damageModifier
            event.armorDamage = damage.armorDamage.apply(event.armorDamage)
            event.baseDamage = damage.damage.apply(event.baseDamage)
            event.fireTicks = damage.fireTicks.apply(event.fireTicks)

            // todo damage dropoff, explosion
        }
    }

    @EventHandler
    fun onExplode(event: ProjectilePreExplodeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        var overrideExplosion: Explosion? = null
        for (modifier in modifiers) {
            if (modifier.explosionModifier.overrideExplosion != null) {
                overrideExplosion = modifier.explosionModifier.overrideExplosion
            }
        }

        // Only override explosion if at least 1 of the attachments tried
        if (overrideExplosion != null) event.explosion = overrideExplosion
    }

    @EventHandler
    fun onReload(event: WeaponReloadEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val reload = modifier.reloadModifier
            event.reloadTime = reload.reloadDuration.apply(event.reloadTime)
            event.reloadAmount = reload.ammoPerReload.apply(event.reloadAmount)
            event.magazineSize = reload.magazineSize.apply(event.magazineSize)

            // TODO reload.getShootDelayAfterReload()
        }
    }

    @EventHandler
    fun onScope(event: WeaponScopeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val scope = modifier.scopeModifier
            val scopeType = event.scopeType
            if (Objects.requireNonNull(scopeType) == ScopeType.IN) {
                event.zoomAmount = scope.zoomAmount.apply(event.zoomAmount)
            } else if (scopeType == ScopeType.STACK) {
                if (scope.zoomStacking.size > event.zoomStack) event.zoomAmount =
                    scope.zoomStacking[event.zoomStack].apply(event.zoomAmount)
            }

            // TODO night vision
        }
    }

    @EventHandler
    fun onShoot(event: WeaponShootEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        var overrideProjectile: Projectile? = null
        for (modifier in modifiers) {
            if (modifier.shootModifier.overrideProjectile != null) {
                overrideProjectile = modifier.shootModifier.overrideProjectile
            }
        }

        // TODO set projectile
    }
}