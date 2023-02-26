package me.deecaad.weaponmechanicsplus.weapon.listeners;

import me.deecaad.weaponmechanics.weapon.explode.Explosion;
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.Projectile;
import me.deecaad.weaponmechanics.weapon.weaponevents.*;
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.DamageModifier;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.Modifier;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ReloadModifier;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ScopeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class AttachmentListeners implements Listener {

    @EventHandler
    public void onDamage(WeaponDamageEntityEvent event) {
        List<Modifier> modifiers = WeaponMechanicsPlusAPI.getModifiers(event.getWeaponStack());

        for (Modifier modifier : modifiers) {
            DamageModifier damage = modifier.getDamageModifier();
            event.setArmorDamage(damage.getArmorDamage().apply(event.getArmorDamage()));
            event.setBaseDamage(damage.getDamage().apply(event.getBaseDamage()));
            event.setFireTicks(damage.getFireTicks().apply(event.getFireTicks()));

            // todo damage dropoff, explosion
        }
    }

    @EventHandler
    public void onExplode(ProjectilePreExplodeEvent event) {
        List<Modifier> modifiers = WeaponMechanicsPlusAPI.getModifiers(event.getWeaponStack());

        Explosion overrideExplosion = null;
        for (Modifier modifier : modifiers) {
            if (modifier.getExplosionModifier().getOverrideExplosion() != null) {
                overrideExplosion = modifier.getExplosionModifier().getOverrideExplosion();
            }
        }

        // Only override explosion if at least 1 of the attachments tried
        if (overrideExplosion != null)
            event.setExplosion(overrideExplosion);
    }

    @EventHandler
    public void onReload(WeaponReloadEvent event) {
        List<Modifier> modifiers = WeaponMechanicsPlusAPI.getModifiers(event.getWeaponStack());

        for (Modifier modifier : modifiers) {
            ReloadModifier reload = modifier.getReloadModifier();
            event.setReloadTime(reload.getReloadDuration().apply(event.getReloadTime()));
            event.setReloadAmount(reload.getAmmoPerReload().apply(event.getReloadAmount()));
            event.setMagazineSize(reload.getMagazineSize().apply(event.getMagazineSize()));

            // TODO reload.getShootDelayAfterReload()
        }
    }

    @EventHandler
    public void onScope(WeaponScopeEvent event) {
        List<Modifier> modifiers = WeaponMechanicsPlusAPI.getModifiers(event.getWeaponStack());

        for (Modifier modifier : modifiers) {
            ScopeModifier scope = modifier.getScopeModifier();

            WeaponScopeEvent.ScopeType scopeType = event.getScopeType();
            if (Objects.requireNonNull(scopeType) == WeaponScopeEvent.ScopeType.IN) {
                event.setZoomAmount(scope.getZoomAmount().apply(event.getZoomAmount()));
            } else if (scopeType == WeaponScopeEvent.ScopeType.STACK) {
                if (scope.getZoomStacking().size() > event.getZoomStack())
                    event.setZoomAmount(scope.getZoomStacking().get(event.getZoomStack()).apply(event.getZoomAmount()));
            }

            // TODO night vision
        }
    }

    @EventHandler
    public void onShoot(WeaponShootEvent event) {
        List<Modifier> modifiers = WeaponMechanicsPlusAPI.getModifiers(event.getWeaponStack());

        Projectile overrideProjectile = null;
        for (Modifier modifier : modifiers) {
            if (modifier.getShootModifier().getOverrideProjectile() != null) {
                overrideProjectile = modifier.getShootModifier().getOverrideProjectile();
            }
        }

        // TODO set projectile
    }
}
