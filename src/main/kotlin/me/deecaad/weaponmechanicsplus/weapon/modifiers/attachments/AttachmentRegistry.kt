package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlus
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.lang.IllegalArgumentException

/**
 * Stores all registered attachments by both name and id. Since
 * [org.bukkit.persistence.PersistentDataType] does not have a wrapper
 * for `String[]`, we have to use an `int[]` to store
 * attachments in items. So instead of storing the "title" of the attachment,
 * we created an arbitrary "id" and store that instead.
 *
 * <p>In order to make sure these numeric ids remain constant between server
 * restarts, we have to save them to a file.
 *
 * @see me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI.getAttachments
 * @see me.deecaad.weaponmechanics.utils.CustomTag.ATTACHMENTS
 */
class AttachmentRegistry private constructor() : Iterable<Attachment> {

    private val file: File = File(WeaponMechanicsPlus.getPlugin().dataFolder, "do-not-edit-me-attachments.yml")
    private val config: FileConfiguration
    private val byTitle: MutableMap<String, Attachment>
    private val byId: BiMap<Int, String>

    init {
        config = YamlConfiguration.loadConfiguration(file)
        byTitle = LinkedHashMap()
        byId = HashBiMap.create()

        // Cache all the title->id pairs
        for (key in config.getKeys(false)) {
            val id = config.getInt(key, -1)
            if (id == -1)
                throw IllegalArgumentException("Corrupted do-not-edit-me-attachments.yml file")

            byId[id] = key
        }
    }

    fun has(attachment: Attachment): Boolean {
        return byTitle.containsKey(attachment.attachmentTitle)
    }

    fun add(attachment: Attachment) {

        // Duplicate attachments in config
        val title = attachment.attachmentTitle
        if (byTitle.containsKey(title))
            throw IllegalArgumentException("Duplicate attachment")

        // Reuse the ID stored in the file... Otherwise find a new id and save
        // it to file for persistence between restarts. Adding attachments should
        // only happen during serialization, so repeated saving should be fine.
        val id = byId.inverse().getOrPut(title) { byId.size }
        config.set(title, id)
        config.save(file)

        byTitle[attachment.attachmentTitle] = attachment
    }

    fun getId(attachment: Attachment): Int {
        return byId.inverse()[attachment.attachmentTitle] ?: throw IllegalArgumentException("Unknown attachment $attachment... Was it added?")
    }

    operator fun get(id: Int): Attachment? {
        return get(byId[id] ?: return null)
    }

    operator fun get(title: String): Attachment? {
        return byTitle[title]
    }

    override fun iterator(): Iterator<Attachment> {
        return byTitle.values.iterator()
    }

    companion object {
        val INSTANCE = AttachmentRegistry()
    }
}