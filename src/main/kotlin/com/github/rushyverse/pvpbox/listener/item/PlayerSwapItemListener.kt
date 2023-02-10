package com.github.rushyverse.pvpbox.listener.item

import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerSwapItemEvent

class PlayerSwapItemListener : EventListener<PlayerSwapItemEvent> {

    override fun eventType(): Class<PlayerSwapItemEvent> {
        return PlayerSwapItemEvent::class.java
    }

    override fun run(event: PlayerSwapItemEvent): EventListener.Result {
        return if (event.player.gameMode != GameMode.CREATIVE) {
            event.isCancelled = true
            EventListener.Result.INVALID
        } else {
            EventListener.Result.SUCCESS
        }
    }
}