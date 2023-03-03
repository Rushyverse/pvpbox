package com.github.rushyverse.pvpbox

import com.github.rushyverse.api.RushyServer
import com.github.rushyverse.api.position.CubeArea
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.core.cache.CacheClient
import com.github.rushyverse.pvpbox.configuration.PvpConfiguration
import com.github.rushyverse.pvpbox.configuration.PvpboxConfiguration
import com.github.rushyverse.pvpbox.items.hotbar.HotbarItemsManager
import com.github.rushyverse.pvpbox.kit.ArcherKit
import com.github.rushyverse.pvpbox.kit.WarriorKit
import com.github.rushyverse.pvpbox.kit.commons.AbstractKit
import com.github.rushyverse.pvpbox.listener.PlayerAttackListener
import com.github.rushyverse.pvpbox.listener.PlayerLoginListener
import com.github.rushyverse.pvpbox.listener.PlayerMoveListener
import com.github.rushyverse.pvpbox.listener.PlayerSpawnListener
import com.github.rushyverse.pvpbox.listener.block.PlayerBreakBlockListener
import com.github.rushyverse.pvpbox.listener.block.PlayerPlaceBlockListener
import com.github.rushyverse.pvpbox.listener.death.PlayerDeathListener
import com.github.rushyverse.pvpbox.listener.item.PlayerDropItemListener
import com.github.rushyverse.pvpbox.listener.item.PlayerInventoryClickListener
import com.github.rushyverse.pvpbox.listener.item.PlayerItemClickListener
import com.github.rushyverse.pvpbox.listener.item.PlayerSwapItemListener
import com.github.rushyverse.pvpbox.map.MapImage
import io.github.bloepiloepi.pvp.PvpExtension
import io.github.bloepiloepi.pvp.config.DamageConfig
import io.github.bloepiloepi.pvp.config.FoodConfig
import io.github.bloepiloepi.pvp.config.PvPConfig
import io.github.bloepiloepi.pvp.events.LegacyKnockbackEvent
import io.github.bloepiloepi.pvp.explosion.PvpExplosionSupplier
import io.github.bloepiloepi.pvp.legacy.LegacyKnockbackSettings
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.lettuce.core.RedisURI
import kotlinx.serialization.json.Json
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceContainer


suspend fun main(args: Array<String>) {
    PvpboxServer(args.firstOrNull()).start()
}

class PvpboxServer(private val configuration: String? = null) : RushyServer() {

    companion object {
        const val BUNDLE_PVPBOX = "pvpbox"
    }

    override suspend fun start() {
        start<PvpboxConfiguration>(configuration) {
            val translationsProvider = createTranslationsProvider(
                listOf(
                    API.BUNDLE_API,
                    BUNDLE_PVPBOX
                )
            )

            MapImage.create(it, Pos(-122.0, 163.0, 119.0))

            val kitsList = listOf(
                WarriorKit(),
                ArcherKit()
            )

            val globalEventHandler = MinecraftServer.getGlobalEventHandler()
            addListeners(this, globalEventHandler, it, translationsProvider, kitsList)

            API.registerCommands()
            addCommands()

            loadPvp(pvp, it)

            MinecraftServer.setBrandName("Rushyverse-Pvpbox")
        }
    }

    suspend fun createCacheClient(): CacheClient {
        return CacheClient {
            uri = RedisURI.create("redis://default:redispw@localhost:49153")
        }
    }

    fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    /**
     * Register all listeners of the server.
     * @param globalEventHandler Event handler of the server.
     * @param instanceContainer Instance container of the server.
     */
    private fun addListeners(
        configuration: PvpboxConfiguration,
        globalEventHandler: GlobalEventHandler,
        instanceContainer: InstanceContainer,
        translationsProvider: TranslationsProvider,
        kitsList: List<AbstractKit>
    ) {
        globalEventHandler.addListener(PlayerLoginListener(instanceContainer))
        val areaConfig = configuration.area
        val spawnArea = CubeArea<Player>(instanceContainer, areaConfig.spawnArea1, areaConfig.spawnArea2)
        globalEventHandler.addListener(
            PlayerSpawnListener(
                translationsProvider,
                HotbarItemsManager(translationsProvider, kitsList),
                areaConfig.spawnPoint,
                spawnArea,
            )
        )
        globalEventHandler.addListener(PlayerAttackListener(spawnArea))
        globalEventHandler.addListener(PlayerMoveListener(areaConfig.limitY))
        globalEventHandler.addListener(PlayerDeathListener(areaConfig.spawnPoint, spawnArea))

        globalEventHandler.addListener(PlayerItemClickListener())
        globalEventHandler.addListener(PlayerDropItemListener())
        globalEventHandler.addListener(PlayerSwapItemListener())
        globalEventHandler.addListener(PlayerInventoryClickListener())
        globalEventHandler.addListener(PlayerPlaceBlockListener())
        globalEventHandler.addListener(PlayerBreakBlockListener())
    }

    /**
     * Register all commands.
     */
    private fun addCommands() {
        val commandManager = MinecraftServer.getCommandManager()

    }

    private fun loadPvp(configuration: PvpConfiguration, instance: Instance) {
        PvpExtension.init();
        val foodConfig = FoodConfig.emptyBuilder(configuration.food) // Disable food
        val damageConfig = DamageConfig.legacyBuilder()
            .fallDamage(configuration.fallDamage)
            .equipmentDamage(configuration.equipmentDamage)
            .exhaustion(configuration.exhaustion)
        val pvpConfig = PvPConfig.legacyBuilder()
            .food(foodConfig)
            .damage(damageConfig)
            .build()

        // Knockback
        val kbConf = configuration.knockback
        val kbSettings = LegacyKnockbackSettings.builder()
            .horizontal(kbConf.horizontal)
            .vertical(kbConf.vertical)
            .verticalLimit(kbConf.verticalLimit)
            .extraHorizontal(kbConf.extraHorizontal)
            .extraVertical(kbConf.extraVertical)
            .build()
        MinecraftServer.getGlobalEventHandler().addListener(
            LegacyKnockbackEvent::class.java
        ) { event: LegacyKnockbackEvent ->
            event.settings = kbSettings
        }

        MinecraftServer.getGlobalEventHandler().addChild(pvpConfig.createNode())
        instance.explosionSupplier = PvpExplosionSupplier.INSTANCE;
    }
}