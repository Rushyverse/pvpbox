package com.github.rushyverse.pvpbox.kit

import com.github.rushyverse.pvpbox.kit.commons.AbstractKit
import net.minestom.server.entity.Player
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class WarriorKit : AbstractKit(
    "warrior",
    Material.IRON_SWORD
) {
    override fun applyKit(player: Player, inv: PlayerInventory) {

        inv.helmet = ItemStack.of(Material.IRON_HELMET)
        inv.chestplate = ItemStack.of(Material.IRON_CHESTPLATE)
        inv.leggings = ItemStack.of(Material.IRON_LEGGINGS)
        inv.boots = ItemStack.of(Material.IRON_BOOTS)

        val sword = ItemStack.builder(Material.IRON_SWORD)
            .meta { it.enchantment(Enchantment.SHARPNESS, 1) }
            .build()

        inv.addItemStack(sword)

    }
}