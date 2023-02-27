package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments

/**
 * Stores all registered attachments by both name and id. Since
 * [org.bukkit.persistence.PersistentDataType] does not have a wrapper
 * for `String[]`, we have to use an `int[]` to store
 * attachments in items. So instead of storing the "title" of the attachment,
 * we created an arbitrary "id" and store that instead.
 *
 * @see me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI.getAttachments
 * @see me.deecaad.weaponmechanics.utils.CustomTag.ATTACHMENTS
 */
class AttachmentRegistry private constructor() {

    private val byTitle: MutableMap<String?, Attachment>
    private val byId: MutableList<Attachment>

    init {
        byTitle = LinkedHashMap()
        byId = ArrayList()
    }

    fun has(attachment: Attachment): Boolean {
        return byTitle.containsKey(attachment.attachmentTitle)
    }

    fun add(attachment: Attachment) {
        byId.add(attachment)
        byTitle[attachment.attachmentTitle] = attachment
    }

    fun getId(attachment: Attachment): Int {
        val temp = byId.indexOf(attachment)
        require(temp != -1) { "Unknown attachment $attachment... Was it added?" }
        return temp
    }

    operator fun get(id: Int): Attachment {
        return byId[id]
    }

    operator fun get(title: String?): Attachment? {
        return byTitle[title]
    }

    companion object {
        val INSTANCE = AttachmentRegistry()
    }
}