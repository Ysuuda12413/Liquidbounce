package net.ccbluex.liquidbounce.ui.client.clickgui.style.styles

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.clickgui.Panel
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ButtonElement
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ModuleElement
import net.ccbluex.liquidbounce.ui.client.clickgui.style.Style
import net.minecraft.client.Minecraft
import net.ccbluex.liquidbounce.ui.utils.render.RenderUtils
import net.vitox.particle.util.RenderUtils as ParticleRenderUtils
import java.awt.Color

object LBPlusStyle : Style() {

    private val panelBg = Color(41, 43, 51, 255).rgb
    private val panelHeader = Color(35, 36, 43, 255).rgb
    private val hoverColor = Color(67, 74, 99, 255).rgb
    private val textColor = Color(255, 255, 255, 255).rgb
    private val subTextColor = Color(136, 136, 136, 255).rgb
    private val accent = Color(187, 170, 255, 255).rgb

    // Helper: vẽ rect thường vì không có drawRoundedRect
    private fun drawRect(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        net.ccbluex.liquidbounce.ui.utils.render.RenderUtils.drawRect(x1, y1, x2, y2, color)
    }

    // Option: vẽ bo góc thô sơ 4 góc
    private fun drawRectWithCircles(x: Int, y: Int, w: Int, h: Int, radius: Int, color: Int) {
        drawRect(x + radius, y, x + w - radius, y + h, color)
        drawRect(x, y + radius, x + w, y + h - radius, color)
        // 4 góc
        ParticleRenderUtils.drawCircle((x + radius).toFloat(), (y + radius).toFloat(), radius.toFloat(), color)
        ParticleRenderUtils.drawCircle((x + w - radius).toFloat(), (y + radius).toFloat(), radius.toFloat(), color)
        ParticleRenderUtils.drawCircle((x + radius).toFloat(), (y + h - radius).toFloat(), radius.toFloat(), color)
        ParticleRenderUtils.drawCircle((x + w - radius).toFloat(), (y + h - radius).toFloat(), radius.toFloat(), color)
    }

    override fun drawPanel(mouseX: Int, mouseY: Int, panel: Panel) {
        val mc = Minecraft.getMinecraft()
        val font = mc.fontRendererObj
        val x = panel.x
        val y = panel.y
        val w = panel.width
        val h = panel.height

        // Vẽ panel với hiệu ứng bo góc thô sơ
        drawRectWithCircles(x, y, w, h, 8, panelBg)
        // Header
        drawRect(x, y, x + w, y + 36, panelHeader)
        font.drawString(panel.name, x + 20f, y + 14f, accent, false)
    }

    override fun drawHoverText(mouseX: Int, mouseY: Int, text: String) {
        val mc = Minecraft.getMinecraft()
        val font = mc.fontRendererObj
        val width = font.getStringWidth(text) + 18
        val height = 18
        drawRect(mouseX + 12, mouseY, mouseX + 12 + width, mouseY + height, Color(30, 33, 40, 250).rgb)
        font.drawString(text, mouseX + 20f, mouseY + 5f, textColor, false)
    }

    override fun drawButtonElement(mouseX: Int, mouseY: Int, buttonElement: ButtonElement) {
        val mc = Minecraft.getMinecraft()
        val font = mc.fontRendererObj

        val x = buttonElement.x
        val y = buttonElement.y
        val w = buttonElement.width
        val h = buttonElement.height

        val hovered = buttonElement.isHovered(mouseX, mouseY)
        drawRect(x, y, x + w, y + h, if (hovered) hoverColor else panelHeader)
        font.drawString(buttonElement.displayName, x + 16f, y + (h - 8) / 2f, if (hovered) accent else textColor, false)
    }

    override fun drawModuleElementAndClick(
        mouseX: Int,
        mouseY: Int,
        moduleElement: ModuleElement,
        mouseButton: Int?
    ): Boolean {
        val mc = Minecraft.getMinecraft()
        val font = mc.fontRendererObj

        val x = moduleElement.x
        val y = moduleElement.y
        val w = moduleElement.supposedWidth
        val h = 30

        val hovered = moduleElement.isHovered(mouseX, mouseY)
        drawRect(x, y, x + w, y + h, if (hovered) hoverColor else panelBg)
        font.drawString(moduleElement.displayName, x + 12f, y + 6f, textColor, false)
        moduleElement.hoverText?.let {
            font.drawString(it, x + 12f, y + 18f, subTextColor, false)
        }
        // Toggle switch (vuông)
        val swX = x + w - 36
        val swY = y + 7
        val swW = 20
        val swH = 16
        val toggled = moduleElement.module.state
        drawRect(swX, swY, swX + swW, swY + swH, Color(44, 44, 44, 255).rgb)
        drawRect(
            swX + if (toggled) 10 else 2,
            swY + 3,
            swX + if (toggled) 18 else 10,
            swY + swH - 3,
            if (toggled) accent else Color(120, 120, 120).rgb
        )
        if (mouseButton == 0 && mouseX in swX..(swX + swW) && mouseY in swY..(swY + swH)) {
            return true
        }
        return false
    }
}