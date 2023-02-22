package com.github.rushyverse.pvpbox.configuration

import com.github.rushyverse.api.configuration.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
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
    val area: AreaConfiguration
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
    @Serializable(with = PosSerializable::class)
    val spawnPoint: Pos,
    @Serializable(with = PosSerializable::class)
    val spawnArea1: Pos,
    @Serializable(with = PosSerializable::class)
    val spawnArea2: Pos
) {
    val spawnArea: List<Pos> get() = listOf(spawnArea1, spawnArea2)
}

object PosSerializable : KSerializer<Pos> {

    private val coordinateSerializer get() = Double.serializer()
    private val rotationSerializer get() = Float.serializer().nullable

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("pos") {
        element("x", coordinateSerializer.descriptor)
        element("y", coordinateSerializer.descriptor)
        element("z", coordinateSerializer.descriptor)
        element("yaw", rotationSerializer.descriptor)
        element("pitch", rotationSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Pos) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, coordinateSerializer, value.x)
            encodeSerializableElement(descriptor, 1, coordinateSerializer, value.y)
            encodeSerializableElement(descriptor, 2, coordinateSerializer, value.z)
            encodeSerializableElement(descriptor, 3, rotationSerializer, value.yaw)
            encodeSerializableElement(descriptor, 4, rotationSerializer, value.pitch)
        }
    }

    override fun deserialize(decoder: Decoder): Pos {
        return decoder.decodeStructure(descriptor) {
            var x: Double? = null
            var y: Double? = null
            var z: Double? = null
            var yaw: Float? = null
            var pitch: Float? = null

            if (decodeSequentially()) {
                x = decodeSerializableElement(descriptor, 0, coordinateSerializer)
                y = decodeSerializableElement(descriptor, 1, coordinateSerializer)
                z = decodeSerializableElement(descriptor, 2, coordinateSerializer)
                yaw = decodeSerializableElement(descriptor, 3, rotationSerializer)
                pitch = decodeSerializableElement(descriptor, 4, rotationSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> x = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        1 -> y = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        2 -> z = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        3 -> yaw = decodeSerializableElement(descriptor, index, rotationSerializer)
                        4 -> pitch = decodeSerializableElement(descriptor, index, rotationSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
            }

            Pos(
                x ?: throw SerializationException("The field x is missing"),
                y ?: throw SerializationException("The field y is missing"),
                z ?: throw SerializationException("The field z is missing"),
                yaw ?: 0f,
                pitch ?: 0f
            )
        }
    }
}