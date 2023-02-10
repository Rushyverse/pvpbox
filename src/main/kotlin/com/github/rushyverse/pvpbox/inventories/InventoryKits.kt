package com.github.rushyverse.pvpbox.inventories

import com.github.rushyverse.api.extension.addItemStack
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.pvpbox.PvpboxServer.Companion.BUNDLE_PVPBOX
import com.github.rushyverse.pvpbox.kit.commons.AbstractKit
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import org.slf4j.LoggerFactory
import java.util.*

class InventoryKits(
    private val translationsProvider: TranslationsProvider,
    private val locale: Locale,
    private val player: Player,
    private val kitsList: List<AbstractKit>,
) {

     fun build() : Inventory {

        val inv = Inventory(InventoryType.CHEST_3_ROW, translationsProvider.translate("kits_menu_title", locale, BUNDLE_PVPBOX))

        kitsList.forEach { kit ->

            LoggerFactory.getLogger(this.javaClass).info("loop: ${kit.key}")

            val kitIcon = kit.buildIcon(translationsProvider, locale)
            
            inv.addItemStack(kitIcon) { player, _,_,_ ->
                val inv = player.inventory;
                player.clearEffects()
                inv.clear()
                kit.applyKit(player, inv)
                player.closeInventory()
            }
        }


        return inv;
    }
}