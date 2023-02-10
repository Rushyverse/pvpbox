package com.github.rushyverse.pvpbox.listener.item

import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventListener
import net.minestom.server.event.item.ItemDropEvent

class PlayerDropItemListener : EventListener<ItemDropEvent> {

    override fun eventType(): Class<ItemDropEvent> {
        return ItemDropEvent::class.java
    }

    override fun run(event: ItemDropEvent): EventListener.Result {
        return if (event.player.gameMode != GameMode.CREATIVE) {
            event.isCancelled = true
            EventListener.Result.INVALID
        } else {
            EventListener.Result.SUCCESS
        }
    }
}