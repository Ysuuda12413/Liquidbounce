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
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object HUD : Module("HUD", Category.RENDER, gameDetecting = false, defaultState = true, defaultHidden = true) {
    val customHotbar by boolean("CustomHotbar", true)
    val smoothHotbarSlot by boolean("SmoothHotbarSlot", true) { customHotbar }
    val modernHud by boolean("ModernHud", true)
    val modernHudDetail by boolean("ModernHud-Detail", true) { modernHud } // Setting mới

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
    val iconSize by int("IconSize", 11, 8..40) { modernHud }

    private val modernHud_render: ModernHUD
        get() = ModernHUD(
            barWidth = barWidth,
            barHeight = barHeight,
            barRadius = barRadius,
            barAlpha = barAlpha,
            iconSize = iconSize,
            detail = modernHudDetail
        )

    val onRender2D = handler<Render2DEvent> {
        if (mc.currentScreen is GuiHudDesigner) return@handler

        val mc = Minecraft.getMinecraft()
        val sr = ScaledResolution(mc)
        val screenWidth = sr.scaledWidth
        val screenHeight = sr.scaledHeight

        val bar = modernHud_render

        val centerX = screenWidth / 2
        val baseY = screenHeight - 39
        // Health (trái)
        val healthX = centerX - 91
        val healthY = baseY

        // Armor (trên health)
        val armorX = healthX
        val armorY = healthY - bar.iconSize - bar.barHeight - 2

        // Food (phải)
        val foodX = centerX + 91 - bar.barWidth
        val foodY = baseY

        // Air (trên food)
        val airX = foodX
        val airY = foodY - bar.iconSize - bar.barHeight - 2

        // Exp (trên armor)
        val expX = armorX
        val expY = armorY - bar.iconSize - bar.barHeight - 2

        if (modernHud) {
            bar.drawHealthBar(healthX, healthY)
            bar.drawArmorBar(armorX, armorY)
            bar.drawFoodBar(foodX, foodY)
            bar.drawExpBar(expX, expY)
            bar.drawAirBar(airX, airY)
            bar.drawAbsorptionBar(healthX, healthY - bar.barHeight - 2)
            hud.render(false)
        } else {
            hud.render(false)
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
        if (!modernHud) return

        when (event.type) {
            RenderGameOverlayEvent.ElementType.HEALTH,
            RenderGameOverlayEvent.ElementType.ARMOR,
            RenderGameOverlayEvent.ElementType.FOOD,
            RenderGameOverlayEvent.ElementType.EXPERIENCE,
            RenderGameOverlayEvent.ElementType.AIR -> {
                event.isCanceled = true
            }
            else -> {}
        }
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