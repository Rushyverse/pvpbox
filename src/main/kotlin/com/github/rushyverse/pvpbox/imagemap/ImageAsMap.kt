package com.github.rushyverse.pvpbox.imagemap

import com.github.rushyverse.pvpbox.PvpboxServer
import net.minestom.server.coordinate.Point
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
import java.io.InputStream
import javax.imageio.ImageIO


internal object ImageAsMap {
    private var packets: Array<SendablePacket?>? = null

    fun packets(resourceImageName: String, widthBlocks: Int, heightBlocks: Int): Array<SendablePacket?>? {
        return if (packets != null) packets else try {
            val framebuffer = LargeGraphics2DFramebuffer(widthBlocks * 128, heightBlocks * 128)
            val imageStream: InputStream = PvpboxServer::class.java.getResourceAsStream("/$resourceImageName")!!
            val image = ImageIO.read(imageStream)
            framebuffer.renderer.drawRenderedImage(image, AffineTransform.getScaleInstance(1.0, 1.0))
            packets = mapPackets(framebuffer)
            packets
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Creates the maps on the board in the lobby
     */
    fun create(instance: Instance, maximum: Point) {
        val maxX = maximum.blockX()
        val maxY = maximum.blockY()
        val z = maximum.blockZ()
        for (i in 0..14) {
            val x = maxX - i % 5
            val y = maxY - i / 5
            val itemFrame = Entity(EntityType.ITEM_FRAME)
            val meta = itemFrame.entityMeta as ItemFrameMeta
            itemFrame.setInstance(instance, Pos(x.toDouble(), y.toDouble(), z.toDouble(), 180f, 0f))
            meta.setNotifyAboutChanges(false)
            meta.orientation = ItemFrameMeta.Orientation.NORTH
            meta.isInvisible = true
            meta.item = ItemStack.builder(Material.FILLED_MAP)
                .meta(
                    MapMeta::class.java
                ) { builder: MapMeta.Builder -> builder.mapId(i) }
                .build()
            meta.setNotifyAboutChanges(true)
        }
    }

    /**
     * Creates packets for maps that will display an image on the board in the lobby
     */
    private fun mapPackets(framebuffer: LargeGraphics2DFramebuffer): Array<SendablePacket?> {
        val packets = arrayOfNulls<SendablePacket>(15)
        for (i in 0..14) {
            val x = i % 5
            val y = i / 5
            packets[i] = framebuffer.createSubView(x * 128, y * 128).preparePacket(i)
        }
        return packets
    }
}