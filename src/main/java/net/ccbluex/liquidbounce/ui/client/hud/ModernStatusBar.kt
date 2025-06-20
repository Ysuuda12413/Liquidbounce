package net.ccbluex.liquidbounce.ui.client.hud

import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import java.awt.Color
import kotlin.math.abs

object ModernStatusBar {
    private val ICON_HEART = ResourceLocation("liquidbounce/hud/icon-heart.png")
    private val ICON_FOOD = ResourceLocation("liquidbounce/hud/icon-food.png")
    private val ICON_SHIELD = ResourceLocation("liquidbounce/hud/icon-shield.png")

    private var renderHealth = 20f
    private var renderFood = 20f
    private var renderArmor = 20f

    fun draw(mc: Minecraft) {
        val player = mc.thePlayer ?: return
        val sr = ScaledResolution(mc)
        val barWidth = 110
        val barHeight = 12
        val barRadius = 7f
        val iconSize = 15
        val barAlpha = 180
        val spacing = 34

        // Animation mượt
        val health = player.health.coerceAtMost(player.maxHealth)
        val food = player.foodStats.foodLevel.toFloat()
        val armor = player.totalArmorValue.toFloat().coerceAtMost(20f)
        renderHealth = animate(renderHealth, health, 0.14f)
        renderFood = animate(renderFood, food, 0.14f)
        renderArmor = animate(renderArmor, armor, 0.14f)

        val barCount = 3
        val barTotalWidth = barWidth * barCount + spacing * (barCount - 1)
        val xBase = (sr.scaledWidth / 2) - (barTotalWidth / 2)
        val yBase = sr.scaledHeight - barHeight - 10

        // Health
        val healthPercent = renderHealth / player.maxHealth
        val healthX = xBase
        RenderUtils.drawRoundedRect2(
            healthX.toFloat(), yBase.toFloat(),
            (healthX + barWidth).toFloat(), (yBase + barHeight).toFloat(),
            Color(0, 0, 0, 90), barRadius
        )
        RenderUtils.drawRoundedRect2(
            healthX.toFloat(), yBase.toFloat(),
            (healthX + (barWidth * healthPercent)).toFloat(), (yBase + barHeight).toFloat(),
            Color(252, 65, 48, barAlpha), barRadius
        )
        drawIcon(mc, ICON_HEART, healthX + 4, yBase + (barHeight - iconSize) / 2, iconSize, iconSize)

        // Armor
        val armorPercent = renderArmor / 20f
        val armorX = healthX + barWidth + spacing
        RenderUtils.drawRoundedRect2(
            armorX.toFloat(), yBase.toFloat(),
            (armorX + barWidth).toFloat(), (yBase + barHeight).toFloat(),
            Color(0, 0, 0, 90), barRadius
        )
        RenderUtils.drawRoundedRect2(
            armorX.toFloat(), yBase.toFloat(),
            (armorX + (barWidth * armorPercent)).toFloat(), (yBase + barHeight).toFloat(),
            Color(73, 234, 214, barAlpha), barRadius
        )
        drawIcon(mc, ICON_SHIELD, armorX + 4, yBase + (barHeight - iconSize) / 2, iconSize, iconSize)

        // Food
        val foodPercent = renderFood / 20f
        val foodX = armorX + barWidth + spacing
        RenderUtils.drawRoundedRect2(
            foodX.toFloat(), yBase.toFloat(),
            (foodX + barWidth).toFloat(), (yBase + barHeight).toFloat(),
            Color(0, 0, 0, 90), barRadius
        )
        RenderUtils.drawRoundedRect2(
            foodX.toFloat(), yBase.toFloat(),
            (foodX + (barWidth * foodPercent)).toFloat(), (yBase + barHeight).toFloat(),
            Color(184, 132, 88, barAlpha), barRadius
        )
        drawIcon(mc, ICON_FOOD, foodX + 4, yBase + (barHeight - iconSize) / 2, iconSize, iconSize)
    }

    private fun animate(current: Float, target: Float, speed: Float): Float {
        return if (abs(current - target) < 0.01f) target
        else current + (target - current) * speed
    }

    private fun drawIcon(mc: Minecraft, resource: ResourceLocation, x: Int, y: Int, w: Int, h: Int, color: Color = Color(255,255,255,255)) {
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