package com.cjcrafter.weaponmechanicsplus.listeners

import com.cjcrafter.armormechanics.events.ArmorGenerateEvent
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ArmorGenerateListener : Listener {

    @EventHandler
    fun onWeaponGenerate(event: ArmorGenerateEvent) {
        val attachments: List<String> = event.data["attachments"] as List<String>? ?: listOf()

        for (attachment in attachments) {
            val attachmentInstance = AttachmentRegistry.INSTANCE[attachment]
            if (attachmentInstance == null) {
                event.sender.sendMessage("${ChatColor.RED}Unknown attachment: $attachment")
                continue
            }

            // Admins may bypass the canAttach check via commands here, which is intended.
            // But MAYBE the admin will be confused and think it is a bug, so let's warn them.
            if (!attachmentInstance.canAttach(event.armor, event.armorTitle, isWeapon = false)) {
                event.sender.sendMessage("${ChatColor.YELLOW}$attachment cannot normally be attached, but was attached anyway due to admin permissions.")
            }

            attachmentInstance.attach(event.armor)
        }
    }
}