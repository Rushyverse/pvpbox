package com.github.rushyverse.pvpbox.items.hotbar

import com.github.rushyverse.api.extension.withBold
import com.github.rushyverse.api.extension.withoutItalic
import com.github.rushyverse.api.item.InventoryConditionSuspend
import com.github.rushyverse.api.item.asNative
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.pvpbox.PvpboxServer.Companion.BUNDLE_PVPBOX
import com.github.rushyverse.pvpbox.inventories.InventoryKits
import com.github.rushyverse.pvpbox.kit.commons.AbstractKit
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.inventory.condition.InventoryCondition
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.util.*

class HotbarItemsManager(
    private val translationsProvider: TranslationsProvider,
    private val kitList: List<AbstractKit>
) {

    fun createMenuItemWithHandler(locale: Locale): Pair<ItemStack, InventoryCondition> {
        val item = ItemStack.builder(Material.GOLDEN_SWORD)
            .displayName(
                Component.text(
                    translationsProvider.translate("menu_item_name", locale, BUNDLE_PVPBOX), NamedTextColor.GOLD
                ).withoutItalic().withBold()
            ).build()

        return item to InventoryConditionSuspend { player: Player, _, _, _ ->
            val menu = InventoryKits(translationsProvider, locale, player, kitList).build()
            player.openInventory(menu)
        }.asNative()
    }
}