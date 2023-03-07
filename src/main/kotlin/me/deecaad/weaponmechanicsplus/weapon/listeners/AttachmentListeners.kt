package me.deecaad.weaponmechanicsplus.weapon.listeners

import me.deecaad.weaponmechanics.weapon.weaponevents.*
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponScopeEvent.ScopeType
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AttachmentListeners : Listener {

    @EventHandler
    fun onDamage(event: WeaponDamageEntityEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val damage = modifier.damageModifier ?: continue

            event.armorDamage = damage.armorDamage?.apply(event.armorDamage) ?: event.armorDamage
            event.baseDamage = damage.damage?.apply(event.baseDamage) ?: event.baseDamage
            event.fireTicks = damage.fireTicks?.apply(event.fireTicks) ?: event.fireTicks

            // todo damage dropoff, explosion
        }
    }

    @EventHandler
    fun onExplode(event: ProjectilePreExplodeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack ?: return)
        for (modifier in modifiers) {
            val explosion = modifier.explosionModifier ?: continue

            event.explosion = explosion.overrideExplosion ?: event.explosion
        }
    }

    @EventHandler
    fun onReload(event: WeaponReloadEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val reload = modifier.reloadModifier ?: continue

            event.reloadTime = reload.reloadDuration?.apply(event.reloadTime) ?: event.reloadTime
            event.reloadAmount = reload.ammoPerReload?.apply(event.reloadAmount) ?: event.reloadAmount
            event.magazineSize = reload.magazineSize?.apply(event.magazineSize) ?: event.magazineSize

            // TODO reload.getShootDelayAfterReload()
        }
    }

    @EventHandler
    fun onScope(event: WeaponScopeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val scope = modifier.scopeModifier ?: continue

            if (event.scopeType == ScopeType.IN) {
                event.zoomAmount = scope.zoomAmount?.apply(event.zoomAmount) ?: event.zoomAmount
            } else if (event.scopeType == ScopeType.STACK) {
                if (scope.zoomStacking.size > event.zoomStack)
                    event.zoomAmount = scope.zoomStacking[event.zoomStack]?.apply(event.zoomAmount) ?: event.zoomAmount
            }

            // TODO night vision
        }
    }

    @EventHandler
    fun onShoot(event: WeaponShootEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val shoot = modifier.shootModifier ?: continue

            //event.projectile = shoot.overrideProjectile?.create() ?: event.projectile
        }

        // TODO set projectile
    }
}