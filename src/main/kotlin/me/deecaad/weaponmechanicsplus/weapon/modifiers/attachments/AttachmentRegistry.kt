package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments

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

    private val byTitle: MutableMap<String, Attachment>

    init {
        byTitle = LinkedHashMap()
    }

    fun has(attachment: Attachment): Boolean {
        return byTitle.containsKey(attachment.attachmentTitle)
    }

    fun add(attachment: Attachment) {

        // Duplicate attachments in config
        val title = attachment.attachmentTitle
        if (byTitle.containsKey(title))
            throw IllegalArgumentException("Duplicate attachment")

        byTitle[attachment.attachmentTitle] = attachment
    }

    fun clear() {
        byTitle.clear()
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