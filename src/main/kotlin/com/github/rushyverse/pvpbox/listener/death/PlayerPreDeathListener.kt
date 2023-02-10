package com.github.rushyverse.pvpbox.listener.death

import io.github.bloepiloepi.pvp.damage.CustomDamageType
import io.github.bloepiloepi.pvp.events.EntityPreDeathEvent
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener

class PlayerPreDeathListener(
) : EventListener<EntityPreDeathEvent> {

    override fun eventType(): Class<EntityPreDeathEvent> {
        return EntityPreDeathEvent::class.java
    }

    override fun run(event: EntityPreDeathEvent): EventListener.Result {

        val player = event.entity as Player


        val damageType = event.damageType
        if (damageType == CustomDamageType.FALL){
            event.isCancelled=true
        }

        return EventListener.Result.SUCCESS
    }
}