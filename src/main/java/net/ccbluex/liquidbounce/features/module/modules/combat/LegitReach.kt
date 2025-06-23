package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.*
import net.ccbluex.liquidbounce.utils.attack.EntityUtils
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C02PacketUseEntity.Action
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.INetHandlerPlayClient
import net.ccbluex.liquidbounce.utils.timing.MSTimer
object LegitReach : Module("LegitReach",Category.COMBAT) {

    private val packets = mutableListOf<Packet<INetHandlerPlayClient>>()
    private val pulseTimer = MSTimer()
    private val packetDelay by int ("Packets", 5, 0..30)

    private var fakePlayer: EntityOtherPlayerMP? = null
    private var currentTarget: EntityLivingBase? = null

    override fun onDisable() {
        removeFakePlayer()
        packets.clear()
    }

    private fun removeFakePlayer() {
        fakePlayer?.let {
            mc.theWorld.removeEntity(it)
            fakePlayer = null
        }
        currentTarget = null
    }
    fun onAttack(event: AttackEvent) {
        if (fakePlayer == null) {
            val target = event.targetEntity as? EntityLivingBase ?: return
            val profile = mc.netHandler.getPlayerInfo(target.uniqueID)?.gameProfile ?: return

            val faker = EntityOtherPlayerMP(mc.theWorld, profile).apply {
                copyLocationAndAnglesFrom(target)
                health = target.health
                rotationYawHead = target.rotationYawHead
                renderYawOffset = target.renderYawOffset
                for (index in 0..4) {
                    setCurrentItemOrArmor(index, target.getEquipmentInSlot(index))
                }
            }

            mc.theWorld.addEntityToWorld(-1337, faker)
            fakePlayer = faker
            currentTarget = target
        } else {
            if (event.targetEntity == fakePlayer) {
                attackEntity(currentTarget ?: return)
            } else {
                removeFakePlayer()
            }
        }
    }

    private fun attackEntity(entity: EntityLivingBase) {
        mc.thePlayer?.let {
            it.swingItem()
            mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, Action.ATTACK))
            it.attackTargetEntityWithCurrentItem(entity)
        }
    }

    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return

        if (currentTarget?.isDead == true) {
            removeFakePlayer()
            return
        }

        if (mc.thePlayer.ticksExisted % packetDelay == 0 && fakePlayer != null && currentTarget != null) {
            fakePlayer!!.copyLocationAndAnglesFrom(currentTarget!!)
            fakePlayer!!.rotationYawHead = currentTarget!!.rotationYawHead
            fakePlayer!!.renderYawOffset = currentTarget!!.renderYawOffset
        }
    }

    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S14PacketEntity) {
            if (packet.getEntity(mc.theWorld) == currentTarget) {
                event.cancelEvent()
                packets.add(packet as Packet<INetHandlerPlayClient>)
            }
        }
    }
}
