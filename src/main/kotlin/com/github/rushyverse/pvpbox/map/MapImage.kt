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
import java.io.IOException
import javax.imageio.ImageIO

/**
 * A class that allows you to create an Image as Map Item Frame on the server.
 * @property resourceImageName The resource that used to be printed as map.
 * @property widthBlocks The width blocks size desired for the item frame.
 * @property heightBlocks The height blocks size desired for the item frame.
 * @property packets The packets list to send to new players.
 * @constructor
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
        const val DEFAULT_RESOLUTION = 128;

        /**
         * Create an item frame on which the image will be displayed
         * @param instance The instance where you want to create the frame.
         * @param maximum The position of the frame.
         */
        fun createItemFrame(instance: Instance, maximum: Pos) {
            val maxX = maximum.blockX()
            val maxY = maximum.blockY()
            val z = maximum.blockZ()
            val yaw = maximum.yaw
            for (i in 0..14) {
                val x = maxX - i % 5
                val y = maxY - i / 5
                val itemFrame = Entity(EntityType.ITEM_FRAME)
                val meta = itemFrame.entityMeta as ItemFrameMeta
                itemFrame.setInstance(instance, Pos(x.toDouble(), y.toDouble(), z.toDouble(), yaw, 0f))
                meta.setNotifyAboutChanges(false)
                meta.orientation = ItemFrameMeta.Orientation.NORTH
                meta.isInvisible = true
                meta.item = ItemStack.builder(Material.FILLED_MAP)
                    .meta(MapMeta::class.java) { builder: MapMeta.Builder -> builder.mapId(i) }
                    .build()
                meta.setNotifyAboutChanges(true)
            }
        }

        /**
         * Creates packets from the image.
         * @param framebuffer The frame buffer to convert as packets.
         * @return The list of packets.
         */
        private fun mapPackets(framebuffer: LargeGraphics2DFramebuffer): Array<SendablePacket?> {
            val packets = arrayOfNulls<SendablePacket>(15)
            for (i in 0..14) {
                val x = i % 5
                val y = i / 5
                packets[i] = framebuffer.createSubView(x shl 7, y shl 7).preparePacket(i)
            }
            return packets
        }
    }

    private var packets: Array<SendablePacket?>? = null

    /**
     * Get the packets of the current Map Image.
     * This function generates and stores packets if they are not created.
     * @return The list of packets.
     */
    fun packets(): Array<SendablePacket?>? {
        return if (packets != null) packets else try {
            val framebuffer =
                LargeGraphics2DFramebuffer(widthBlocks * DEFAULT_RESOLUTION, heightBlocks * DEFAULT_RESOLUTION)
            javaClass.getResourceAsStream("/$resourceImageName")!!.use {
                val image = ImageIO.read(it)
                framebuffer.renderer.drawRenderedImage(image, AffineTransform.getScaleInstance(1.0, 1.0))
                packets = mapPackets(framebuffer)
            }
            packets
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}