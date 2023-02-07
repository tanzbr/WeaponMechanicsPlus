package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.core.file.SerializerOptionsException;
import me.deecaad.weaponmechanics.WeaponMechanics;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModifierBase implements Serializer<ModifierBase> {

    protected int priority;
    protected Modifier modifier;
    protected Map<String, Modifier> perWeaponModifiers;

    /**
     * Default constructor for serializer.
     */
    public ModifierBase() {
    }

    public ModifierBase(int priority, Modifier modifier, Map<String, Modifier> perWeaponModifiers) {
        this.priority = priority;
        this.modifier = modifier;
        this.perWeaponModifiers = perWeaponModifiers;
    }

    public int getPriority() {
        return priority;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public Map<String, Modifier> getPerWeaponModifiers() {
        return perWeaponModifiers;
    }

    public Modifier getModifier(String weaponTitle) {
        return perWeaponModifiers.getOrDefault(weaponTitle, modifier);
    }

    @NotNull
    @Override
    public ModifierBase serialize(SerializeData data) throws SerializerException {
        int priority = data.of("Priority").getInt(0); // can be negative
        Modifier modifier = data.of("Modifiers").assertExists().serialize(Modifier.class);

        Map<String, Modifier> perWeaponModifiers = new HashMap<>();
        if (data.has("Per_Weapon_Modifiers")) {
            ConfigurationSection config = data.of("Per_Weapon_Modifiers").assertExists().assertType(ConfigurationSection.class).get();
            for (String key : config.getKeys(false)) {

                List<String> options = WeaponMechanics.getWeaponHandler().getInfoHandler().getSortedWeaponList();
                if (!options.contains(key))
                    throw new SerializerOptionsException(data.serializer, "Weapon Title", options, key, data.of(key).getLocation());

                Modifier temp = data.of("Per_Weapon_Modifiers." + key).serialize(Modifier.class);
                perWeaponModifiers.put(key, temp);
            }
        }

        return new ModifierBase(priority, modifier, perWeaponModifiers) {};
    }
}