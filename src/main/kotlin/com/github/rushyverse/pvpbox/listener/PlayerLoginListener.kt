package com.github.rushyverse.pvpbox.listener

import com.github.rushyverse.api.command.GamemodeCommand
import com.github.rushyverse.api.command.GiveCommand
import com.github.rushyverse.api.command.KickCommand
import com.github.rushyverse.api.command.StopCommand
import com.github.rushyverse.api.extension.sync
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.InstanceContainer

class PlayerLoginListener(private val instanceContainer: InstanceContainer) : EventListener<PlayerLoginEvent> {

    override fun eventType(): Class<PlayerLoginEvent> {
        return PlayerLoginEvent::class.java
    }

    override fun run(event: PlayerLoginEvent): EventListener.Result {
        event.setSpawningInstance(instanceContainer)
        event.player.sync {
            respawnPoint = Pos(0.0, 100.0, 0.0)
            addPermission(GamemodeCommand.Permissions.SELF.permission)
            addPermission(GamemodeCommand.Permissions.OTHER.permission)
            addPermission(StopCommand.Permissions.EXECUTE.permission)
            addPermission(GiveCommand.Permissions.EXECUTE.permission)
            addPermission(KickCommand.Permissions.EXECUTE.permission)
        }
        return EventListener.Result.SUCCESS
    }
}