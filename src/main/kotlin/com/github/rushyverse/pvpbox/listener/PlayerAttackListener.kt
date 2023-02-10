package com.github.rushyverse.pvpbox.listener

import com.github.rushyverse.api.extension.isInCube
import com.github.rushyverse.api.position.CubeArea
import io.github.bloepiloepi.pvp.events.FinalAttackEvent
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener

class PlayerAttackListener(
    private val spawnArea: CubeArea<Player>
) : EventListener<FinalAttackEvent> {

    override fun eventType(): Class<FinalAttackEvent> {
        return FinalAttackEvent::class.java
    }

    override fun run(event: FinalAttackEvent): EventListener.Result {

        val target = event.target
        val pos = target.position

        event.isCancelled = pos.isInCube(spawnArea.min, spawnArea.max)

        return EventListener.Result.SUCCESS
    }
}