package me.deecaad.weaponmechanicsplus

import me.deecaad.core.events.QueueSerializerEvent
import me.deecaad.core.file.SerializerInstancer
import me.deecaad.core.file.TaskChain
import me.deecaad.core.utils.Debugger
import me.deecaad.core.utils.FileUtil
import me.deecaad.core.utils.LogLevel
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.lib.auto.UpdateChecker
import me.deecaad.weaponmechanics.lib.auto.UpdateInfo
import me.deecaad.weaponmechanics.lib.bstats.bukkit.Metrics
import me.deecaad.weaponmechanicsplus.weapon.firemode.FireModeTriggerListener
import me.deecaad.weaponmechanicsplus.weapon.listeners.AttachmentListeners
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.util.jar.JarFile
import java.util.logging.Logger

class WeaponMechanicsPlus internal constructor(private val javaPlugin: WeaponMechanicsPlusLoader) {

    private lateinit var update: UpdateChecker
    private lateinit var metrics: Metrics
    private lateinit var debug: Debugger

    val config: FileConfiguration
        get() = javaPlugin.config
    val logger: Logger
        get() = javaPlugin.logger
    val dataFolder: File
        get() = javaPlugin.dataFolder
    val classLoader: ClassLoader
        get() = javaPlugin.classLoader0
    val file: File
        get() = javaPlugin.file0

    init {
        plugin = this
    }

    fun onLoad() {
        val level = config.getInt("Debug_Level", 2)
        val printTraces = config.getBoolean("Print_Traces", false)
        debug = Debugger(logger, level, printTraces)
    }

    fun onEnable() {
        writeFiles()
        registerDebugger()
        registerUpdateChecker()
        registerBStats()
        registerSerializerQueue()
    }

    fun onDisable() {}

    private fun writeFiles() {
        // Create files
        if (!dataFolder.exists() || dataFolder.listFiles() == null || dataFolder.listFiles()?.size == 0) {
            debug.info("Copying files from jar (This process may take up to 30 seconds during the first load!)")
            FileUtil.copyResourcesTo(classLoader.getResource("WeaponMechanicsPlus"), dataFolder.toPath())
        }
    }

    private fun registerDebugger() {
        debug.permission = "weaponmechanicsplus.errorlog"
        debug.msg = "WeaponMechanicsPlus had %s error(s) in console."
        debug.start(javaPlugin)
    }

    private fun registerUpdateChecker() {

        // TODO WHEN APPROVED CHANGE ID
        val todoWaiting = true
        if (todoWaiting) return
        if (!config.getBoolean("Update_Checker.Enabled", true)) return
        update = UpdateChecker(javaPlugin, UpdateChecker.spigot(1, "WeaponMechanicsPlus"))
        val listener: Listener = object : Listener {
            @EventHandler
            fun onJoin(event: PlayerJoinEvent) {
                if (event.player.isOp) {
                    TaskChain(javaPlugin)
                        .thenRunAsync { callback: Any? -> update!!.hasUpdate() }
                        .thenRunSync { callback: Any? ->
                            val update = callback as UpdateInfo?
                            if (callback != null) event.player.sendMessage(ChatColor.RED.toString() + "WeaponMechanicsPlus is out of date! " + update!!.current + " -> " + update.newest)
                            null
                        }
                }
            }
        }
        Bukkit.getPluginManager().registerEvents(listener, javaPlugin)
    }

    private fun registerBStats() {
        if (metrics != null) return
        debug!!.debug("Registering bStats")

        // See https://bstats.org/plugin/bukkit/WeaponMechanicsPlus/16382. This is
        // the bStats plugin id used to track information.
        val id = 16382
        metrics = Metrics(javaPlugin, id)
    }

    private fun registerSerializerQueue() {
        val listener = object : Listener {
            @EventHandler
            fun onJoin(event: QueueSerializerEvent) {
                // Perfect place to register all things to WM ;D
                if (event.sourceName != "WeaponMechanics") return

                // Register serializers
                try {
                    event.addSerializers(SerializerInstancer(JarFile(file)).createAllInstances(classLoader))
                } catch (e: IOException) {
                    debug!!.log(LogLevel.WARN, "Failed to add serializers...", e)
                }

                // Register trigger listeners
                val weaponHandler = WeaponMechanics.getWeaponHandler()
                weaponHandler.addTriggerListener(FireModeTriggerListener())

                // Register projectile script manager
                val projectilesRunnable = WeaponMechanics.getProjectilesRunnable()
                projectilesRunnable.addScriptManager(PlusScriptManager(javaPlugin))
            }
        }
        Bukkit.getPluginManager().registerEvents(listener, javaPlugin)
        Bukkit.getPluginManager().registerEvents(AttachmentListeners(), javaPlugin)
    }

    companion object {
        private lateinit var plugin: WeaponMechanicsPlus
        fun getDebug(): Debugger? {
            return plugin.debug
        }

        fun getPlugin(): Plugin {
            return plugin.javaPlugin
        }
    }
}