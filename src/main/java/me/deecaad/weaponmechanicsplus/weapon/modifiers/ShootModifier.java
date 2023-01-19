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

    @NotNull
    @Override
    public ShootModifier serialize(SerializeData serializeData) throws SerializerException {
        return null;
    }
}
