package com.github.rushyverse

import com.github.rushyverse.api.command.GamemodeCommand
import com.github.rushyverse.api.command.GiveCommand
import com.github.rushyverse.api.command.KickCommand
import com.github.rushyverse.api.command.StopCommand
import com.github.rushyverse.api.configuration.IConfiguration
import com.github.rushyverse.pvpbox.configuration.PvpboxConfiguration
import com.github.rushyverse.pvpbox.PvpboxServer
import com.github.rushyverse.pvpbox.listener.PlayerLoginListener
import com.github.rushyverse.pvpbox.listener.PlayerAttackListener
import com.github.rushyverse.pvpbox.listener.PlayerSpawnListener
import com.github.rushyverse.utils.randomString
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.minestom.server.MinecraftServer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import kotlin.test.*

class PvpboxServerTest : AbstractTest() {

    @AfterTest
    override fun onAfter() {
        super.onAfter()
        MinecraftServer.stopCleanly()
    }

    @Nested
    inner class CreateOrGetConfiguration {

        @Test
        fun `should create a configuration file if it doesn't exist`() = runTest {
            assertThrows<IOException> {
                PvpboxServer().start()
            }
            val configurationFile = fileOfTmpDirectory(IConfiguration.DEFAULT_CONFIG_FILE_NAME)
            assertTrue { configurationFile.isFile }

            val configuration = IConfiguration.readHoconConfigurationFile<PvpboxConfiguration>(configurationFile)
            assertEquals(expectedDefaultConfiguration, configuration)
        }

        @Test
        fun `should use the configuration file if exists`() = runTest {
            val configurationFile = fileOfTmpDirectory(randomString())
            assertTrue { configurationFile.createNewFile() }

            val configuration = defaultConfigurationOnAvailablePort()
            configurationToHoconFile(configuration, configurationFile)

            val exception = assertThrows<FileSystemException> {
                PvpboxServer(configurationFile.absolutePath).start()
            }
            assertEquals(configuration.server.world, exception.file.name)
        }

    }

    @Nested
    inner class UseConfiguration {

        @Test
        fun `should use configuration to turn on the server`() = runTest {
            val configuration = defaultConfigurationOnAvailablePort()
            val configurationFile = fileOfTmpDirectory(randomString())
            configurationToHoconFile(configuration, configurationFile)

            copyWorldInTmpDirectory(configuration)

            PvpboxServer(configurationFile.absolutePath).start()

            // If no exception is thrown, the world is loaded
            assertTrue { MinecraftServer.isStarted() }

            val server = MinecraftServer.getServer()
            assertEquals(configuration.server.port, server.port)
            assertEquals("0.0.0.0", server.address)
        }
    }

    @Nested
    inner class Listener {

        @Test
        fun `should load the listeners`() = runTest {
            copyWorldInTmpDirectory()
            PvpboxServer().start()

            val eventHandler = MinecraftServer.getGlobalEventHandler()

            sequenceOf(
                PlayerLoginListener(mockk()),
                PlayerSpawnListener(mockk(), mockk(), mockk(), mockk()),
                PlayerAttackListener(mockk())
            ).map { it.eventType() }.all { eventHandler.hasListener(it) }
        }
    }

    @Nested
    inner class Command {

        @Test
        fun `should load all commands`() = runTest {
            copyWorldInTmpDirectory()
            PvpboxServer().start()

            val commandManager = MinecraftServer.getCommandManager()
            assertContentEquals(
                commandManager.commands.asSequence().map { it::class.java }.sortedBy { it.simpleName }.toList(),
                sequenceOf(
                    StopCommand::class.java,
                    KickCommand::class.java,
                    GiveCommand::class.java,
                    GamemodeCommand::class.java
                ).sortedBy { it.simpleName }.toList()
            )
        }
    }
}