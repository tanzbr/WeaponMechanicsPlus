package me.deecaad.weaponmechanicsplus;

import me.deecaad.weaponmechanics.lib.auto.AutoMechanicsDownload;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class WeaponMechanicsPlusLoader extends JavaPlugin {

    private WeaponMechanicsPlus plugin;
    private boolean success;

    @Override
    public void onLoad() {

        // Attempt to automatically download MechanicsCore and WeaponMechanics.
        if (getConfig().getBoolean("Auto_Download.Enabled", true)) {
            AutoMechanicsDownload auto = new AutoMechanicsDownload(getConfig());
            auto.MECHANICS_CORE.install();
            auto.WEAPON_MECHANICS.install();
        }

        // Don't enable the plugin if either dependencies are absent
        if (Bukkit.getPluginManager().getPlugin("MechanicsCore") == null) {
            System.out.println("CORE");
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("WeaponMechanics") == null) {
            System.out.println("WEAPON");
            return;
        }

        plugin = new WeaponMechanicsPlus(this);
        plugin.onLoad();
        success = true;
    }

    @Override
    public void onEnable() {
        if (!success) {
            getLogger().log(Level.SEVERE, "");
            getLogger().log(Level.SEVERE, " !!! MechanicsCore and/or WeaponMechanics was missing");
            getLogger().log(Level.SEVERE, " !!! Download them here: https://www.spigotmc.org/resources/99913/");
            getLogger().log(Level.SEVERE, "");
            return;
        }

        plugin.onEnable();
    }

    @Override
    public void onDisable() {
        if (!success)
            return;

        plugin.onDisable();
        success = false;
    }

    ClassLoader getClassLoader0() {
        return getClassLoader();
    }

    File getFile0() {
        return getFile();
    }
}