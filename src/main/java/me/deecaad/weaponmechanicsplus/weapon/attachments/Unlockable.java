package me.deecaad.weaponmechanicsplus.weapon.attachments;

import me.deecaad.weaponmechanics.weapon.stats.WeaponStat;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Unlockable {

    private ItemStack itemStack;
    private String permission;
    private int experience;
    private Map<WeaponStat, Number> stats;

}