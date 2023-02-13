package com.github.rushyverse.pvpbox

import com.github.rushyverse.api.RushyServer
import com.github.rushyverse.api.position.CubeArea
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.core.cache.CacheClient
import com.github.rushyverse.pvpbox.configuration.PvpboxConfiguration
import com.github.rushyverse.pvpbox.items.hotbar.HotbarItemsManager
import com.github.rushyverse.pvpbox.kit.ArcherKit
import com.github.rushyverse.pvpbox.kit.WarriorKit
import com.github.rushyverse.pvpbox.kit.commons.AbstractKit
import com.github.rushyverse.pvpbox.listener.*
import com.github.rushyverse.pvpbox.listener.block.PlayerBreakBlockListener
import com.github.rushyverse.pvpbox.listener.block.PlayerPlaceBlockListener
import com.github.rushyverse.pvpbox.listener.death.PlayerDeathListener
import com.github.rushyverse.pvpbox.listener.item.PlayerDropItemListener
import com.github.rushyverse.pvpbox.listener.item.PlayerInventoryClickListener
import com.github.rushyverse.pvpbox.listener.item.PlayerItemClickListener
import com.github.rushyverse.pvpbox.listener.item.PlayerSwapItemListener
import io.github.bloepiloepi.pvp.PvpExtension
import io.github.bloepiloepi.pvp.config.DamageConfig
import io.github.bloepiloepi.pvp.config.FoodConfig
import io.github.bloepiloepi.pvp.config.PvPConfig
import io.github.bloepiloepi.pvp.explosion.PvpExplosionSupplier
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

private lateinit var kitsList: List<AbstractKit>
private lateinit var spawnArea: CubeArea<Player>
private lateinit var instance: Instance
private lateinit var spawnPoint: Pos

class PvpboxServer(private val configuration: String? = null) : RushyServer() {

    private  val limitY = 80.0 // Below this limit, player is killed

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

            instance = MinecraftServer.getInstanceManager().instances.first()
            val pos1 = Pos(-114.0, 152.0, 103.0)
            val pos2 = Pos(-134.0, 167.0, 123.0)
            spawnArea = CubeArea<Player>(instance, pos1, pos2)
            spawnPoint = Pos(-123.5, 156.0, 113.5).withYaw(-180F)

            kitsList = listOf<AbstractKit>(
                WarriorKit(),
                ArcherKit()
            )

            val globalEventHandler = MinecraftServer.getGlobalEventHandler()
            addListeners(globalEventHandler, it, translationsProvider)

            API.registerCommands()
            addCommands()

            loadPvp()

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
        globalEventHandler: GlobalEventHandler,
        instanceContainer: InstanceContainer, translationsProvider: TranslationsProvider
    ) {
        globalEventHandler.addListener(PlayerLoginListener(instanceContainer))
        globalEventHandler.addListener(
            PlayerSpawnListener(
                translationsProvider,
                HotbarItemsManager(translationsProvider, kitsList),
                spawnPoint,
                spawnArea,
            )
        )
        globalEventHandler.addListener(PlayerAttackListener(spawnArea))
        globalEventHandler.addListener(PlayerMoveListener(limitY))
        globalEventHandler.addListener(PlayerDeathListener(spawnPoint,spawnArea))

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

    private fun loadPvp() {
        PvpExtension.init();
        val foodConfig = FoodConfig.emptyBuilder(false) // Disable food
        val damageConfig = DamageConfig.legacyBuilder()
            .fallDamage(false)
            .equipmentDamage(false)
            .exhaustion(false)
        val pvpConfig = PvPConfig.legacyBuilder()
            .food(foodConfig)
            .damage(damageConfig)
            .build()

        MinecraftServer.getGlobalEventHandler().addChild(pvpConfig.createNode())
        instance.explosionSupplier = PvpExplosionSupplier.INSTANCE;
    }
}