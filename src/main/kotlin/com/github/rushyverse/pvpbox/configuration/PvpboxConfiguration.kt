package com.github.rushyverse.pvpbox.configuration

import com.github.rushyverse.api.configuration.*
import com.github.rushyverse.api.serializer.PosSerializer
import kotlinx.serialization.*
import kotlinx.serialization.encoding.*
import net.minestom.server.coordinate.Pos

/**
 * Configuration of the server.
 * @property server Configuration about the minestom server.
 */
@Suppress("PROVIDED_RUNTIME_TOO_LOW") // https://github.com/Kotlin/kotlinx.serialization/issues/993
@Serializable
data class PvpboxConfiguration(
    @SerialName("server")
    override val server: ServerConfiguration,
    @SerialName("area")
    val area: AreaConfiguration,
    @SerialName("pvp")
    val pvp: PvpConfiguration
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

@Serializable
data class AreaConfiguration(
    val limitY: Double,
    @Serializable(with = PosSerializer::class)
    val spawnPoint: Pos,
    @Serializable(with = PosSerializer::class)
    val spawnArea1: Pos,
    @Serializable(with = PosSerializer::class)
    val spawnArea2: Pos
) {
    val spawnArea: List<Pos> get() = listOf(spawnArea1, spawnArea2)
}

@Serializable
data class PvpConfiguration(
    val food: Boolean,
    val fallDamage: Boolean,
    val equipmentDamage: Boolean,
    val exhaustion: Boolean,
    val knockback: KnockbackConfiguration
)

@Serializable
data class KnockbackConfiguration(
    val horizontal: Double,
    val vertical: Double,
    val verticalLimit: Double,
    val extraHorizontal: Double,
    val extraVertical: Double
)