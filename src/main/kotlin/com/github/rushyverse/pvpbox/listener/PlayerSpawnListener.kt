package com.github.rushyverse.pvpbox.listener

import com.github.rushyverse.api.extension.setItemStack
import com.github.rushyverse.api.image.MapImage
import com.github.rushyverse.api.listener.EventListenerSuspend
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.pvpbox.items.hotbar.HotbarItemsManager
import com.github.rushyverse.pvpbox.scoreboard.PvpboxScoreboard
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent

class PlayerSpawnListener(
    private val translationsProvider: TranslationsProvider,
    private val hotbarItemsManager: HotbarItemsManager,
    private val spawnPoint: Pos,
    private val mapImage: MapImage
) : EventListenerSuspend<PlayerSpawnEvent>() {

    override fun eventType(): Class<PlayerSpawnEvent> {
        return PlayerSpawnEvent::class.java
    }

    override suspend fun runSuspend(event: PlayerSpawnEvent) {
        val player = event.player
        val scoreboard = PvpboxScoreboard(translationsProvider, player)

        scoreboard.addViewer(player)

        giveItems(player)
        player.setHeldItemSlot(4)

        player.teleport(spawnPoint)
        player.sendPackets(*mapImage.packets)
    }

    private fun giveItems(player: Player) {
        val inv = player.inventory
        val locale = SupportedLanguage.ENGLISH.locale

        val menu = hotbarItemsManager.createMenuItemWithHandler(locale)
        inv.setItemStack(4, menu.first, handler = menu.second)
    }
}