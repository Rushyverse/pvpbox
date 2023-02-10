package com.github.rushyverse.pvpbox.listener.death

import com.github.rushyverse.api.position.CubeArea
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerDeathEvent

class PlayerDeathListener(
    private val respawnPoint: Pos,
    private val spawnArea: CubeArea<Player>
) : EventListener<PlayerDeathEvent> {

    override fun eventType(): Class<PlayerDeathEvent> {
        return PlayerDeathEvent::class.java
    }

    override fun run(event: PlayerDeathEvent): EventListener.Result {

        val player = event.player

        val killer = event.entity

        player.isEnableRespawnScreen=false
        player.respawnPoint = respawnPoint

        return EventListener.Result.SUCCESS
    }
}