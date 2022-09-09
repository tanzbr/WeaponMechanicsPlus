package me.deecaad.weaponmechanicsplus.weapon.modifiers.ammotype;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ModifierBase;
import org.jetbrains.annotations.NotNull;

public class AmmoTypeModifier extends ModifierBase {

    @Override
    public String getKeyword() {
        return "Ammo_Type_Modifier";
    }

    @NotNull
    @Override
    public AmmoTypeModifier serialize(SerializeData data) throws SerializerException {
        ModifierBase base = super.serialize(data);

        return null;
    }
}