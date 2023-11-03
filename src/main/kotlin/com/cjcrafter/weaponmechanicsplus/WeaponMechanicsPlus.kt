package com.cjcrafter.weaponmechanicsplus

import me.deecaad.core.events.QueueSerializerEvent
import me.deecaad.core.file.SerializerInstancer
import me.deecaad.core.file.TaskChain
import me.deecaad.core.utils.Debugger
import me.deecaad.core.utils.FileUtil
import me.deecaad.core.utils.LogLevel
import me.deecaad.core.utils.ReflectionUtil
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.lib.auto.UpdateChecker
import me.deecaad.weaponmechanics.lib.auto.UpdateInfo
import me.deecaad.weaponmechanics.lib.bstats.bukkit.Metrics
import com.cjcrafter.weaponmechanicsplus.weapon.firemode.FireModeTriggerListener
import com.cjcrafter.weaponmechanicsplus.listeners.AddAttachment
import com.cjcrafter.weaponmechanicsplus.listeners.ArmorModifierListeners
import com.cjcrafter.weaponmechanicsplus.listeners.ModifierListeners
import com.cjcrafter.weaponmechanicsplus.listeners.PlaceholderListeners
import com.cjcrafter.weaponmechanicsplus.placeholders.ArmorMechanicsPlaceholderListener
import com.cjcrafter.weaponmechanicsplus.placeholders.WeaponMechanicsPlaceholderListener
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.io.IOException
import java.util.jar.JarFile
import java.util.logging.Logger

class WeaponMechanicsPlus internal constructor(private val javaPlugin: WeaponMechanicsPlusLoader) {

    private lateinit var debug: Debugger
    private var update: UpdateChecker? = null
    private var metrics: Metrics? = null

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

        if (ReflectionUtil.getMCVersion() >= 13)
            Command.register()
    }

    fun onDisable() {
    }

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
        if (true) return

        if (!config.getBoolean("Update_Checker.Enabled", true)) return
        update = UpdateChecker(javaPlugin, UpdateChecker.spigot(1, "WeaponMechanicsPlus"))
        val listener: Listener = object : Listener {
            @EventHandler
            fun onJoin(event: PlayerJoinEvent) {
                if (event.player.isOp) {
                    TaskChain(javaPlugin)
                        .thenRunAsync { callback -> update?.hasUpdate() }
                        .thenRunSync { callback ->
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
        debug.debug("Registering bStats")

        // See https://bstats.org/plugin/bukkit/WeaponMechanicsPlus/16382. This is
        // the bStats plugin id used to track information.
        val id = 16382
        metrics = Metrics(javaPlugin, id)
    }

    private fun registerSerializerQueue() {
        val listener = object : Listener {
            @EventHandler
            fun onConfigSerialize(event: QueueSerializerEvent) {
                // Perfect place to register all things to WM ;D
                if (event.sourceName != "WeaponMechanics") return

                debug.info("Reloading plugin")
                HandlerList.unregisterAll(javaPlugin)
                AttachmentRegistry.INSTANCE.clear()

                // Register serializers
                try {
                    event.addSerializers(SerializerInstancer(JarFile(file)).createAllInstances(classLoader))
                } catch (e: IOException) {
                    debug.log(LogLevel.WARN, "Failed to add serializers...", e)
                }

                // Register trigger listeners
                val weaponHandler = WeaponMechanics.getWeaponHandler()
                weaponHandler.addTriggerListener(FireModeTriggerListener())

                // Register projectile script manager
                val projectilesRunnable = WeaponMechanics.getProjectilesRunnable()
                projectilesRunnable.addScriptManager(ProjectileScriptManager(javaPlugin))

                // Other listeners
                val manager = Bukkit.getPluginManager()
                manager.registerEvents(AddAttachment(), javaPlugin)
                manager.registerEvents(ModifierListeners(), javaPlugin)
                manager.registerEvents(ArmorModifierListeners(), javaPlugin)
                manager.registerEvents(PlaceholderListeners(), javaPlugin)

                // We need a serialized list of weapons, so we run this 5 ticks after server start/reload
                object : BukkitRunnable() {
                    override fun run() {
                        manager.registerEvents(WeaponMechanicsPlaceholderListener(), javaPlugin)
                        if (manager.getPlugin("ArmorMechanics") != null)
                            manager.registerEvents(ArmorMechanicsPlaceholderListener(), javaPlugin)
                    }
                }.runTaskLater(javaPlugin, 5L)

                // Reregister WMP since we removed it earlier in HandlerList.unregisterAll
                registerSerializerQueue()
            }
        }

        Bukkit.getPluginManager().registerEvents(listener, javaPlugin)
    }

    companion object {
        private lateinit var plugin: WeaponMechanicsPlus
        fun getDebug(): Debugger {
            return plugin.debug
        }

        fun getPlugin(): Plugin {
            return plugin.javaPlugin
        }
    }
}