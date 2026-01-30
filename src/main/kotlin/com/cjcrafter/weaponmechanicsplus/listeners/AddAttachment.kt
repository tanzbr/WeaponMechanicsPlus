/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.listeners

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments.Attachment
import me.deecaad.core.mechanics.CastData
import me.deecaad.weaponmechanics.utils.CustomTag
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.inventory.PlayerInventory

class AddAttachment : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {

        // Let's only allow attachments in the player's inventory... Hopefully
        // this will avoid most duplication issues with other plugins (like in gui)
        val inventory = event.clickedInventory
        if (inventory !is PlayerInventory)
            return

        // Require a drag and drop action
        val item = inventory.getItem(event.slot)
        val attachmentItem = event.cursor
        if (item == null || attachmentItem == null || !item.hasItemMeta() || !attachmentItem.hasItemMeta())
            return

        // need to drag and drop an attachment onto a weapon
        val weaponTitle = CustomTag.WEAPON_TITLE.getString(item)
        val itemTitle = weaponTitle ?: CustomTag.ARMOR_TITLE.getString(item)
        val attachmentTitle = CustomTag.ATTACHMENT_TITLE.getString(attachmentItem)
        if (itemTitle == null || attachmentTitle == null)
            return

        // Users in creative mode get "item creation privilege" which means that
        // items get duplicated.
        if (event is InventoryCreativeEvent) {
            event.whoClicked.sendMessage("${ChatColor.RED}Cannot use attachments while in Creative mode")
            WeaponMechanicsPlus.getInstance().debugger.fine("Cannot use InventoryCreativeEvent for attachments")
            return
        }

        // This happens when the admin deleted attachment from config, but
        // players still have the attachment items in their inventory.
        val config = WeaponMechanicsPlus.getInstance().attachmentConfiguration
        val attachment = config.get<Attachment>(attachmentTitle)
        if (attachment == null) {
            WeaponMechanicsPlus.getInstance().debugger.warning("Attachment '$attachmentTitle' no longer exists in config. (tried adding)")
            return
        }

        if (!attachment.canAttach(item, itemTitle, weaponTitle != null)) {
            attachment.denyMechanics?.use(CastData(event.whoClicked, itemTitle, item))
            return
        }

        // Now we handle the actual attachment part
        attachmentItem.amount -= 1
        attachment.attach(item)
        attachment.equipMechanics?.use(CastData(event.whoClicked, itemTitle, item))

        // Cancel the event, so we don't pick up the weapon
        event.isCancelled = true
    }
}