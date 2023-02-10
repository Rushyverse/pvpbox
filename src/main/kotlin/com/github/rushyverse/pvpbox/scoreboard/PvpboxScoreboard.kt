package com.github.rushyverse.pvpbox.scoreboard

import com.github.rushyverse.api.extension.withBold
import com.github.rushyverse.api.extension.withItalic
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationsProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.scoreboard.Sidebar

class PvpboxScoreboard(val translationsProvider: TranslationsProvider, val player: Player) :
    Sidebar(Component.text("   PvPBox   ",NamedTextColor.RED).withBold().withItalic()) {

    init {
        val playerLocale = SupportedLanguage.ENGLISH.locale

        createLine(ScoreboardLine("emptyLine3", Component.empty(), 5))

        createLine(
            ScoreboardLine(
                "kills",
                Component.text("Kills:", NamedTextColor.WHITE)
                    .append(Component.text(" 0", NamedTextColor.AQUA)),
                4
            )
        )
        createLine(
            ScoreboardLine(
                "deaths",
                Component.text("Deaths:", NamedTextColor.WHITE)
                    .append(Component.text(" 0", NamedTextColor.RED)),
                2
            )
        )

        createLine(ScoreboardLine("emptyLine2", Component.empty(), 3))

        createLine(ScoreboardLine("emptyLine1", Component.empty(), 1))

        createLine(
            ScoreboardLine(
                "ip",
                Component.text("  rushy.space", NamedTextColor.LIGHT_PURPLE).withItalic(),
                0
            )
        )
    }

    public fun update(counter: Int) {
        updateLineContent("players", Component.text("Counter : $counter", NamedTextColor.RED))
    }
}