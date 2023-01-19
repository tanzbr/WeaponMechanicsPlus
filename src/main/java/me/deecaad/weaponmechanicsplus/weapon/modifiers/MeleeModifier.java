package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.ModifierHelper;
import org.jetbrains.annotations.NotNull;

public class MeleeModifier implements Serializer<MeleeModifier>, ModifierHelper {

    /**
     * Default constructor for serializer.
     */
    public MeleeModifier() {
    }


    @NotNull
    @Override
    public MeleeModifier serialize(SerializeData data) throws SerializerException {
        return null;
    }
}
