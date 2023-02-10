package com.github.rushyverse.pvpbox.listener.item

import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryConditionResult

class PlayerItemClickListener : EventListener<PlayerUseItemEvent> {

    override fun eventType(): Class<PlayerUseItemEvent> {
        return PlayerUseItemEvent::class.java
    }

    override fun run(event: PlayerUseItemEvent): EventListener.Result {
        val player = event.player
        val item = event.itemStack
        val slot = player.heldSlot.toInt()

        player.inventory.inventoryConditions.forEach {
            val result = InventoryConditionResult(item, null)
            it.accept(player, slot, ClickType.RIGHT_CLICK, result)
            event.isCancelled = result.isCancel
        }

        return EventListener.Result.SUCCESS
    }
}