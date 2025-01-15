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
import me.deecaad.core.utils.LogLevel
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.File
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

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
        val placeholdersResource = javaClass.classLoader.getResource("WeaponMechanics/placeholders")!!
        val placeholdersFolder = File(WeaponMechanics.getPlugin().dataFolder, "placeholders")
        if (!placeholdersFolder.exists() || (placeholdersFolder.listFiles()?.size ?: 0) == 0)
            FileUtil.copyResourcesTo(placeholdersResource, placeholdersFolder.toPath())

        // Walk the placeholders folder, and make sure that we set any missing files
        // to their default values.
        val pathReference = FileUtil.PathReference.of(placeholdersResource.toURI())
        Files.walkFileTree(pathReference.path, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                val relativePath = pathReference.path.relativize(file)
                val targetFile = File(placeholdersFolder, relativePath.toString())
                if (!targetFile.exists()) {
                    try {
                        Files.copy(file, targetFile.toPath())
                    } catch (e: Exception) {
                        WeaponMechanicsPlus.getDebug().log(LogLevel.WARN, "Could not copy $file to $targetFile", e)
                    }
                }

                return FileVisitResult.CONTINUE
            }
        })

        loadPlaceholders(File(placeholdersFolder, "numeric"), NumericPlaceholderFormat(), numerics)
        loadPlaceholders(File(placeholdersFolder, "enums"), EnumPlaceholderFormat(), enums)
        loadPlaceholders(File(placeholdersFolder, "lists"), ListPlaceholderFormat(), lists)
    }

    private fun <T : PlaceholderHandler> loadPlaceholders(folder: File, serializer: PlaceholderFormat<T>, map: MutableMap<T, PlaceholderFormat<T>>) {
        if (!folder.exists())
            folder.mkdirs()

        for (file in folder.listFiles()!!) {

            // "link" the name of the file to an existing placeholder. "Optional"
            // addons, like ArmorMechanics, use filenames that start with a '$'.
            var placeholderName = file.nameWithoutExtension
            var requiredPlaceholder = true
            if (placeholderName.startsWith("$")) {
                placeholderName = placeholderName.substring(1)
                requiredPlaceholder = false
            }

            val placeholder = PlaceholderHandler.REGISTRY[placeholderName]
            if (placeholder == null) {
                if (requiredPlaceholder)
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
            val result = when (val placeholder = PlaceholderHandler.REGISTRY[entry.key]) {
                is NumericPlaceholderHandler -> numerics[placeholder]?.format(placeholder, event.placeholderData)
                is EnumPlaceholderHandler -> enums[placeholder]?.format(placeholder, event.placeholderData)
                is ListPlaceholderHandler -> lists[placeholder]?.format(placeholder, event.placeholderData)
                else -> null
            }

            result?.let { event.setPlaceholder(entry.key, it) }
        }
    }
}