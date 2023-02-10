package com.github.rushyverse.pvpbox.listener.block

import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerBlockPlaceEvent

class PlayerPlaceBlockListener : EventListener<PlayerBlockPlaceEvent> {

    override fun eventType(): Class<PlayerBlockPlaceEvent> {
        return PlayerBlockPlaceEvent::class.java
    }

    override fun run(event: PlayerBlockPlaceEvent): EventListener.Result {
        return if (event.player.gameMode != GameMode.CREATIVE) {
            event.isCancelled = true
            EventListener.Result.INVALID
        } else {
            EventListener.Result.SUCCESS
        }
    }
}