package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ModifierBase implements Serializer<ModifierBase> {

    private Modifier modifier;
    private Map<String, Modifier> perWeaponModifiers;

    @NotNull
    @Override
    public ModifierBase serialize(SerializeData serializeData) throws SerializerException {
        return null;
    }
}