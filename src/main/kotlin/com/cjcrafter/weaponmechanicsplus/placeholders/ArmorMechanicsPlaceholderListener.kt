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
        val armorFile = File(ArmorMechanics.INSTANCE.dataFolder, "Armor.yml")
        val config = YamlConfiguration.loadConfiguration(armorFile)

        for (armorTitle in config.getKeys(false)) {
            val data = SerializeData("Armor", armorFile, armorTitle, BukkitConfig(config))

            val display: PlaceholderMessage? = data.of("Name").getAdventure().orElse(null)?.let { PlaceholderMessage(it) }
            val lores: List<PlaceholderMessage>? = (data.of("Lore").get(List::class.java).orElse(null) as List<String>?)?.map { StringUtil.colorAdventure("<!i>$it") }?.map { PlaceholderMessage(it!!) }

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
