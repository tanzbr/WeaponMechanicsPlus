package com.cjcrafter.weaponmechanicsplus.listeners

import me.deecaad.core.placeholder.NumericPlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderRequestEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlaceholderListeners : Listener {


    @EventHandler
    fun requestPlaceholders(event: PlaceholderRequestEvent) {
        for (entry in event.placeholders()) {
            val placeholder = PlaceholderHandler.REGISTRY[entry.key]

            if (placeholder is NumericPlaceholderHandler) {

            }
        }
    }
}