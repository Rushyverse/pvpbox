package com.github.rushyverse.pvpbox.kit.commons

import com.github.rushyverse.api.extension.formattedLore
import com.github.rushyverse.api.extension.withoutItalic
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.pvpbox.PvpboxServer.Companion.BUNDLE_PVPBOX
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemHideFlag
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.util.*

/**
 * AbstractKit allows you to create a customized kit.
 * @property key String value used to retrieve the name and description of the kit.
 * @property material Material represents the icon of the kit.
 * @constructor
 */
abstract class AbstractKit(
    val key: String,
    val material: Material,
) {

    fun buildIcon(translationsProvider: TranslationsProvider, locale: Locale): ItemStack {
        val nameKey = "kit.$key.name"
        val kitName = translationsProvider.get(nameKey, locale, BUNDLE_PVPBOX)

        val descKey = "kit.$key.description"
        val kitDesc = translationsProvider.get(descKey, locale, BUNDLE_PVPBOX)

        val icon = ItemStack.builder(material)
            .displayName(Component.text(kitName, NamedTextColor.GREEN).withoutItalic())
            .formattedLore(kitDesc, 30)
            .meta { it.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES) }

        return icon.build()
    }

    abstract fun applyKit(player: Player, inv:PlayerInventory)
}