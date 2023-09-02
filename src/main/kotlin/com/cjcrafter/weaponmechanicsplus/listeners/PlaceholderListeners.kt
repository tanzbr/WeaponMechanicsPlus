package com.cjcrafter.weaponmechanicsplus.listeners

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import com.cjcrafter.weaponmechanicsplus.placeholders.EnumPlaceholderFormat
import com.cjcrafter.weaponmechanicsplus.placeholders.ListPlaceholderFormat
import com.cjcrafter.weaponmechanicsplus.placeholders.NumericPlaceholderFormat
import com.cjcrafter.weaponmechanicsplus.placeholders.PlaceholderFormat
import me.deecaad.core.file.BukkitConfig
import me.deecaad.core.file.SerializeData
import me.deecaad.core.placeholder.EnumPlaceholderHandler
import me.deecaad.core.placeholder.ListPlaceholderHandler
import me.deecaad.core.placeholder.NumericPlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderRequestEvent
import me.deecaad.core.utils.FileUtil
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.File

class PlaceholderListeners : Listener {

    private val numerics = HashMap<NumericPlaceholderHandler, PlaceholderFormat<NumericPlaceholderHandler>>()
    private val enums = HashMap<EnumPlaceholderHandler, PlaceholderFormat<EnumPlaceholderHandler>>()
    private val lists = HashMap<ListPlaceholderHandler, PlaceholderFormat<ListPlaceholderHandler>>()

    /**
     * Make sure this is run AFTER the server's first tick. We need all armors
     * and weapons to be serialized since we need to iterate over them.
     */
    init {

        // Register placeholder formats
        val placeholdersFolder = File(WeaponMechanics.getPlugin().dataFolder, "placeholders")
        if (!placeholdersFolder.exists() || (placeholdersFolder.listFiles()?.size ?: 0) == 0)
            FileUtil.copyResourcesTo(javaClass.classLoader.getResource("WeaponMechanics/placeholders"), placeholdersFolder.toPath())

        loadPlaceholders(File(placeholdersFolder, "numeric"), NumericPlaceholderFormat(), numerics)
        loadPlaceholders(File(placeholdersFolder, "enums"), EnumPlaceholderFormat(), enums)
        loadPlaceholders(File(placeholdersFolder, "lists"), ListPlaceholderFormat(), lists)
    }

    private fun <T : PlaceholderHandler> loadPlaceholders(folder: File, serializer: PlaceholderFormat<T>, map: MutableMap<T, PlaceholderFormat<T>>) {
        for (file in folder.listFiles()!!) {
            val placeholderName = file.nameWithoutExtension
            val placeholder = PlaceholderHandler.REGISTRY[placeholderName]
            if (placeholder == null) {
                WeaponMechanicsPlus.getDebug().error("Could not find placeholder associated with $file")
                continue
            } else if (!serializer.clazz.isAssignableFrom(placeholder::class.java)) {
                WeaponMechanicsPlus.getDebug().error("Placeholder $placeholderName is a ${placeholder::class.java} but expected a ${serializer::class.java}")
                continue
            }

            val config = YamlConfiguration.loadConfiguration(file)
            val data = SerializeData(serializer, file, null, BukkitConfig(config))
            val format = serializer.serialize(data)
            map[placeholder as T] = format
        }
    }

    @EventHandler
    fun requestPlaceholders(event: PlaceholderRequestEvent) {
        for (entry in event.placeholders()) {
            val placeholder = PlaceholderHandler.REGISTRY[entry.key]
            val format = when (placeholder) {
                is NumericPlaceholderHandler -> numerics[placeholder]?.format(placeholder, event.placeholderData)
                is EnumPlaceholderHandler -> enums[placeholder]?.format(placeholder, event.placeholderData)
                is ListPlaceholderHandler -> lists[placeholder]?.format(placeholder, event.placeholderData)
                else -> null
            }

            format?.format(placeholder, event.placeholderData)?.let{ event.setPlaceholder(entry.key, it) }

            if (format != null)
                event.setPlaceholder(entry.key, format)
        }
    }
}