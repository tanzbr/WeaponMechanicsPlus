package com.cjcrafter.weaponmechanicsplus

import me.deecaad.weaponmechanics.utils.CustomTag
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.*
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments.Attachment
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

/**
 * This utility class contains static methods to facilitate getting a weapon's
 * modifiers. These methods are designed to be *as fast as possible* for
 * repeated usage.
 */
object WeaponMechanicsPlusAPI {

    fun getAttachments(weapon: ItemStack?): List<Attachment>? {
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

        return attachments
    }

    fun forEachAttachment(item: ItemStack, action: (Attachment) -> Unit) {
        if (!item.hasItemMeta())
            return

        val attachmentIds = CustomTag.ATTACHMENTS.getStringArray(item)
        if (attachmentIds.isEmpty()) return

        // Get the attachment config information from each attachment id
        for (i in attachmentIds.indices) {
            val temp = AttachmentRegistry.INSTANCE[attachmentIds[i]]
            if (temp == null) {
                WeaponMechanicsPlus.getDebug().warn("Found deleted attachment ${attachmentIds[i]} on $item")
                continue
            }

            action(temp)
        }
    }

    fun forEachModifier(entity: LivingEntity, weapon: ItemStack? = null, action: (ModifierBase) -> Unit) {
        // Where can modifiers exist?
        // 1. the attachments on an item the player is holding (weapon)
        // 2. the ammo loaded in the gun a player is holding
        // 3. helmet/chestplate/leggings/boots attachments

        val lists = ArrayList<List<ModifierBase>>()

        // 1. weapon attachments, & 2. weapon ammo
        if (weapon != null) {
            getAttachments(weapon)?.let { lists.add(it) }
            // TODO weapon ammo
        }

        // 3. armor attachments
        val equipment = entity.equipment
        if (equipment != null) {
            equipment.helmet?.let { armor -> getAttachments(armor)?.let { attachments -> lists.add(attachments) } }
            equipment.chestplate?.let { armor -> getAttachments(armor)?.let { attachments -> lists.add(attachments) } }
            equipment.leggings?.let { armor -> getAttachments(armor)?.let { attachments -> lists.add(attachments) } }
            equipment.boots?.let { armor -> getAttachments(armor)?.let { attachments -> lists.add(attachments) } }
        }

        kWayMerge(action, lists)
    }

    private data class Element<T : Comparable<T>>(val value: T, val index: Int, val listIndex: Int) : Comparable<Element<T>> {
        override fun compareTo(other: Element<T>): Int {
            return this.value.compareTo(other.value)
        }
    }

    private fun <T : Comparable<T>> kWayMerge(action: (T) -> Unit, lists: List<List<T>>) {
        val pq = PriorityQueue<Element<T>>()
        for ((listIndex, list) in lists.withIndex()) {
            if (list.isNotEmpty()) {
                pq.offer(Element(list[0], 0, listIndex))
            }
        }

        while (pq.isNotEmpty()) {
            val smallest = pq.poll()
            action(smallest.value)

            val nextIndex = smallest.index + 1
            if (nextIndex < lists[smallest.listIndex].size) {
                pq.offer(Element(lists[smallest.listIndex][nextIndex], nextIndex, smallest.listIndex))
            }
        }
    }
}