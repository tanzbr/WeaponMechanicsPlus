package com.cjcrafter.weaponmechanicsplus

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

class WeaponMechanicsPlusLoader : JavaPlugin() {

    private lateinit var plugin: WeaponMechanicsPlus
    private var success = false

    val classLoader0: ClassLoader
        get() = classLoader
    val file0: File
        get() = file

    override fun onLoad() {
        // Don't enable the plugin if either dependencies are absent
        if (Bukkit.getPluginManager().getPlugin("MechanicsCore") == null)
            return
        if (Bukkit.getPluginManager().getPlugin("WeaponMechanics") == null)
            return

        plugin = WeaponMechanicsPlus(this)
        plugin.onLoad()
        success = true
    }

    override fun onEnable() {
        if (!success) {
            logger.log(Level.SEVERE, "")
            logger.log(Level.SEVERE, " !!! MechanicsCore and/or WeaponMechanics was missing")
            logger.log(Level.SEVERE, " !!! Download them here: https://www.spigotmc.org/resources/99913/")
            logger.log(Level.SEVERE, "")
            return
        }

        plugin.onEnable()
    }

    override fun onDisable() {
        if (!success)
            return

        plugin.onDisable()
        success = false
    }
}