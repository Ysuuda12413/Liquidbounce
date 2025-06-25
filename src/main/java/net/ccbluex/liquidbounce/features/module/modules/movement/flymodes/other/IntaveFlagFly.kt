/*
 * LiquidBounce Hacked Client
 * IntaveFlagFly: Fly mode an toàn tối đa cho Intave, ưu tiên không bị flag.
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.ui.utils.movement.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer

object IntaveFlagFly : FlyMode("IntaveFlagFly") {
    private var tickCount = 0

    override fun onEnable() {
        tickCount = 0
    }

    override fun onUpdate() {
        val player = mc.thePlayer ?: return
        player.motionY = if (tickCount % 12 == 0) 0.022 else -0.012 + ((Math.random() - 0.5) * 0.007)
        player.fallDistance = 0f
        if (player.ticksExisted % 3 == 0) {
            player.onGround = true
            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
        }
        if (player.moveForward != 0f || player.moveStrafing != 0f) {
            MovementUtils.strafe(0.15f + Math.random().toFloat() * 0.02f)
        }

        tickCount++
        if (tickCount >= Fly.maxFlyTicksValue) {
            player.motionY = -0.08
        }
    }

    override fun onDisable() {
        tickCount = 0
    }
}