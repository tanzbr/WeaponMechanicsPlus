package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanics.weapon.explode.Explosion;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.ModifierHelper;
import org.jetbrains.annotations.NotNull;

public class ExplosionModifier implements Serializer<ExplosionModifier>, ModifierHelper {

    private Explosion overrideExplosion;

    /**
     * Default constructor for serializer
     */
    public ExplosionModifier() {
    }

    public ExplosionModifier(Explosion overrideExplosion) {
        this.overrideExplosion = overrideExplosion;
    }

    @NotNull
    @Override
    public ExplosionModifier serialize(SerializeData data) throws SerializerException {
        Explosion overrideExplosion = data.of("Override_Explosion").serialize(Explosion.class);

        return new ExplosionModifier(overrideExplosion);
    }
}
