package com.github.rushyverse.pvpbox.listener

import com.github.rushyverse.api.extension.setItemStack
import com.github.rushyverse.api.position.CubeArea
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.pvpbox.items.hotbar.HotbarItemsManager
import com.github.rushyverse.pvpbox.map.MapImage
import com.github.rushyverse.pvpbox.scoreboard.PvpboxScoreboard
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerSpawnEvent

class PlayerSpawnListener(
    private val translationsProvider: TranslationsProvider,
    private val hotbarItemsManager: HotbarItemsManager,
    private val spawnPoint: Pos,
    private val spawnArea: CubeArea<Player>
) : EventListener<PlayerSpawnEvent> {

    override fun eventType(): Class<PlayerSpawnEvent> {
        return PlayerSpawnEvent::class.java
    }

    override fun run(event: PlayerSpawnEvent): EventListener.Result {
        val player = event.player
        val scoreboard = PvpboxScoreboard(translationsProvider, player)

        scoreboard.addViewer(player)

        giveItems(player)
        player.setHeldItemSlot(4)

        player.teleport(spawnPoint)

        MapImage.packets()?.toList()?.let { player.sendPackets(it) }

        return EventListener.Result.SUCCESS
    }

    private fun giveItems(player: Player) {
        val inv = player.inventory
        val locale = SupportedLanguage.ENGLISH.locale

        val menu = hotbarItemsManager.createMenuItemWithHandler(locale)
        inv.setItemStack(4, menu.first, handler = menu.second)
    }
}