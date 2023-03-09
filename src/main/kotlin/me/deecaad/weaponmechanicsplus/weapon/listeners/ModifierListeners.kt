package me.deecaad.weaponmechanicsplus.weapon.listeners

import me.deecaad.weaponmechanics.weapon.weaponevents.*
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponScopeEvent.ScopeType
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ModifierListeners : Listener {

    @EventHandler
    fun onDamage(event: WeaponDamageEntityEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val damage = modifier.damageModifier ?: continue

            damage.armorDamage?.let { event.armorDamage = it.apply(event.armorDamage) }
            damage.baseDamage?.let { event.baseDamage = it.apply(event.baseDamage) }
            damage.fireTicks?.let { event.fireTicks = it.apply(event.fireTicks) }

            // todo damage dropoff, explosion
        }
    }

    @EventHandler
    fun onExplode(event: ProjectilePreExplodeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack ?: return)
        for (modifier in modifiers) {
            val explosion = modifier.explosionModifier ?: continue

            explosion.overrideExplosion?.let { event.explosion = explosion.overrideExplosion }
        }
    }

    @EventHandler
    fun onReload(event: WeaponReloadEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val reload = modifier.reloadModifier ?: continue

            reload.reloadDuration?.let { event.reloadTime = it.apply(event.reloadAmount) }
            reload.ammoPerReload?.let { event.reloadAmount = it.apply(event.reloadAmount) }
            reload.magazineSize?.let { event.magazineSize = it.apply(event.magazineSize) }

            // TODO reload.getShootDelayAfterReload()
        }
    }

    @EventHandler
    fun onScope(event: WeaponScopeEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val scope = modifier.scopeModifier ?: continue

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
    fun onShoot(event: WeaponShootEvent) {
        val modifiers = WeaponMechanicsPlusAPI.getModifiers(event.weaponStack)
        for (modifier in modifiers) {
            val shoot = modifier.shootModifier ?: continue

            //event.projectile = shoot.overrideProjectile?.create() ?: event.projectile
        }

        // TODO set projectile
    }
}