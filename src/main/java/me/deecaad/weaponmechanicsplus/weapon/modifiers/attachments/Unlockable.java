package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanics.weapon.stats.WeaponStat;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Unlockable implements Serializer<Unlockable> {

    private ItemStack itemStack;
    private int experience;
    private Map<WeaponStat, Number> stats;

    @NotNull
    @Override
    public Unlockable serialize(SerializeData data) throws SerializerException {
        return null;
    }
}