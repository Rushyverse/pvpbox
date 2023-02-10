package com.github.rushyverse.pvpbox.listener.block

import net.minestom.server.entity.GameMode
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerBlockBreakEvent

class PlayerBreakBlockListener : EventListener<PlayerBlockBreakEvent> {

    override fun eventType(): Class<PlayerBlockBreakEvent> {
        return PlayerBlockBreakEvent::class.java
    }

    override fun run(event: PlayerBlockBreakEvent): EventListener.Result {
        return if (event.player.gameMode != GameMode.CREATIVE) {
            event.isCancelled = true
            EventListener.Result.INVALID
        } else {
            EventListener.Result.SUCCESS
        }
    }
}