package me.deecaad.weaponmechanicsplus

import me.deecaad.weaponmechanics.utils.CustomTag
import me.deecaad.weaponmechanicsplus.weapon.modifiers.*
import me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments.Attachment
import me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * This utility class contains static methods to facilitate getting a weapon's
 * modifiers. These methods are designed to be *as fast as possible* for
 * repeated usage.
 */
object WeaponMechanicsPlusAPI {

    /**
     * "What problem does this solve?" you may ask. Well, we want the result
     * of [.getModifiers] to be sorted by
     * [ModifierBase.priority]. Sorting methods are slow, so instead
     * of sorting a list multiple times every tick, we store the result. This
     * way, the only expense is in the triplet instantiation instead of sorting.
     */
    private val CACHE: MutableMap<Triple<List<String>, String, String>, List<Modifier>> = HashMap()

    /**
     * Returns an array of all attachments currently attached to the gun. Note
     * that if you are looking to use the [Modifier] from the attachment,
     * you should use [WeaponMechanicsPlusAPI.getModifiers] instead. You should
     * check [ItemStack.hasItemMeta] before calling this method.
     *
     * The order of the array is in increasing attachment priority.
     *
     * @param weapon The non-null weapon to get attachments from.
     * @return The array of attachments, or null.
     */
    fun getAttachments(weapon: ItemStack?): Array<Attachment>? {
        val attachmentIds = CustomTag.ATTACHMENTS.getStringArray(weapon)
        if (attachmentIds.isEmpty()) return null

        // Get the attachment config information from each attachment id
        val size = attachmentIds.size
        val attachments = ArrayList<Attachment>(size)
        for (i in 0 until size) {
            val temp = AttachmentRegistry.INSTANCE[attachmentIds[i]]
            if (temp == null) {
                WeaponMechanicsPlus.getDebug().warn("Found deleted attachment ${attachmentIds[i]} on $weapon")
                continue
            }
            attachments.add(temp)
        }

        return attachments.toTypedArray()
    }

    /**
     * Returns an immutable list of all modifiers currently attached to the gun.
     * This includes modifiers from any [Attachment] and any [AmmoTypeModifier].
     *
     * @param weapon The non-null weapon to get modifiers from.
     * @param entity The nullable entity using the weapon.
     * @return The array of modifiers, or null.
     */
    fun getModifiers(weapon: ItemStack, entity: LivingEntity? = null): List<Modifier> {
        val weaponTitle = CustomTag.WEAPON_TITLE.getString(weapon)
        val attachmentIds = CustomTag.ATTACHMENTS.getStringArray(weapon)
        val ammo: String? = null
        // todo account for ammo modifiers

        val size = attachmentIds.size
        val modifiers = ArrayList<Modifier>(size)

        for (i in 0 until size) {
            val attachment = AttachmentRegistry.INSTANCE[attachmentIds[i]]

            // Happens when the server admin deletes/changes the name of an
            // attachment in config, but some players still have it.
            if (attachment == null) {
                WeaponMechanicsPlus.getDebug().warn("Attachment '${attachmentIds[i]}' no longer exists in config. (getModifiers)")
                continue
            }

            modifiers.add(attachment.getModifier(weaponTitle))
        }
        return modifiers
    }
}