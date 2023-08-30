package com.cjcrafter.weaponmechanicsplus.placeholders

import com.cjcrafter.armormechanics.ArmorMechanics
import com.cjcrafter.armormechanics.events.ArmorUpdateEvent
import me.deecaad.core.MechanicsCore
import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.core.placeholder.PlaceholderMessage
import me.deecaad.core.placeholder.PlaceholderMessageChain
import me.deecaad.core.utils.AdventureUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ArmorMechanicsPlaceholderListener : Listener {

    private val armorMechanicsDisplays: MutableMap<String, PlaceholderMessage> = HashMap()
    private val armorMechanicsLores: MutableMap<String, PlaceholderMessageChain> = HashMap()

    init {
        val mini = MechanicsCore.getPlugin().message

        // ArmorMechanics armor updating template
        for (entry in ArmorMechanics.INSTANCE.armors) {
            val display = PlaceholderMessage(mini.serialize(AdventureUtil.getName(entry.value)))
            val lores = AdventureUtil.getLore(entry.value)?.map { mini.serialize(it) }?.map { PlaceholderMessage(it) }

            armorMechanicsDisplays[entry.key] = display
            if (lores != null) armorMechanicsLores[entry.key] = PlaceholderMessageChain(lores)
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
