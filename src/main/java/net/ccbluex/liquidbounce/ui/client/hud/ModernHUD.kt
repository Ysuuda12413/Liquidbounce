package net.ccbluex.liquidbounce.ui.client.hud

import net.ccbluex.liquidbounce.ui.utils.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import java.awt.Color

class ModernHUD(
    var barWidth: Int = 86,
    var barHeight: Int = 9,
    var barRadius: Float = 5f,
    var barAlpha: Int = 180,
    var iconSize: Int = 13,
    var detail: Boolean = false
) {
    private val mc: Minecraft
        get() = Minecraft.getMinecraft()

    // Animation values
    private var displayHealth = -1f
    private var displayAbsorption = -1f
    private var displayArmor = -1f
    private var displayExp = -1f
    private var displayFood = -1f
    private var displayAir = -1f

    private val ICON_HEART = ResourceLocation("liquidbounce/hud/icon-heart.png")
    private val ICON_SHIELD = ResourceLocation("liquidbounce/hud/icon-shield.png")
    private val ICON_FOOD = ResourceLocation("liquidbounce/hud/icon-food.png")

    fun drawHealthBar(x: Int, y: Int) {
        val player = mc.thePlayer ?: return
        displayHealth = lerp(displayHealth, player.health, 0.15f)
        drawIcon(ICON_HEART, x + (barWidth - iconSize) / 2, y, iconSize, iconSize)
        drawRoundedBar(
            x,
            y + iconSize + 2,
            barWidth,
            barHeight,
            (displayHealth / player.maxHealth).coerceIn(0f, 1f),
            Color(252, 65, 48, barAlpha),
            barRadius
        )
        if (detail) {
            val text = "${displayHealth.toInt()}/${player.maxHealth.toInt()}"
            mc.fontRendererObj.drawStringWithShadow(
                text,
                (x + barWidth + 10).toFloat(),
                (y + iconSize + 4).toFloat(),
                Color(252, 65, 48).rgb
            )
        }
    }

    fun drawAbsorptionBar(x: Int, y: Int) {
        val player = mc.thePlayer ?: return
        if (player.absorptionAmount <= 0f) return
        displayAbsorption = lerp(displayAbsorption, player.absorptionAmount, 0.15f)
        drawIcon(ICON_HEART, x + (barWidth - iconSize) / 2, y, iconSize, iconSize, Color(212, 175, 55, barAlpha))
        drawRoundedBar(
            x,
            y + iconSize + 2,
            barWidth,
            barHeight,
            (displayAbsorption / player.maxHealth).coerceIn(0f, 1f),
            Color(212, 175, 55, barAlpha),
            barRadius
        )
        if (detail) {
            val text = "${displayAbsorption.toInt()}/${player.maxHealth.toInt()}"
            mc.fontRendererObj.drawStringWithShadow(
                text,
                (x + barWidth + 10).toFloat(),
                (y + iconSize + 4).toFloat(),
                Color(212, 175, 55).rgb
            )
        }
    }

    fun drawArmorBar(x: Int, y: Int) {
        val player = mc.thePlayer ?: return
        if (player.totalArmorValue <= 0) return
        displayArmor = lerp(displayArmor, player.totalArmorValue.toFloat(), 0.15f)
        drawIcon(ICON_SHIELD, x + (barWidth - iconSize) / 2, y, iconSize, iconSize)
        drawRoundedBar(
            x,
            y + iconSize + 2,
            barWidth,
            barHeight,
            (displayArmor / 20f).coerceIn(0f, 1f),
            Color(73, 234, 214, barAlpha),
            barRadius
        )
        if (detail) {
            val text = "${displayArmor.toInt()}/20"
            mc.fontRendererObj.drawStringWithShadow(
                text,
                (x + barWidth + 10).toFloat(),
                (y + iconSize + 4).toFloat(),
                Color(73, 234, 214).rgb
            )
        }
    }

    fun drawExpBar(x: Int, y: Int) {
        val player = mc.thePlayer ?: return
        if (player.experienceLevel <= 0) return
        displayExp = lerp(displayExp, player.experience, 0.15f)
        drawIcon(ICON_SHIELD, x + (barWidth - iconSize) / 2, y, iconSize, iconSize, Color(136, 198, 87, barAlpha))
        drawRoundedBar(
            x,
            y + iconSize + 2,
            barWidth,
            barHeight,
            displayExp.coerceIn(0f, 1f),
            Color(136, 198, 87, barAlpha),
            barRadius
        )
        if (detail) {
            val text = "${(displayExp * 100).toInt()}% | Lv.${player.experienceLevel}"
            mc.fontRendererObj.drawStringWithShadow(
                text,
                (x + barWidth + 10).toFloat(),
                (y + iconSize + 4).toFloat(),
                Color(136, 198, 87).rgb
            )
        } else {
            mc.fontRendererObj.drawStringWithShadow(
                player.experienceLevel.toString(),
                (x + barWidth + 10).toFloat(),
                (y + iconSize + 4).toFloat(),
                Color(136, 198, 87).rgb
            )
        }
    }

    fun drawFoodBar(x: Int, y: Int) {
        val player = mc.thePlayer ?: return
        displayFood = lerp(displayFood, player.foodStats.foodLevel.toFloat(), 0.15f)
        drawIcon(ICON_FOOD, x + (barWidth - iconSize) / 2, y, iconSize, iconSize)
        drawRoundedBar(
            x,
            y + iconSize + 2,
            barWidth,
            barHeight,
            (displayFood / 20f).coerceIn(0f, 1f),
            Color(184, 132, 88, barAlpha),
            barRadius
        )
        if (detail) {
            val text = "${displayFood.toInt()}/20"
            mc.fontRendererObj.drawStringWithShadow(
                text,
                (x + barWidth + 10).toFloat(),
                (y + iconSize + 4).toFloat(),
                Color(184, 132, 88).rgb
            )
        }
    }

    fun drawAirBar(x: Int, y: Int) {
        val player = mc.thePlayer ?: return
        if (player.air >= 300) return
        displayAir = lerp(displayAir, player.air.toFloat(), 0.15f)
        drawIcon(ICON_FOOD, x + (barWidth - iconSize) / 2, y, iconSize, iconSize, Color(170, 193, 227, barAlpha))
        drawRoundedBar(
            x,
            y + iconSize + 2,
            barWidth,
            barHeight,
            (displayAir / 300f).coerceIn(0f, 1f),
            Color(170, 193, 227, barAlpha),
            barRadius
        )
        if (detail) {
            val text = "${displayAir.toInt()}/300"
            mc.fontRendererObj.drawStringWithShadow(
                text,
                (x + barWidth + 10).toFloat(),
                (y + iconSize + 4).toFloat(),
                Color(170, 193, 227).rgb
            )
        }
    }

    // --- Helper functions ---

    private fun lerp(current: Float, target: Float, speed: Float): Float {
        if (current < 0f) return target
        return current + (target - current) * speed
    }

    private fun drawRoundedBar(x: Int, y: Int, width: Int, height: Int, percent: Float, color: Color, radius: Float) {
        RenderUtils.drawRoundedRect2(
            x.toFloat(), y.toFloat(), x + width.toFloat(), y + height.toFloat(),
            Color(0, 0, 0, 90), radius
        )
        RenderUtils.drawRoundedRect2(
            x.toFloat(), y.toFloat(), x + (width * percent).toFloat(), y + height.toFloat(),
            color, radius
        )
    }

    private fun drawIcon(
        resource: ResourceLocation,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        color: Color = Color(255,255,255,255)
    ) {
        GlStateManager.enableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.color(
            color.red / 255.0f,
            color.green / 255.0f,
            color.blue / 255.0f,
            color.alpha / 255.0f
        )
        mc.textureManager.bindTexture(resource)
        RenderUtils.drawImage(resource, x, y, w, h, color)
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.disableAlpha()
        GlStateManager.disableBlend()
    }
}