package me.deecaad.weaponmechanicsplus;

import me.deecaad.weaponmechanics.weapon.projectile.AProjectile;
import me.deecaad.weaponmechanics.weapon.projectile.ProjectileScriptManager;
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.WeaponProjectile;
import me.deecaad.weaponmechanicsplus.weapon.guidedprojectile.GuidedProjectileScript;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

import static me.deecaad.weaponmechanics.WeaponMechanics.getConfigurations;

public class PlusScriptManager extends ProjectileScriptManager {

    public PlusScriptManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void attach(@Nonnull AProjectile aProjectile) {
        if (!(aProjectile instanceof WeaponProjectile projectile)) return;

        if (getConfigurations().containsKey(projectile.getWeaponTitle() + ".Projectile.Guided_Projectile")) {
            projectile.addProjectileScript(new GuidedProjectileScript(getPlugin(), projectile));
        }
    }
}