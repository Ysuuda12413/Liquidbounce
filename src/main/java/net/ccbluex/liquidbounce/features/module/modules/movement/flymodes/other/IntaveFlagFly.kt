/*
 * LiquidBounce Hacked Client
 * IntaveFlagFly: Fly mode an toàn tối đa cho Intave, ưu tiên không bị flag.
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.other

import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.ui.utils.movement.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S27PacketExplosion
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.EventTarget

object IntaveFlagFly : FlyMode("IntaveFlagFly") {
    private var tickCount = 0
    private var boosting = false
    private var boostTicks = 0

    override fun onEnable() {
        tickCount = 0
        boosting = false
        boostTicks = 0
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S27PacketExplosion) {
            boosting = true
            boostTicks = 0
        }
    }

    override fun onUpdate() {
        val player = mc.thePlayer ?: return

        if (boosting) {
            // Boost cực mạnh, random nhẹ để hỗ trợ bypass Intave
            val boostValue = 2.2 + Math.random() * 0.25 // Có thể điều chỉnh để bay xa hơn/nhiều hơn
            val yawRad = Math.toRadians(player.rotationYaw.toDouble())
            player.motionX = -Math.sin(yawRad) * boostValue + ((Math.random() - 0.5) * 0.07)
            player.motionZ =  Math.cos(yawRad) * boostValue + ((Math.random() - 0.5) * 0.07)
            player.motionY = 0.42 + (Math.random() - 0.5) * 0.08
            player.fallDistance = 0f
            boostTicks++
            if (boostTicks >= Fly.maxFlyTicksValue) {
                boosting = false
                player.motionX = 0.0
                player.motionZ = 0.0
            }
            return // Khi boosting thì không chạy logic thường
        }

        // Logic thường (giữ an toàn cho Intave)
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
        boosting = false
        boostTicks = 0
    }
}
