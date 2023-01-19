package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.Projectile;
import org.jetbrains.annotations.NotNull;

public class ProjectileModifier implements Serializer<ProjectileModifier> {

    private Projectile replaceProjectile;

    /**
     * Default constructor for serializer
     */
    public ProjectileModifier() {
    }

    public ProjectileModifier(Projectile replaceProjectile) {
        this.replaceProjectile = replaceProjectile;
    }

    @NotNull
    @Override
    public ProjectileModifier serialize(SerializeData data) throws SerializerException {
        Projectile replaceProjectile = data.of("Set_Projectile").serialize(Projectile.class);

        return new ProjectileModifier(replaceProjectile);
    }
}
