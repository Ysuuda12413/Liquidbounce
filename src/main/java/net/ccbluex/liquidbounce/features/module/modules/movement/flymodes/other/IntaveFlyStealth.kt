/*
 * LiquidBounce Hacked Client
 * IntaveFlyStealth: Fly mode an toàn tối đa cho Intave, ưu tiên không bị flag.
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other

import net.ccbluex.liquidbounce.event.BlockBBEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.ui.utils.extensions.isMoving
import net.ccbluex.liquidbounce.ui.utils.movement.MovementUtils
import net.minecraft.block.BlockLadder
import net.minecraft.block.material.Material
import net.minecraft.util.AxisAlignedBB
import net.minecraft.network.play.client.C03PacketPlayer

object IntaveFlyStealth : FlyMode("IntaveFlyStealth") {
    private var baseY = 0.0f
    private var hoverTicks = 0
    private var lastTouchGroundTick = 0

    override fun onEnable() {
        baseY = mc.thePlayer?.posY?.toFloat() ?: 0.0f
        hoverTicks = 0
        lastTouchGroundTick = 0
    }

    override fun onUpdate() {
        val player = mc.thePlayer ?: return
        if (player.onGround) {
            baseY = player.posY.toFloat()
            hoverTicks = 0
            lastTouchGroundTick = player.ticksExisted
        }

        player.motionY = when {
            mc.gameSettings.keyBindJump.isKeyDown -> (0.32f + Math.random().toFloat() * 0.012f).toDouble()
            mc.gameSettings.keyBindSneak.isKeyDown -> (-0.18f - Math.random().toFloat() * 0.012f).toDouble()
            else -> (if (hoverTicks % 14 == 0) 0.014f else -0.012f + ((Math.random() - 0.5) * 0.007).toFloat()).toDouble()
        }
        hoverTicks++

        player.fallDistance = 0f

        // Mỗi 3 tick gửi onGround true để reset fall và ground check
        if (player.ticksExisted % 3 == 0) {
            player.onGround = true
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
        }

        // Sử dụng MovementUtils.isMoving() đúng cách!
        if (mc.thePlayer?.isMoving == true) {
            val speed = 0.16f + Math.random().toFloat() * 0.021f
            MovementUtils.strafe(speed)
        }

        // Mỗi 40-60 tick fake chạm đất thật sự để tránh hover flag
        if (hoverTicks % (40 + (Math.random() * 20).toInt()) == 0) {
            val yTouch = baseY - 0.08f - Math.random().toFloat() * 0.08f
            player.setPosition(player.posX, yTouch.toDouble(), player.posZ)
            mc.netHandler.addToSendQueue(
                C03PacketPlayer.C04PacketPlayerPosition(
                    player.posX, yTouch.toDouble(), player.posZ, true
                )
            )
            player.onGround = true
            lastTouchGroundTick = player.ticksExisted
        }
    }

    override fun onMove(event: MoveEvent) {
        event.y = mc.thePlayer?.motionY?.toDouble() ?: 0.0
    }

    override fun onBB(event: BlockBBEvent) {
        val yCheck =
            if (mc.gameSettings.keyBindJump.isKeyDown)
                event.y.toFloat() <= baseY + 0.51f
            else if (mc.gameSettings.keyBindSneak.isKeyDown)
                event.y.toFloat() < baseY - 0.51f
            else
                event.y.toFloat() <= baseY

        if ((!event.block.material.blocksMovement() &&
                    event.block.material != Material.carpet &&
                    event.block.material != Material.vine &&
                    event.block.material != Material.snow &&
                    event.block !is BlockLadder) && yCheck) {
            event.boundingBox = AxisAlignedBB.fromBounds(
                event.x.toDouble(),
                event.y.toDouble(),
                event.z.toDouble(),
                event.x.toDouble() + 1,
                event.y.toDouble() + 0.51,
                event.z.toDouble() + 1
            )
        }
    }
}