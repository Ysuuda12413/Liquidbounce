/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.LiquidBounce.CLIENT_NAME
import net.ccbluex.liquidbounce.ui.client.hud.ModernHUD
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Element.Companion.MAX_GRADIENT_COLORS
import net.ccbluex.liquidbounce.ui.utils.render.ColorSettingsFloat
import net.ccbluex.liquidbounce.ui.utils.render.ColorSettingsInteger
import net.minecraft.client.gui.GuiChat
import net.minecraft.util.ResourceLocation
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution

object HUD : Module("HUD", Category.RENDER, gameDetecting = false, defaultState = true, defaultHidden = true) {
    val customHotbar by boolean("CustomHotbar", true)
    val smoothHotbarSlot by boolean("SmoothHotbarSlot", true) { customHotbar }
    val modernHud by boolean("ModernHud", false)
    val roundedHotbarRadius by float("RoundedHotbar-Radius", 3F, 0F..5F) { customHotbar }

    val hotbarMode by choices("Hotbar-Color", arrayOf("Custom", "Rainbow", "Gradient"), "Custom") { customHotbar }
    val hbHighlightColors = ColorSettingsInteger(this, "Hotbar-Highlight-Colors", applyMax = true)
    { customHotbar }.with(a = 0)
    val hbBackgroundColors = ColorSettingsInteger(this, "Hotbar-Background-Colors")
    { customHotbar && hotbarMode == "Custom" }.with(a = 190)
    val gradientHotbarSpeed by float("Hotbar-Gradient-Speed", 1f, 0.5f..10f)
    { customHotbar && hotbarMode == "Gradient" }
    val maxHotbarGradientColors by int("Max-Hotbar-Gradient-Colors", 4, 1..MAX_GRADIENT_COLORS)
    { customHotbar && hotbarMode == "Gradient" }
    val bgGradColors = ColorSettingsFloat.create(this, "Hotbar-Gradient")
    { customHotbar && hotbarMode == "Gradient" && it <= maxHotbarGradientColors }
    val hbHighlightBorder by float("HotbarBorder-Highlight-Width", 2F, 0.5F..5F) { customHotbar }
    val hbHighlightBorderColors = ColorSettingsInteger(this, "HotbarBorder-Highlight-Colors")
    { customHotbar }.with(a = 255, g = 111, b = 255)
    val hbBackgroundBorder by float("HotbarBorder-Background-Width", 0.5F, 0.5F..5F) { customHotbar }
    val hbBackgroundBorderColors = ColorSettingsInteger(this, "HotbarBorder-Background-Colors")
    { customHotbar }.with(a = 0)

    val rainbowX by float("Rainbow-X", -1000F, -2000F..2000F) { customHotbar && hotbarMode == "Rainbow" }
    val rainbowY by float("Rainbow-Y", -1000F, -2000F..2000F) { customHotbar && hotbarMode == "Rainbow" }
    val gradientX by float("Gradient-X", -1000F, -2000F..2000F) { customHotbar && hotbarMode == "Gradient" }
    val gradientY by float("Gradient-Y", -1000F, -2000F..2000F) { customHotbar && hotbarMode == "Gradient" }
    val inventoryParticle by boolean("InventoryParticle", false)
    private val blur by boolean("Blur", false)
    private val fontChat by boolean("FontChat", false)
    val hud = LiquidBounce.hud

    val barWidth by int("BarWidth", 86, 40..200) { modernHud }
    val barHeight by int("BarHeight", 9, 4..30) { modernHud }
    val barRadius by float("BarRadius", 5f, 0f..20f) { modernHud }
    val barAlpha by int("BarAlpha", 180, 0..255) { modernHud }
    val iconSize by int("IconSize", 13, 8..40) { modernHud }

    // Khi tạo ModernHUD, truyền setting động
    private val modernHud_render: ModernHUD
        get() = ModernHUD(
            barWidth = barWidth,
            barHeight = barHeight,
            barRadius = barRadius,
            barAlpha = barAlpha,
            iconSize = iconSize
        )

    val onRender2D = handler<Render2DEvent> {
        if (mc.currentScreen is GuiHudDesigner)
            return@handler
        val mc = Minecraft.getMinecraft()
        val sr = ScaledResolution(mc)
        val screenWidth = sr.scaledWidth
        val screenHeight = sr.scaledHeight
        val margin = 10
        // Health: bottom left
        val healthX = margin
        val healthY = screenHeight - margin - modernHud_render.iconSize - modernHud_render.barHeight

        // Armor: just above health
        val armorX = margin
        val armorY = healthY - modernHud_render.iconSize - modernHud_render.barHeight - 2

        // Food: bottom right
        val foodX = screenWidth - margin - modernHud_render.barWidth
        val foodY = screenHeight - margin - modernHud_render.iconSize - modernHud_render.barHeight

        // Exp: above armor
        val expX = margin
        val expY = armorY - modernHud_render.iconSize - modernHud_render.barHeight - 2

        // Air: above food (when underwater)
        val airX = foodX
        val airY = foodY - modernHud_render.iconSize - modernHud_render.barHeight - 2
        if (modernHud) {
            modernHud_render.drawHealthBar(healthX, healthY)
            modernHud_render.drawArmorBar(armorX, armorY)
            modernHud_render.drawFoodBar(foodX, foodY)
            modernHud_render.drawExpBar(expX, expY)
            modernHud_render.drawAirBar(airX, airY)
        }
        hud.render(false)
    }

    val onUpdate = handler<UpdateEvent> {
        val hud = LiquidBounce.hud
        hud.update()
    }

    val onKey = handler<KeyEvent> { event ->
        hud.handleKey('a', event.key)
    }

    val onScreen = handler<ScreenEvent>(always = true) { event ->
        if (mc.theWorld == null || mc.thePlayer == null) return@handler
        if (state && blur && !mc.entityRenderer.isShaderActive && event.guiScreen != null &&
            !(event.guiScreen is GuiChat || event.guiScreen is GuiHudDesigner)
        ) mc.entityRenderer.loadShader(
            ResourceLocation(CLIENT_NAME.lowercase() + "/blur.json")
        ) else if (mc.entityRenderer.shaderGroup != null &&
            "liquidbounce/blur.json" in mc.entityRenderer.shaderGroup.shaderGroupName
        ) mc.entityRenderer.stopUseShader()
    }

    fun shouldModifyChatFont() = handleEvents() && fontChat
}