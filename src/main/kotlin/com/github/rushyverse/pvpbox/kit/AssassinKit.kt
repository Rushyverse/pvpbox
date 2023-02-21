package com.github.rushyverse.pvpbox.kit

import com.github.rushyverse.api.entity.PlayerNPCEntity
import com.github.rushyverse.api.extension.withoutDecorations
import com.github.rushyverse.pvpbox.kit.commons.AbstractKit
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.color.Color
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.*
import net.minestom.server.entity.metadata.other.ArmorStandMeta
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.instance.Instance
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.*
import net.minestom.server.item.metadata.LeatherArmorMeta
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import java.util.*


class AssassinKit: AbstractKit(
    "assassin",
    Material.NETHER_STAR
) {

    companion object {
        val aliveKnifes = HashMap<UUID, KnifeThread>();
        private val ARMOR_COLOR = Color(48, 48, 48)

        private fun buildMeta() = LeatherArmorMeta.Builder()
            .color(ARMOR_COLOR)
            .enchantment(Enchantment.PROTECTION, 1)
            .build()

        private val HELMET = ItemStack
            .builder(Material.LEATHER_HELMET)
            .meta(buildMeta())
            .build()

        private val CHESTPLATE = ItemStack
            .builder(Material.LEATHER_CHESTPLATE)
            .meta(buildMeta())
            .build()

        private val LEGGINGS = ItemStack
            .builder(Material.LEATHER_LEGGINGS)
            .meta(buildMeta())
            .build()

        private val BOOTS = ItemStack
            .builder(Material.LEATHER_BOOTS)
            .meta(buildMeta())
            .build()

        private val SWORD = ItemStack
            .builder(Material.IRON_SWORD)
            .meta { it.enchantment(Enchantment.SHARPNESS, 1) }
            .build()

        private val KNIFE = ItemStack
            .builder(Material.GHAST_TEAR)
            .displayName(Component.text("Knife", NamedTextColor.RED)
                .append(Component.text(" (Right Click)", NamedTextColor.GRAY))
                .withoutDecorations()
            )
            .lore(Component.text("You can throw the knife !", NamedTextColor.GOLD))
            .meta {
                it.enchantment(Enchantment.INFINITY, 0)
                it.hideFlag(ItemHideFlag.HIDE_ENCHANTS)
            }
            .build()

        private val EFFECT = Potion(PotionEffect.SPEED, 1, 99999)
    }


    private fun spawnFakeplayer(instance:Instance, pos: Pos, name: String, viewer:Player) {
        val fp = PlayerNPCEntity(name)
        fp.setInstance(instance)
        fp.teleport(pos)
        fp.addViewer(viewer)
    }

    override fun applyKit(player: Player, inv: PlayerInventory) {
        inv.helmet = HELMET
        inv.chestplate = CHESTPLATE
        inv.leggings = LEGGINGS
        inv.boots = BOOTS

        inv.addItemStack(SWORD)
        inv.addItemStack(KNIFE)

        player.addEffect(EFFECT)

        spawnFakeplayer(player.instance!!, player.position, "John", player);
    }

    class KnifeListener : EventListener<PlayerUseItemEvent> {

        override fun eventType(): Class<PlayerUseItemEvent> {
            return PlayerUseItemEvent::class.java
        }

        override fun run(event: PlayerUseItemEvent): EventListener.Result {
            val player = event.player
            val item = event.itemStack
            val slot = player.heldSlot.toInt()
            val instance = player.instance

            if (!item.isSimilar(KNIFE)) {
                return EventListener.Result.INVALID
            }

            if (aliveKnifes.containsKey(player.uuid)) {
                player.sendMessage("Your knife is already launched")
                return EventListener.Result.INVALID
            }

            val vector = player.position.direction()

            val knifeEntity: LivingEntity = spawnKnife(instance!!, player.position)

            val thread = KnifeThread(player, knifeEntity, vector)
            val scheduler = MinecraftServer.getSchedulerManager()
                .buildTask(thread)
                .repeat(TaskSchedule.millis((250)))

            thread.task = scheduler.schedule()

            aliveKnifes[player.uuid] = thread

            return EventListener.Result.SUCCESS
        }

        private fun spawnKnife(instance: Instance, pos: Pos): LivingEntity {
            val knifeEntity = LivingEntity(EntityType.ARMOR_STAND)
            val meta = knifeEntity.entityMeta as ArmorStandMeta

            meta.isSmall = false
            meta.isHasNoBasePlate = true

            knifeEntity.itemInMainHand = KNIFE
            knifeEntity.isInvisible = true
            knifeEntity.isAutoViewable = true
            knifeEntity.setNoGravity(true)
            knifeEntity.setInstance(instance)
            knifeEntity.teleport(pos.add(0.0, 0.5, 0.0))
            return knifeEntity
        }
    }

    class KnifeThread(
        val player: Player,
        val knifeEntity: LivingEntity,
        val vector: Vec,
        val startPosition: Pos = knifeEntity.position
    ) : Runnable {
        lateinit var task: Task
        private var previousPos: Pos? = null

        override fun run() {
            val instance = knifeEntity.instance!!
            val currentPosition = knifeEntity.position
            val previousPos = previousPos

            // Check if entity blocked
            if (previousPos != null) {
                val distPrevPos = previousPos.distance(currentPosition)
                if (distPrevPos < 2) {
                    player.sendMessage("end cause entity is blocked")
                    endEntity(null,knifeEntity,player)
                    return
                }
            }

            // Check the entity distance from startPosition
            val distance = startPosition.distance(currentPosition)
            if (distance >= 10) {
                endEntity(null, knifeEntity, player)
                player.sendMessage("Knife distance exceeded (10blocks)")
                return;
            }

            knifeEntity.velocity = vector.mul(20.0)

            // CHECK COLLISION
            val entityContact = instance.getNearbyEntities(currentPosition, 2.0)
            if (!entityContact.isEmpty()) {
                val currentEntityContact = entityContact.first() as LivingEntity

                if (currentEntityContact.uuid != player.uuid && currentEntityContact.uuid != knifeEntity.uuid) {
                    player.sendMessage("Ton knife a touché : ${currentEntityContact.customName}")

                    endEntity(currentEntityContact, knifeEntity, player)
                    return
                }
            }

            this.previousPos = knifeEntity.position
        }

        private fun endEntity(entityContact: LivingEntity?, entityAS: Entity, player: Player) {
            player.sendMessage("END ENTITY & TASK")
            task.cancel()

            entityAS.remove()
            aliveKnifes.remove(player.uuid)

            entityContact?.kill()
        }

    }
}
