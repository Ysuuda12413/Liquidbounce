package net.ccbluex.liquidbounce.ui.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import java.awt.Color

@ElementInfo(name = "ModernStatusBar")
class ModernStatusBar(
    x: Double = 20.0, y: Double = 32.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.DOWN)
) : Element("ModernStatusBar", x, y, scale, side) {

    private val barWidth by int("Width", 86, 40..200)
    private val barHeight by int("Height", 9, 5..20)
    private val barRadius by float("Radius", 5f, 2f..8f)
    private val barAlpha by int("Alpha", 180, 50..255)
    private val spacing by int("Spacing", 13, 4..20)
    private val iconSize by int("IconSize", 13, 8..18)

    private val showAbsorption by boolean("Absorption", true)
    private val showArmor by boolean("Armor", true)
    private val showExp by boolean("Exp", true)
    private val showFood by boolean("Food", true)
    private val showAir by boolean("Air", true)

    private val ICON_HEART = ResourceLocation("liquidbounce/hud/icon-heart.png")
    private val ICON_SHIELD = ResourceLocation("liquidbounce/hud/icon-shield.png")
    private val ICON_FOOD = ResourceLocation("liquidbounce/hud/icon-food.png")

    override fun drawElement(): Border? {
        val player = mc.thePlayer ?: return Border(0f, 0f, 0f, 0f)
        val width = barWidth
        val height = barHeight
        val radius = barRadius
        val sp = spacing
        val iconS = iconSize
        val alpha = barAlpha

        var yOffset = 0

        // Health
        drawIcon(ICON_HEART, 0, yOffset + (height - iconS) / 2, iconS, iconS)
        drawRoundedBar(iconS + 4, yOffset, width, height, player.health / player.maxHealth, Color(252, 65, 48, alpha), radius)
        yOffset -= sp

        // Absorption
        if (showAbsorption) {
            val absorption = player.absorptionAmount
            if (absorption > 0) {
                drawIcon(ICON_HEART, 0, yOffset + (height - iconS) / 2, iconS, iconS, Color(212, 175, 55, alpha))
                drawRoundedBar(iconS + 4, yOffset, width, height, absorption.coerceAtMost(player.maxHealth) / player.maxHealth, Color(212, 175, 55, alpha), radius)
                yOffset -= sp
            }
        }

        // Armor
        if (showArmor) {
            val armor = player.totalArmorValue
            if (armor > 0) {
                drawIcon(ICON_SHIELD, 0, yOffset + (height - iconS) / 2, iconS, iconS)
                drawRoundedBar(iconS + 4, yOffset, width, height, armor / 20.0f, Color(73, 234, 214, alpha), radius)
                yOffset -= sp
            }
        }

        // Exp
        if (showExp) {
            val expLevel = player.experienceLevel
            val expProgress = player.experience
            if (expLevel > 0) {
                drawIcon(ICON_SHIELD, 0, yOffset + (height - iconS) / 2, iconS, iconS, Color(136, 198, 87, alpha))
                drawRoundedBar(iconS + 4, yOffset, width, height, expProgress, Color(136, 198, 87, alpha), radius)
                mc.fontRendererObj.drawStringWithShadow(
                    expLevel.toString(), (iconS + width + 10).toFloat(), (yOffset + 1).toFloat(), Color(136, 198, 87).rgb
                )
                yOffset -= sp
            }
        }

        // Food (right)
        if (showFood) {
            drawIcon(ICON_FOOD, width + 30, 0, iconS, iconS)
            val food = player.foodStats.foodLevel
            drawRoundedBar(width + iconS + 34, 0, width, height, food / 20.0f, Color(184, 132, 88, alpha), radius)
        }

        // Air (right, above food)
        if (showAir) {
            val air = player.air
            val maxAir = 300
            if (air < maxAir) {
                drawIcon(ICON_FOOD, width + 30, -sp, iconS, iconS, Color(170, 193, 227, alpha))
                drawRoundedBar(width + iconS + 34, -sp, width, height, air / maxAir.toFloat(), Color(170, 193, 227, alpha), radius)
            }
        }

        // Designer border
        return Border(0f, yOffset.toFloat(), (width * 2 + 80).toFloat(), height.toFloat())
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

    companion object {
        fun default(): ModernStatusBar = ModernStatusBar()
    }
}