/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.listeners

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import com.cjcrafter.weaponmechanicsplus.weapon.firemode.FireMode
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments.Attachment
import me.deecaad.core.compatibility.CompatibilityAPI
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponGenerateEvent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class WeaponGenerateListener : Listener {

    @EventHandler
    fun onWeaponGenerate(event: WeaponGenerateEvent) {
        val attachments: List<String> = event.getOrDefault("attachments", listOf())!!

        for (attachment in attachments) {
            val attachmentInstance = WeaponMechanicsPlus.getInstance().attachmentConfiguration.get<Attachment>(attachment)
            if (attachmentInstance == null) {
                event.sender?.sendMessage("${ChatColor.RED}Unknown attachment: $attachment")
                continue
            }

            // Admins may bypass the canAttach check via commands here, which is intended.
            // But MAYBE the admin will be confused and think it is a bug, so let's warn them.
            if (!attachmentInstance.canAttach(event.weaponStack, event.weaponTitle, isWeapon = true)) {
                event.sender?.sendMessage("${ChatColor.YELLOW}$attachment cannot normally be attached, but was attached anyway due to admin permissions.")
            }

            attachmentInstance.attach(event.weaponStack)
        }

        // For firemodes, we need to make sure that each firemode comes fully loaded
        val config = WeaponMechanics.getInstance().weaponConfigurations
        val firemode: FireMode? = config.getObject("${event.weaponTitle}.Fire_Mode", FireMode::class.java)
        if (firemode != null) {
            for (mode in firemode.modes) {
                if (mode.separateAmmo == FireMode.UNIVERSAL_AMMO) continue

                val ammoLeftTag = mode.separateAmmo + FireMode.AMMO_LEFT_SUFFIX
                val reloadAmount = config.getInt("${mode.weaponTitle}.Reload.Magazine_Size")
                CompatibilityAPI.getNBTCompatibility().setInt(event.weaponStack, "weaponmechanicsplus", ammoLeftTag, reloadAmount)
            }
        }
    }
}