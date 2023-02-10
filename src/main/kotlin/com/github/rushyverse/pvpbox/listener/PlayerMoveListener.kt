package com.github.rushyverse.pvpbox.listener

import com.github.rushyverse.api.extension.isInCube
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerMoveEvent

class PlayerMoveListener(
    private val limitY : Double
) : EventListener<PlayerMoveEvent> {

    override fun eventType(): Class<PlayerMoveEvent> {
        return PlayerMoveEvent::class.java
    }

    override fun run(event: PlayerMoveEvent): EventListener.Result {
        val player = event.player
        val pos = player.position

        if (pos.y <= limitY && !player.isDead) {
            player.kill()
        }

        return EventListener.Result.SUCCESS
    }
}