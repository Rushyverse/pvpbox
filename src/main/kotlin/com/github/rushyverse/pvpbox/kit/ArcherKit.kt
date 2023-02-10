package com.github.rushyverse.pvpbox.kit

import com.github.rushyverse.pvpbox.kit.commons.AbstractKit
import net.minestom.server.entity.Player
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class ArcherKit : AbstractKit(
    "archer",
    Material.BOW
) {
    override fun applyKit(player: Player, inv: PlayerInventory) {

        inv.helmet = ItemStack.of(Material.GOLDEN_HELMET)
        inv.chestplate = ItemStack.of(Material.CHAINMAIL_CHESTPLATE)
        inv.leggings = ItemStack.of(Material.IRON_LEGGINGS)
        inv.boots = ItemStack.of(Material.LEATHER_BOOTS)

        val bow = ItemStack.builder(Material.BOW)
            .meta { it.enchantment(Enchantment.INFINITY, 1) }
            .build()

        inv.addItemStack(bow)
        inv.addItemStack(ItemStack.of(Material.WOODEN_SWORD))
        inv.setItemStack(9, ItemStack.of(Material.ARROW).withAmount(64))
    }
}