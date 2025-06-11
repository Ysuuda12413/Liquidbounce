package net.ccbluex.liquidbounce.ui.client.clickgui.style.styles

import net.ccbluex.liquidbounce.ui.client.clickgui.Panel
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ButtonElement
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ModuleElement
import net.ccbluex.liquidbounce.ui.client.clickgui.style.Style
import net.ccbluex.liquidbounce.ui.font.Fonts.fontSemibold35
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawRect
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawBorderedRect
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Keyboard
import java.awt.Color
import kotlin.math.roundToInt

object TabStyle : Style() {
    private val sidebarColor = Color(27, 28, 38, 235).rgb
    private val sidebarActive = Color(60, 160, 250, 200).rgb
    private val backgroundColor = Color(36, 37, 49, 252).rgb
    private val mainBorder = Color(60, 65, 80, 175).rgb
    private val moduleBoxColor = Color(44, 47, 63, 245).rgb
    private val moduleBoxActive = Color(51, 155, 255, 54).rgb
    private val moduleBoxOutline = Color(51, 155, 255, 110).rgb
    private val hoveredOverlay = Color(51, 155, 255, 35).rgb
    private val textColor = Color(238, 238, 238).rgb
    private val descColor = Color(150, 155, 180).rgb
    private val keyBindBg = Color(67, 79, 110, 170).rgb
    private val keyBindText = Color(210, 210, 210).rgb
    private val toggleBg = Color(39, 44, 62, 255).rgb
    private val toggleOff = Color(130, 130, 135, 140).rgb
    private val toggleOn = Color(0, 170, 255, 255).rgb

    // Draw panel with border and sidebar
    override fun drawPanel(mouseX: Int, mouseY: Int, panel: Panel) {
        // Sidebar
        drawRect(
            panel.x - 125, panel.y + 3,
            panel.x - 5, panel.y + panel.height + panel.fade - 3,
            sidebarColor
        )
        // Main background
        drawRect(
            panel.x, panel.y,
            panel.x + panel.width, panel.y + panel.height + panel.fade,
            backgroundColor
        )
        // Border
        drawBorderedRect(panel.x, panel.y, panel.x + panel.width, panel.y + panel.height + panel.fade, 2, mainBorder, Color(0,0,0,0).rgb)
        // Title
        fontSemibold35.drawString(
            panel.name,
            panel.x + 32, panel.y + 18,
            textColor
        )
    }

    // Tooltip chuẩn LiquidBounce (không destructuring)
    override fun drawHoverText(mouseX: Int, mouseY: Int, text: String) {
        val lines = text.lines()
        val width = lines.maxOfOrNull { fontSemibold35.getStringWidth(it) + 22 } ?: return
        val height = fontSemibold35.fontHeight * lines.size + 11
        val scaledResolution = ScaledResolution(mc)
        val scaledWidth = scaledResolution.scaledWidth
        val scaledHeight = scaledResolution.scaledHeight
        val x = clamp(mouseX, 0, (scaledWidth - width))
        val y = clamp(mouseY, 0, (scaledHeight - height))
        drawBorderedRect(x + 2, y, x + width + 6, y + height + 4, 1, sidebarActive, sidebarColor)
        lines.forEachIndexed { index, t ->
            fontSemibold35.drawString(t, x + 16, y + 6 + (fontSemibold35.fontHeight + 2) * index, textColor)
        }
    }

    // Sidebar button chỉ highlight khi hover
    override fun drawButtonElement(mouseX: Int, mouseY: Int, buttonElement: ButtonElement) {
        val isHovered = buttonElement.isHovered(mouseX, mouseY)
        val color = if (isHovered) sidebarActive else sidebarColor
        drawRect(
            buttonElement.x + 5, buttonElement.y + 5,
            buttonElement.x + buttonElement.width - 8, buttonElement.y + buttonElement.height - 8,
            color
        )
        fontSemibold35.drawString(
            buttonElement.displayName,
            buttonElement.x + 28,
            buttonElement.y + buttonElement.height / 2 - fontSemibold35.fontHeight / 2 + 2,
            textColor
        )
    }

    // Module box đẹp, highlight, toggle switch, keybind, desc
    override fun drawModuleElementAndClick(
        mouseX: Int, mouseY: Int, moduleElement: ModuleElement, mouseButton: Int?
    ): Boolean {
        val active = moduleElement.module.state
        val hovered = moduleElement.isHovered(mouseX, mouseY)
        // Main module box
        drawRect(
            moduleElement.x, moduleElement.y,
            moduleElement.x + moduleElement.width, moduleElement.y + moduleElement.height,
            moduleBoxColor
        )
        // Active highlight
        if (active) {
            drawRect(
                moduleElement.x, moduleElement.y,
                moduleElement.x + moduleElement.width, moduleElement.y + moduleElement.height,
                moduleBoxActive
            )
        }
        // Outline on hover or active
        if (hovered || active)
            drawBorderedRect(
                moduleElement.x, moduleElement.y,
                moduleElement.x + moduleElement.width, moduleElement.y + moduleElement.height,
                2, moduleBoxOutline, Color(0,0,0,0).rgb
            )
        // Hover overlay
        if (hovered && !active)
            drawRect(
                moduleElement.x, moduleElement.y,
                moduleElement.x + moduleElement.width, moduleElement.y + moduleElement.height,
                hoveredOverlay
            )
        // Name
        fontSemibold35.drawString(
            moduleElement.displayName,
            moduleElement.x + 28,
            moduleElement.y + 13,
            textColor
        )
        // Keybind
        val key = moduleElement.module.keyBind
        if (key > 0) {
            val keyName = Keyboard.getKeyName(key)
            drawRect(
                moduleElement.x + moduleElement.width - 59,
                moduleElement.y + 15,
                moduleElement.x + moduleElement.width - 22,
                moduleElement.y + 31,
                keyBindBg
            )
            fontSemibold35.drawString(
                keyName,
                moduleElement.x + moduleElement.width - 52,
                moduleElement.y + 19,
                keyBindText
            )
        }
        // Description
        fontSemibold35.drawString(
            moduleElement.module.description.take(56),
            moduleElement.x + 28,
            moduleElement.y + moduleElement.height - 22,
            descColor
        )
        // Toggle switch
        val toggleX = moduleElement.x + moduleElement.width - 41
        val toggleY = moduleElement.y + moduleElement.height / 2 - 10
        drawToggleSwitch(toggleX, toggleY, 30, 20, active)
        // Expand arrow
        val arrowX = moduleElement.x + moduleElement.width - 19
        val arrowY = moduleElement.y + moduleElement.height / 2 - 6
        fontSemibold35.drawString(if (moduleElement.showSettings) "▲" else "▼", arrowX, arrowY, descColor)

        // Toggle click
        if (mouseButton == 0 &&
            mouseX in (toggleX..(toggleX + 30)) &&
            mouseY in (toggleY..(toggleY + 20))
        ) {
            moduleElement.module.toggle()
            return true
        }

        // Expand click
        if (mouseButton == 0 &&
            mouseX in (arrowX..(arrowX + 14)) &&
            mouseY in (arrowY..(arrowY + 14))
        ) {
            moduleElement.showSettings = !moduleElement.showSettings
            return true
        }
        return false
    }

    // Toggle switch kiểu đơn giản (chỉ dùng drawRect)
    private fun drawToggleSwitch(x: Int, y: Int, w: Int, h: Int, toggled: Boolean) {
        drawRect(x, y, x + w, y + h, toggleBg)
        if (toggled) {
            drawRect(x + w - h, y, x + w, y + h, toggleOn)
        } else {
            drawRect(x, y, x + h, y + h, toggleOff)
        }
        // Knob
        val knobX = if (toggled) x + w - h + 3 else x + 3
        drawRect(knobX, y + 3, knobX + h - 6, y + h - 3, Color.WHITE.rgb)
    }
    private fun clamp(value: Int, min: Int, max: Int): Int = if (value < min) min else if (value > max) max else value
}