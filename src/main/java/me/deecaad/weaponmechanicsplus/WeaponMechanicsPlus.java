package me.deecaad.weaponmechanicsplus;

import me.deecaad.core.file.Configuration;
import me.deecaad.core.utils.Debugger;
import me.deecaad.core.utils.NumberUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.logging.Logger;

public class WeaponMechanicsPlus {

    private static WeaponMechanicsPlus INSTANCE;
    private WeaponMechanicsPlusLoader plugin;
    private Debugger debug;
    private Configuration basicConfiguration;

    WeaponMechanicsPlus(WeaponMechanicsPlusLoader plugin) {
        this.plugin = plugin;

        INSTANCE = this;
    }

    // * ----- HELPER METHODS ----- * //
    public Logger getLogger() { return plugin.getLogger(); }
    public FileConfiguration getConfig() { return plugin.getConfig(); }
    public File getDataFolder() { return plugin.getDataFolder(); }
    public ClassLoader getClassLoader() { return plugin.getClassLoader0();}
    public File getFile() { return plugin.getFile0(); }



    // * ----- STANDARD PLUGIN METHODS ----- * //
    public void onLoad() {
        //setupDebugger();
    }

    public void onEnable() {
        long millisCurrent = System.currentTimeMillis();
        INSTANCE = this;

        //writeFiles();
        //registerEvents();

        //registerBStats();

        long tookMillis = System.currentTimeMillis() - millisCurrent;
        double seconds = NumberUtil.getAsRounded(tookMillis * 0.001, 2);
        debug.info("Enabled WeaponMechanicsCosmetics in " + seconds + "s");
        debug.start(plugin);
    }

    public void onDisable() {
        HandlerList.unregisterAll(plugin);
    }
}
