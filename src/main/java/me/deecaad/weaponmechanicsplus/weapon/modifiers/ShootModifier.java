package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanics.weapon.projectile.weaponprojectile.Projectile;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.ModifierHelper;
import org.jetbrains.annotations.NotNull;

public class ShootModifier implements Serializer<ShootModifier>, ModifierHelper {

    private Projectile overrideProjectile;

    /**
     * Default constructor for serializer.
     */
    public ShootModifier() {
    }

    public ShootModifier(Projectile overrideProjectile) {
        this.overrideProjectile = overrideProjectile;
    }

    public Projectile getOverrideProjectile() {
        return overrideProjectile;
    }

    @NotNull
    @Override
    public ShootModifier serialize(SerializeData data) throws SerializerException {
        Projectile overrideProjectile = data.of("Override_Projectile").serialize(Projectile.class);
        return new ShootModifier(overrideProjectile);
    }
}
