package me.deecaad.weaponmechanicsplus;

import me.deecaad.core.events.QueueSerializerEvent;
import me.deecaad.core.file.SerializerInstancer;
import me.deecaad.core.file.TaskChain;
import me.deecaad.core.utils.Debugger;
import me.deecaad.core.utils.FileUtil;
import me.deecaad.core.utils.LogLevel;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.lib.auto.UpdateChecker;
import me.deecaad.weaponmechanics.lib.auto.UpdateInfo;
import me.deecaad.weaponmechanics.lib.bstats.bukkit.Metrics;
import me.deecaad.weaponmechanics.weapon.WeaponHandler;
import me.deecaad.weaponmechanics.weapon.projectile.ProjectilesRunnable;
import me.deecaad.weaponmechanicsplus.weapon.firemode.FireModeTriggerListener;
import me.deecaad.weaponmechanicsplus.weapon.listeners.AttachmentListeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class WeaponMechanicsPlus {

    private static WeaponMechanicsPlus plugin;

    private WeaponMechanicsPlusLoader javaPlugin;
    private UpdateChecker update;
    private Metrics metrics;
    private Debugger debug;

    WeaponMechanicsPlus(WeaponMechanicsPlusLoader javaPlugin) {
        this.javaPlugin = javaPlugin;

        plugin = this;
    }

    public void onLoad() {
        int level = getConfig().getInt("Debug_Level", 2);
        boolean printTraces = getConfig().getBoolean("Print_Traces", false);
        debug = new Debugger(getLogger(), level, printTraces);
    }

    public void onEnable() {
        writeFiles();
        registerDebugger();
        registerUpdateChecker();
        registerBStats();
        registerSerializerQueue();
    }

    public void onDisable() {
    }

    public FileConfiguration getConfig() {
        return javaPlugin.getConfig();
    }

    public Logger getLogger() {
        return javaPlugin.getLogger();
    }

    public File getDataFolder() {
        return javaPlugin.getDataFolder();
    }

    public ClassLoader getClassLoader() {
        return javaPlugin.getClassLoader0();
    }

    public File getFile() {
        return javaPlugin.getFile0();
    }

    public static Debugger getDebug() {
        return plugin.debug;
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
        debug.start(javaPlugin);
    }

    private void registerUpdateChecker() {

        // TODO WHEN APPROVED CHANGE ID
        boolean todoWaiting = true;
        if (todoWaiting) return;

        if (!getConfig().getBoolean("Update_Checker.Enabled", true)) return;

        update = new UpdateChecker(javaPlugin, UpdateChecker.spigot(1, "WeaponMechanicsPlus"));
        Listener listener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                if (event.getPlayer().isOp()) {
                    new TaskChain(javaPlugin)
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

        Bukkit.getPluginManager().registerEvents(listener, javaPlugin);
    }

    private void registerBStats() {
        if (metrics != null) return;

        debug.debug("Registering bStats");

        // See https://bstats.org/plugin/bukkit/WeaponMechanicsPlus/16382. This is
        // the bStats plugin id used to track information.
        int id = 16382;

        this.metrics = new Metrics(javaPlugin, id);
    }

    private void registerSerializerQueue() {
        Listener listener = new Listener() {
            @EventHandler
            public void onJoin(QueueSerializerEvent event) {
                // Perfect place to register all things to WM ;D
                if (!event.getSourceName().equals("WeaponMechanics")) return;

                // Register serializers
                try {
                    event.addSerializers(new SerializerInstancer(new JarFile(getFile())).createAllInstances(getClassLoader()));
                } catch (IOException e) {
                    debug.log(LogLevel.WARN, "Failed to add serializers...", e);
                }

                // Register trigger listeners
                WeaponHandler weaponHandler = WeaponMechanics.getWeaponHandler();
                weaponHandler.addTriggerListener(new FireModeTriggerListener());

                // Register projectile script manager
                ProjectilesRunnable projectilesRunnable = WeaponMechanics.getProjectilesRunnable();
                projectilesRunnable.addScriptManager(new PlusScriptManager(javaPlugin));
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, javaPlugin);
        Bukkit.getPluginManager().registerEvents(new AttachmentListeners(), javaPlugin);
    }

    public static Plugin getPlugin() {
        return plugin.javaPlugin;
    }
}