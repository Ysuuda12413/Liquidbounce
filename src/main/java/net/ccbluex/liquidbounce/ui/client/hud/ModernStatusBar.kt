package net.ccbluex.liquidbounce.ui.client.hud

import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color
import kotlin.math.abs

object ModernStatusBar {
    private val ICON_HEART = ResourceLocation("liquidbounce/hud/icon-heart.png")
    private val ICON_FOOD = ResourceLocation("liquidbounce/hud/icon-food.png")
    private val ICON_SHIELD = ResourceLocation("liquidbounce/hud/icon-shield.png")

    private var renderHealth = 20f
    private var renderFood = 20f
    private var renderArmor = 20f

    /**
     * Vẽ thanh ModernStatusBar ở giữa màn hình như phong cách cũ.
     */
    fun draw(mc: Minecraft) {
        val player = mc.thePlayer ?: return
        val sr = ScaledResolution(mc)
        val barWidth = 110
        val barHeight = 12
        val barRadius = 7f
        val iconSize = 15
        val barAlpha = 180
        val spacing = 34

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

    /**
     * Vẽ từng thanh trạng thái ở vị trí mong muốn (theo chuẩn HUD Minecraft vanilla).
     * healthPos, armorPos, foodPos: Pair<Int, Int> = (x, y) từng thanh.
     */
    fun drawAtPositions(
        mc: Minecraft,
        healthPos: Pair<Int, Int>,
        armorPos: Pair<Int, Int>,
        foodPos: Pair<Int, Int>
    ) {
        val player = mc.thePlayer ?: return

        // Thông số size lấy gần giống vanilla Minecraft
        val barWidth = 81
        val barHeight = 9
        val barRadius = 4f
        val iconSize = 9
        val barAlpha = 180

        val health = player.health.coerceAtMost(player.maxHealth)
        val food = player.foodStats.foodLevel.toFloat()
        val armor = player.totalArmorValue.toFloat().coerceAtMost(20f)

        renderHealth = animate(renderHealth, health, 0.14f)
        renderFood = animate(renderFood, food, 0.14f)
        renderArmor = animate(renderArmor, armor, 0.14f)

        // Health
        val healthPercent = renderHealth / player.maxHealth
        RenderUtils.drawRoundedRect2(
            healthPos.first.toFloat(), healthPos.second.toFloat(),
            (healthPos.first + barWidth).toFloat(), (healthPos.second + barHeight).toFloat(),
            Color(0, 0, 0, 90), barRadius
        )
        RenderUtils.drawRoundedRect2(
            healthPos.first.toFloat(), healthPos.second.toFloat(),
            (healthPos.first + (barWidth * healthPercent)).toFloat(), (healthPos.second + barHeight).toFloat(),
            Color(252, 65, 48, barAlpha), barRadius
        )
        drawIcon(mc, ICON_HEART, healthPos.first - iconSize - 2, healthPos.second, iconSize, iconSize)

        // Armor
        val armorPercent = renderArmor / 20f
        RenderUtils.drawRoundedRect2(
            armorPos.first.toFloat(), armorPos.second.toFloat(),
            (armorPos.first + barWidth).toFloat(), (armorPos.second + barHeight).toFloat(),
            Color(0, 0, 0, 90), barRadius
        )
        RenderUtils.drawRoundedRect2(
            armorPos.first.toFloat(), armorPos.second.toFloat(),
            (armorPos.first + (barWidth * armorPercent)).toFloat(), (armorPos.second + barHeight).toFloat(),
            Color(73, 234, 214, barAlpha), barRadius
        )
        drawIcon(mc, ICON_SHIELD, armorPos.first - iconSize - 2, armorPos.second, iconSize, iconSize)

        // Food
        val foodPercent = renderFood / 20f
        RenderUtils.drawRoundedRect2(
            foodPos.first.toFloat(), foodPos.second.toFloat(),
            (foodPos.first + barWidth).toFloat(), (foodPos.second + barHeight).toFloat(),
            Color(0, 0, 0, 90), barRadius
        )
        RenderUtils.drawRoundedRect2(
            foodPos.first.toFloat(), foodPos.second.toFloat(),
            (foodPos.first + (barWidth * foodPercent)).toFloat(), (foodPos.second + barHeight).toFloat(),
            Color(184, 132, 88, barAlpha), barRadius
        )
        drawIcon(mc, ICON_FOOD, foodPos.first - iconSize - 2, foodPos.second, iconSize, iconSize)
    }

    private fun animate(current: Float, target: Float, speed: Float): Float {
        return if (abs(current - target) < 0.01f) target
        else current + (target - current) * speed
    }

    private fun drawIcon(
        mc: Minecraft,
        resource: ResourceLocation,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        color: Color = Color(255, 255, 255, 255)
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