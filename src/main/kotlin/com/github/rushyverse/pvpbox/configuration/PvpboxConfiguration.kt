package com.github.rushyverse.pvpbox.configuration

import com.github.rushyverse.api.configuration.*
import com.github.rushyverse.api.serializer.PosSerializer
import kotlinx.serialization.*
import kotlinx.serialization.encoding.*
import net.minestom.server.coordinate.Pos

/**
 * Configuration of the server.
 * @property server Configuration about the minestom server.
 * @property area Configuration about area and positions.
 * @property pvp Configuration about pvp, including knockback.
 * @constructor
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

/**
 * Basic server configuration.
 * @property port The port on which the server will start.
 * @property world The world folder name to load.
 * @property onlineMode The online-mode activation or not.
 * @property bungeeCord The BungeeCord configuration.
 * @property velocity The Velocity configuration.
 * @constructor
 */
@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class ServerConfiguration(
    override val port: Int,
    override val world: String,
    override val onlineMode: Boolean,
    override val bungeeCord: BungeeCordConfiguration,
    override val velocity: VelocityConfiguration
) : IServerConfiguration

/**
 * Area configuration for positions and protected area.
 * @property limitY Corresponds to the limit not to be exceeded when falling.
 * @property spawnPoint Represents the spawn point of the game server.
 * @property spawnArea1 Represents the first position allowing to build the cuboid of the protected area.
 * @property spawnArea2 Represents the second position allowing to build the cuboid of the protected area.
 * @constructor
 */
@Serializable
data class AreaConfiguration(
    val limitY: Double,
    @Serializable(with = PosSerializer::class)
    val spawnPoint: Pos,
    @Serializable(with = PosSerializer::class)
    val spawnArea1: Pos,
    @Serializable(with = PosSerializer::class)
    val spawnArea2: Pos
)

/**
 * The pvp configuration.
 * @property food Corresponds to the activation or not of the food system.
 * @property fallDamage Corresponds to the activation or not of fall damages.
 * @property equipmentDamage Corresponds to the activation or not of equipment damages.
 * @property exhaustion Corresponds to the activation or not of player exhaustion.
 * @property knockback Represents the knockback configuration.
 * @constructor
 */
@Serializable
data class PvpConfiguration(
    val food: Boolean,
    val fallDamage: Boolean,
    val equipmentDamage: Boolean,
    val exhaustion: Boolean,
    val knockback: KnockbackConfiguration
)

/**
 * The knockback configuration.
 * @property horizontal Corresponds to the knockback horizontal multiplier.
 * @property vertical Corresponds to the knockback vertical multiplier.
 * @property verticalLimit Corresponds to the knockback vertical limit multiplier.
 * @property extraHorizontal Corresponds to the knockback extra horizontal multiplier.
 * @property extraVertical Corresponds to the knockback extra vertical multiplier.
 * @constructor
 */
@Serializable
data class KnockbackConfiguration(
    val horizontal: Double,
    val vertical: Double,
    val verticalLimit: Double,
    val extraHorizontal: Double,
    val extraVertical: Double
)