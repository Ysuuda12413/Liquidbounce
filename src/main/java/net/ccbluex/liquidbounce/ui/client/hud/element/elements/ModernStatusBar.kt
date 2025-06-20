package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import java.awt.Color
import kotlin.math.abs

@ElementInfo(name = "ModernStatusBar")
class ModernStatusBar(
    x: Double = 0.0, y: Double = 0.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.DOWN)
) : Element("ModernStatusBar", x, y, scale, side) {

    private val barWidth by int("Width", 110, 50..300)
    private val barHeight by int("Height", 12, 6..20)
    private val barRadius by float("Radius", 7f, 2f..10f)
    private val spacing by int("Spacing", 34, 10..40)
    private val iconSize by int("IconSize", 15, 8..20)
    private val barAlpha by int("Alpha", 180, 50..255)
    private val animateSpeed by float("AnimSpeed", 0.14f, 0.01f..0.5f)

    private val ICON_HEART = ResourceLocation("liquidbounce/hud/icon-heart.png")
    private val ICON_FOOD = ResourceLocation("liquidbounce/hud/icon-food.png")
    private val ICON_SHIELD = ResourceLocation("liquidbounce/hud/icon-shield.png")
    // Animation value
    private var renderHealth = 20f
    private var renderFood = 20f
    private var renderArmor = 20f

    override fun drawElement(): Border? {
        val player = mc.thePlayer ?: return Border(0f, 0f, 0f, 0f)
        val sr = ScaledResolution(mc)
        val width = barWidth
        val height = barHeight
        val radius = barRadius
        val iconS = iconSize
        val alpha = barAlpha

        // Animation mượt
        val health = player.health.coerceAtMost(player.maxHealth)
        val food = player.foodStats.foodLevel.toFloat()
        val armor = player.totalArmorValue.toFloat().coerceAtMost(20f)
        renderHealth = animate(renderHealth, health, animateSpeed)
        renderFood = animate(renderFood, food, animateSpeed)
        renderArmor = animate(renderArmor, armor, animateSpeed)

        // Tổng chiều rộng
        val barCount = 3
        val barSpacing = spacing // spacing custom
        val barTotalWidth = width * barCount + barSpacing * (barCount - 1)
        val xBase = (sr.scaledWidth / 2) - (barTotalWidth / 2)
        val yBase = sr.scaledHeight - height - 10 // sát đáy

        // Health
        val healthPercent = renderHealth / player.maxHealth
        val healthX = xBase
        RenderUtils.drawRoundedRect2(
            healthX.toFloat(), yBase.toFloat(),
            (healthX + width).toFloat(), (yBase + height).toFloat(),
            Color(0, 0, 0, 90), radius
        )
        RenderUtils.drawRoundedRect2(
            healthX.toFloat(), yBase.toFloat(),
            (healthX + (width * healthPercent)).toFloat(), (yBase + height).toFloat(),
            Color(252, 65, 48, alpha), radius
        )
        drawIcon(ICON_HEART, healthX + 4, yBase + (height - iconS) / 2, iconS, iconS)

        // Armor
        val armorPercent = renderArmor / 20f
        val armorX = healthX + width + barSpacing
        RenderUtils.drawRoundedRect2(
            armorX.toFloat(), yBase.toFloat(),
            (armorX + width).toFloat(), (yBase + height).toFloat(),
            Color(0, 0, 0, 90), radius
        )
        RenderUtils.drawRoundedRect2(
            armorX.toFloat(), yBase.toFloat(),
            (armorX + (width * armorPercent)).toFloat(), (yBase + height).toFloat(),
            Color(73, 234, 214, alpha), radius
        )
        drawIcon(ICON_SHIELD, armorX + 4, yBase + (height - iconS) / 2, iconS, iconS)

        // Food
        val foodPercent = renderFood / 20f
        val foodX = armorX + width + barSpacing
        RenderUtils.drawRoundedRect2(
            foodX.toFloat(), yBase.toFloat(),
            (foodX + width).toFloat(), (yBase + height).toFloat(),
            Color(0, 0, 0, 90), radius
        )
        RenderUtils.drawRoundedRect2(
            foodX.toFloat(), yBase.toFloat(),
            (foodX + (width * foodPercent)).toFloat(), (yBase + height).toFloat(),
            Color(184, 132, 88, alpha), radius
        )
        drawIcon(ICON_FOOD, foodX + 4, yBase + (height - iconS) / 2, iconS, iconS)

        return Border(
            healthX.toFloat(),
            yBase.toFloat(),
            (foodX + width - healthX).toFloat(),
            height.toFloat()
        )
    }

    private fun animate(current: Float, target: Float, speed: Float): Float {
        return if (abs(current - target) < 0.01f) target
        else current + (target - current) * speed
    }

    private fun drawIcon(resource: ResourceLocation, x: Int, y: Int, w: Int, h: Int, color: Color = Color(255,255,255,255)) {
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
        fun default(): ModernStatusBar = ModernStatusBar(x = 0.0, y = 0.0, scale = 1F)
    }
}