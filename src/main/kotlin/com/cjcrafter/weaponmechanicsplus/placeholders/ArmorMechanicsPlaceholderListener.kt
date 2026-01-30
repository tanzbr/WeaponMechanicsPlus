/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.placeholders

import com.cjcrafter.armormechanics.ArmorMechanics
import com.cjcrafter.armormechanics.events.ArmorUpdateEvent
import me.deecaad.core.file.BukkitConfig
import me.deecaad.core.file.SerializeData
import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.core.placeholder.PlaceholderMessage
import me.deecaad.core.placeholder.PlaceholderMessageChain
import me.deecaad.core.utils.AdventureUtil
import me.deecaad.core.utils.StringUtil
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.File

class ArmorMechanicsPlaceholderListener : Listener {

    private val armorMechanicsDisplays: MutableMap<String, PlaceholderMessage> = HashMap()
    private val armorMechanicsLores: MutableMap<String, PlaceholderMessageChain> = HashMap()

    init {
        val config = ArmorMechanics.getInstance().armorConfigurations
        for (armorTitle in config.keys(deep = false)) {
            val display: PlaceholderMessage? = config.getString("Name")?.let {
                PlaceholderMessage(it)
            }
            val lores: List<PlaceholderMessage>? = config.getObject("Lore", List::class.java)?.map {
                PlaceholderMessage(it as String)
            }

            if (display != null) armorMechanicsDisplays[armorTitle] = display
            if (lores != null) armorMechanicsLores[armorTitle] = PlaceholderMessageChain(lores)
        }
    }

    @EventHandler
    fun updateArmor(event: ArmorUpdateEvent) {
        val player = event.entity as? Player ?: return
        val display = armorMechanicsDisplays[event.armorTitle]
        val lore = armorMechanicsLores[event.armorTitle]

        val placeholderData = PlaceholderData.of(player, event.armor, event.armorTitle, null)
        display?.replaceAndDeserialize(placeholderData)?.let { AdventureUtil.setName(event.armor, it) }
        lore?.replaceAndDeserialize(placeholderData)?.let { AdventureUtil.setLore(event.armor, it) }
    }
}
