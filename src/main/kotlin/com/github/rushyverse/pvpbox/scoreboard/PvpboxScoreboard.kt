package com.github.rushyverse.pvpbox.scoreboard

import com.github.rushyverse.api.extension.undefineBold
import com.github.rushyverse.api.extension.withBold
import com.github.rushyverse.api.extension.withItalic
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationsProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.entity.Player
import net.minestom.server.scoreboard.Sidebar

class PvpboxScoreboard(val translationsProvider: TranslationsProvider, val player: Player) :
    Sidebar(Component.text("PvPBox", TextColor.fromHexString("#e500e8")).withBold()) {

    init {
        val playerLocale = SupportedLanguage.ENGLISH.locale

        createLine(ScoreboardLine("emptyLine4", Component.empty(), 11))

        createLine(
            ScoreboardLine(
                "level",
                Component.text("Level:", NamedTextColor.WHITE)
                    .append(Component.text(" 0" + "✩", NamedTextColor.GRAY)),
                10
            )
        )

        createLine(ScoreboardLine("emptyLine3", Component.empty(), 9))

        createLine(
            ScoreboardLine(
                "dust",
                Component.text("Dust:", NamedTextColor.WHITE)
                    .append(Component.text(" 0" + "✵", NamedTextColor.AQUA)),
                8
            )
        )

        createLine(
            ScoreboardLine(
                "status",
                Component.text("Status:", NamedTextColor.WHITE)
                    .append(Component.text(" Fighting", NamedTextColor.RED)),
                7
            )
        )

        createLine(ScoreboardLine("emptyLine2", Component.empty(), 6))

        createLine(
            ScoreboardLine(
                "kills",
                Component.text("Kills:", NamedTextColor.WHITE)
                    .append(Component.text(" 0", NamedTextColor.GRAY)),
                5
            )
        )

        createLine(
            ScoreboardLine(
                "deaths",
                Component.text("Deaths:", NamedTextColor.WHITE)
                    .append(Component.text(" 0", NamedTextColor.GRAY)),
                4
            )
        )

        createLine(
            ScoreboardLine(
                "killstreak",
                Component.text("Killstreak:", NamedTextColor.WHITE)
                    .append(Component.text(" 0", NamedTextColor.GRAY)),
                3
            )
        )

        createLine(ScoreboardLine("emptyLine1", Component.empty(), 2))

        createLine(
            ScoreboardLine(
                "ip",
                Component.text(" www.rushy.space ", TextColor.fromHexString("#e500e8")).withItalic(),
                1
            )
        )
    }

    public fun update(counter: Int) {
        updateLineContent("players", Component.text("Counter : $counter", NamedTextColor.RED))
    }
}