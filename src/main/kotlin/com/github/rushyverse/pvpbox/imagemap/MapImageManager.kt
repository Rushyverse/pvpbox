package com.github.rushyverse.pvpbox.imagemap

import net.minestom.server.network.packet.server.SendablePacket

class MapImageManager {

    val mapImageList: ArrayList<MapImage> = arrayListOf()

    fun registerMap(image: MapImage) {
        mapImageList.add(image)

        val packets = image.packets()

        packets?.toList()?.let { packet ->
            image.instance.players.forEach { player ->
                player.sendPackets(packet)
            }
        };
    }

    fun packets(): Array<SendablePacket?>? {
        val list = arrayListOf<SendablePacket?>()



        mapImageList.forEach { map ->
            map.packets()?.toList()?.let { packets ->
                list.addAll(packets)
            }
        }

        return list.toTypedArray()
    }
}