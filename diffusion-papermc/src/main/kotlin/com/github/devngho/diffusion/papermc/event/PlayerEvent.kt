package com.github.devngho.diffusion.papermc.event

import com.github.devngho.diffusion.papermc.item.toDiffusionItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PlayerEvent : Listener {
    @EventHandler
    suspend fun onInteraction(event: PlayerInteractEvent) {
        if (event.hasItem()){
            event.item!!.toDiffusionItem()?.run {
                this.onInteraction(event)
            }
        }
    }
}