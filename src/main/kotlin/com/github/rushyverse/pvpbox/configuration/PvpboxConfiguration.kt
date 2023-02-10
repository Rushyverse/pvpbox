package com.github.rushyverse.pvpbox.configuration

import com.github.rushyverse.api.configuration.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration of the server.
 * @property server Configuration about the minestom server.
 */
@Suppress("PROVIDED_RUNTIME_TOO_LOW") // https://github.com/Kotlin/kotlinx.serialization/issues/993
@Serializable
data class PvpboxConfiguration(
    @SerialName("server")
    override val server: ServerConfiguration
) : IConfiguration

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class ServerConfiguration(
    override val port: Int,
    override val world: String,
    override val onlineMode: Boolean,
    override val bungeeCord: BungeeCordConfiguration,
    override val velocity: VelocityConfiguration
) : IServerConfiguration