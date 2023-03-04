package com.github.rushyverse.pvpbox.map

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.ItemFrameMeta
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.metadata.MapMeta
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer
import net.minestom.server.network.packet.server.SendablePacket
import java.awt.geom.AffineTransform
import javax.imageio.ImageIO

/**
 * A class that allows you to create an Image as Map Item Frame on the server.
 * @property resourceImageName The resource that used to be printed as map.
 * @property widthBlocks The width blocks size desired for the item frame.
 * @property heightBlocks The height blocks size desired for the item frame.
 * @property packets The packets list to send to new players.
 */
class MapImage(
    private val resourceImageName: String,
    private val widthBlocks: Int,
    private val heightBlocks: Int,
) {

    companion object {
        /**
         * Corresponds to the default resolution used to build image as packets.
         */
        private const val DEFAULT_RESOLUTION_BITWISE_OPERATION = 7;

        private const val NUMBER_IMAGE_PACKETS = 15

        /**
         * Create an item frame on which the image will be displayed
         * @param instance The instance where you want to create the frame.
         * @param pos The position of the frame.
         */
        fun createItemFrame(instance: Instance, pos: Pos) {
            val maxX = pos.blockX()
            val maxY = pos.blockY()
            val z = pos.blockZ()
            val yaw = pos.yaw
            repeat(NUMBER_IMAGE_PACKETS) { i ->
                val x = maxX - i % 5
                val y = maxY - i / 5

                val itemFrame = Entity(EntityType.ITEM_FRAME)
                val meta = itemFrame.entityMeta as ItemFrameMeta
                itemFrame.setInstance(instance, Pos(x.toDouble(), y.toDouble(), z.toDouble(), yaw, 0f))
                meta.setNotifyAboutChanges(false)

                meta.orientation = ItemFrameMeta.Orientation.NORTH
                meta.isInvisible = true
                meta.item = ItemStack.builder(Material.FILLED_MAP).meta(MapMeta::class.java) { it.mapId(i) }.build()
                meta.setNotifyAboutChanges(true)
            }
        }

        /**
         * Creates packets from the image.
         * @param framebuffer The frame buffer to convert as packets.
         * @return The list of packets.
         */
        private fun mapPackets(framebuffer: LargeGraphics2DFramebuffer): Array<SendablePacket> {
            return Array(NUMBER_IMAGE_PACKETS) {
                val x = it % 5
                val y = it / 5
                framebuffer.createSubView(x shl DEFAULT_RESOLUTION_BITWISE_OPERATION, y shl DEFAULT_RESOLUTION_BITWISE_OPERATION).preparePacket(it)
            }
        }
    }

    val packets: Array<SendablePacket> by lazy {
        val framebuffer = LargeGraphics2DFramebuffer(widthBlocks shl DEFAULT_RESOLUTION_BITWISE_OPERATION, heightBlocks shl DEFAULT_RESOLUTION_BITWISE_OPERATION)
        MapImage::class.java.getResourceAsStream("/$resourceImageName")?.buffered()?.use {
            val image = ImageIO.read(it)
            framebuffer.renderer.drawRenderedImage(image, AffineTransform.getScaleInstance(1.0, 1.0))
            mapPackets(framebuffer)
        } ?: error("Unable to retrieve the image $resourceImageName in resources.")
    }
}