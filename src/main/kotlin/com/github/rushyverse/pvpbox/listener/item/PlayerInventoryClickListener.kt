package com.github.rushyverse.pvpbox.listener.item

import net.minestom.server.event.EventListener
import net.minestom.server.event.inventory.InventoryPreClickEvent

class PlayerInventoryClickListener : EventListener<InventoryPreClickEvent> {

    override fun eventType(): Class<InventoryPreClickEvent> {
        return InventoryPreClickEvent::class.java
    }

    override fun run(event: InventoryPreClickEvent): EventListener.Result {
        // Always true for security (inventory glitch etc..)
        event.isCancelled = true

        return EventListener.Result.SUCCESS
    }
}