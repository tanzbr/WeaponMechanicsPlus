package me.deecaad.weaponmechanicsplus;

import me.cjcrafter.auto.UpdateChecker;
import me.cjcrafter.auto.UpdateInfo;
import me.deecaad.core.events.QueueSerializerEvent;
import me.deecaad.core.file.SerializerInstancer;
import me.deecaad.core.file.TaskChain;
import me.deecaad.core.utils.Debugger;
import me.deecaad.core.utils.FileUtil;
import me.deecaad.core.utils.LogLevel;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class WeaponMechanicsPlus {

    private static WeaponMechanicsPlus INSTANCE;

    private WeaponMechanicsPlusLoader plugin;
    private UpdateChecker update;
    private Metrics metrics;
    private Debugger debug;

    WeaponMechanicsPlus(WeaponMechanicsPlusLoader plugin) {
        this.plugin = plugin;

        INSTANCE = this;
    }

    public void onLoad() {
        int level = getConfig().getInt("Debug_Level", 2);
        boolean printTraces = getConfig().getBoolean("Print_Traces", false);
        debug = new Debugger(getLogger(), level, printTraces);
    }

    public void onEnable() {

        //PluginManager pm = plugin.getServer().getPluginManager();
        //pm.registerEvents(new ExplosionEffectSpawner(), plugin);
        //pm.registerEvents(new MuzzleFlashSpawner(), plugin);

        writeFiles();
        registerDebugger();
        registerUpdateChecker();
        registerBStats();
        registerSerializerQueue();
    }

    public void onDisable() {
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    public ClassLoader getClassLoader() {
        return plugin.getClassLoader0();
    }

    public File getFile() {
        return plugin.getFile0();
    }

    public Debugger getDebug() {
        return debug;
    }

    private void writeFiles() {
        // Create files
        if (!getDataFolder().exists() || getDataFolder().listFiles() == null || getDataFolder().listFiles().length == 0) {
            debug.info("Copying files from jar (This process may take up to 30 seconds during the first load!)");
            FileUtil.copyResourcesTo(getClassLoader().getResource("WeaponMechanicsPlus"), getDataFolder().toPath());
        }
    }

    private void registerDebugger() {
        debug.permission = "weaponmechanicsplus.errorlog";
        debug.msg = "WeaponMechanicsPlus had %s error(s) in console.";
        debug.start(plugin);
    }

    private void registerUpdateChecker() {

        // TODO WHEN APPROVED CHANGE ID
        boolean todoWaiting = true;
        if (todoWaiting) return;

        if (!getConfig().getBoolean("Update_Checker.Enabled", true)) return;

        update = new UpdateChecker(plugin, UpdateChecker.spigot(1, "WeaponMechanicsPlus"));
        Listener listener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                if (event.getPlayer().isOp()) {
                    new TaskChain(plugin)
                            .thenRunAsync((callback) -> update.hasUpdate())
                            .thenRunSync((callback) -> {
                                UpdateInfo update = (UpdateInfo) callback;
                                if (callback != null)
                                    event.getPlayer().sendMessage(ChatColor.RED + "WeaponMechanicsPlus is out of date! " + update.current + " -> " + update.newest);

                                return null;
                            });
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    private void registerBStats() {
        if (metrics != null) return;

        debug.debug("Registering bStats");

        // See https://bstats.org/plugin/bukkit/WeaponMechanicsPlus/16382. This is
        // the bStats plugin id used to track information.
        int id = 16382;

        this.metrics = new Metrics(plugin, id);
    }

    private void registerSerializerQueue() {
        Listener listener = new Listener() {
            @EventHandler
            public void onJoin(QueueSerializerEvent event) {
                if (!event.getSourceName().equals("WeaponMechanics")) return;

                try {
                    event.addSerializers(new SerializerInstancer(new JarFile(getFile())).createAllInstances(getClassLoader()));
                } catch (IOException e) {
                    debug.log(LogLevel.WARN, "Failed to add serializers...", e);
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public WeaponMechanicsPlusLoader getPlugin() {
        return plugin;
    }

    public static WeaponMechanicsPlus getInstance() {
        return INSTANCE;
    }
}