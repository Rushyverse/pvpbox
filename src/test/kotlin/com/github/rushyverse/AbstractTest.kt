package com.github.rushyverse

import com.github.rushyverse.api.configuration.BungeeCordConfiguration
import com.github.rushyverse.api.configuration.IConfiguration
import com.github.rushyverse.api.configuration.VelocityConfiguration
import com.github.rushyverse.pvpbox.configuration.PvpboxConfiguration
import com.github.rushyverse.pvpbox.configuration.ServerConfiguration
import com.github.rushyverse.utils.getAvailablePort
import kotlinx.serialization.hocon.Hocon
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AbstractTest {

    companion object {
        private const val PROPERTY_USER_DIR = "user.dir"
        const val DEFAULT_WORLD = "world"
    }

    @TempDir
    lateinit var tmpDirectory: File

    private lateinit var initCurrentDirectory: String

    protected val expectedDefaultConfiguration: PvpboxConfiguration
        get() = PvpboxConfiguration(
            ServerConfiguration(
                25566,
                DEFAULT_WORLD,
                false,
                BungeeCordConfiguration(false, ""),
                VelocityConfiguration(false, "")
            )
        )

    @BeforeTest
    open fun onBefore() {
        initCurrentDirectory = System.getProperty(PROPERTY_USER_DIR)
        System.setProperty(PROPERTY_USER_DIR, tmpDirectory.absolutePath)
    }

    @AfterTest
    open fun onAfter() {
        System.setProperty(PROPERTY_USER_DIR, initCurrentDirectory)
    }

    protected fun fileOfTmpDirectory(fileName: String) = File(tmpDirectory, fileName)

    protected fun configurationToHocon(configuration: PvpboxConfiguration) =
        Hocon.encodeToConfig(PvpboxConfiguration.serializer(), configuration)

    protected fun configurationToHoconFile(
        configuration: PvpboxConfiguration,
        file: File = fileOfTmpDirectory(IConfiguration.DEFAULT_CONFIG_FILE_NAME)
    ) =
        file.writeText(configurationToHocon(configuration).root().render())

    protected fun copyFolderFromResourcesToFolder(folderName: String, destination: File) {
        val folder = File(javaClass.classLoader.getResource(folderName)!!.file)
        folder.copyRecursively(destination)
    }

    protected fun copyWorldInTmpDirectory(
        configuration: PvpboxConfiguration = defaultConfigurationOnAvailablePort()
    ) {
        val worldFile = fileOfTmpDirectory(configuration.server.world)
        copyFolderFromResourcesToFolder(DEFAULT_WORLD, worldFile)
    }

    protected fun defaultConfigurationOnAvailablePort() = expectedDefaultConfiguration.let {
        it.copy(server = it.server.copy(port = getAvailablePort()))
    }
}