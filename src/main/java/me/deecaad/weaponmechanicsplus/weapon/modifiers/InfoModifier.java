package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.ModifierHelper;
import org.jetbrains.annotations.NotNull;

public class InfoModifier implements Serializer<InfoModifier>, ModifierHelper {

    /**
     * Default constructor for serializer.
     */
    public InfoModifier() {
    }

    @NotNull
    @Override
    public InfoModifier serialize(SerializeData serializeData) throws SerializerException {
        return null;
    }
}
