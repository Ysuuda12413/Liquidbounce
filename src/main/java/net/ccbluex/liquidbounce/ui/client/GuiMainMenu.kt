/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client

import net.ccbluex.liquidbounce.LiquidBounce.CLIENT_NAME
import net.ccbluex.liquidbounce.LiquidBounce.clientVersionText
import net.ccbluex.liquidbounce.lang.translationMenu
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.fontmanager.GuiFontManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawRoundedBorderRect
import net.ccbluex.liquidbounce.utils.ui.AbstractScreen
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.GuiSelectWorld
import net.minecraft.client.resources.I18n
import org.lwjgl.input.Mouse

class GuiMainMenu : AbstractScreen() {

    private var popup: PopupScreen? = null

    companion object {
        var lastWarningTime: Long? = null
    }


    override fun initGui() {
        val defaultHeight = height / 4 + 48

        val baseCol1 = width / 2 - 100
        val baseCol2 = width / 2 + 2

        +GuiButton(100, baseCol1, defaultHeight + 24, 98, 20, translationMenu("altManager"))
        +GuiButton(103, baseCol2, defaultHeight + 24, 98, 20, translationMenu("mods"))
        +GuiButton(109, baseCol1, defaultHeight + 24 * 2, 98, 20, translationMenu("fontManager"))
        +GuiButton(102, baseCol2, defaultHeight + 24 * 2, 98, 20, translationMenu("configuration"))
        +GuiButton(101, baseCol1, defaultHeight + 24 * 3, 98, 20, translationMenu("serverStatus"))
        +GuiButton(108, baseCol2, defaultHeight + 24 * 3, 98, 20, translationMenu("contributors"))

        +GuiButton(1, baseCol1, defaultHeight, 98, 20, I18n.format("menu.singleplayer"))
        +GuiButton(2, baseCol2, defaultHeight, 98, 20, I18n.format("menu.multiplayer"))

        // Minecraft Realms
        //        +GuiButton(14, this.baseCol1, j + 24 * 2, I18n.format("menu.online"))

        +GuiButton(0, baseCol1, defaultHeight + 24 * 4, 98, 20, I18n.format("menu.options"))
        +GuiButton(4, baseCol2, defaultHeight + 24 * 4, 98, 20, I18n.format("menu.quit"))
    }

    private fun showWelcomePopup() {
        popup = PopupScreen {
            title("§a§lWelcome!")
            message("""
                §eThank you for downloading and installing §bLiquidBounce§e!
        
                §6Here is some information you might find useful:§r
                §a- §fClickGUI:§r Press §7[RightShift]§f to open ClickGUI.
                §a- §fRight-click modules with a '+' to edit.
                §a- §fHover over a module to see its description.
        
                §6Important Commands:§r
                §a- §f.bind <module> <key> / .bind <module> none
                §a- §f.config load <name> / .config list
        
                §bNeed help? Contact us!§r
                - §fYouTube: §9https://youtube.com/ccbluex
                - §fTwitter: §9https://twitter.com/ccbluex
                - §fForum: §9https://forums.ccbluex.net/
            """.trimIndent())
            button("§aOK")
            onClose { popup = null }
        }
    }


    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        drawRoundedBorderRect(
            width / 2f - 115, height / 4f + 35, width / 2f + 115, height / 4f + 175,
            2f,
            Integer.MIN_VALUE,
            Integer.MIN_VALUE,
            3F
        )

        Fonts.fontBold180.drawCenteredString(CLIENT_NAME, width / 2F, height / 8F, 4673984, true)
        Fonts.fontSemibold35.drawCenteredString(
            clientVersionText,
            width / 2F + 148,
            height / 8F + Fonts.fontSemibold35.fontHeight,
            0xffffff,
            true
        )

        super.drawScreen(mouseX, mouseY, partialTicks)

        if (popup != null) {
            popup!!.drawScreen(width, height, mouseX, mouseY)
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (popup != null) {
            popup!!.mouseClicked(mouseX, mouseY, mouseButton)
            return
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun actionPerformed(button: GuiButton) {
        if (popup != null) {
            return
        }

        when (button.id) {
            0 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            1 -> mc.displayGuiScreen(GuiSelectWorld(this))
            2 -> mc.displayGuiScreen(GuiMultiplayer(this))
            4 -> mc.shutdown()
            100 -> mc.displayGuiScreen(GuiAltManager(this))
            101 -> mc.displayGuiScreen(GuiServerStatus(this))
            102 -> mc.displayGuiScreen(GuiClientConfiguration(this))
            103 -> mc.displayGuiScreen(GuiModsMenu(this))
            108 -> mc.displayGuiScreen(GuiContributors(this))
            109 -> mc.displayGuiScreen(GuiFontManager(this))
        }
    }

    override fun handleMouseInput() {
        if (popup != null) {
            val eventDWheel = Mouse.getEventDWheel()
            if (eventDWheel != 0) {
                popup!!.handleMouseWheel(eventDWheel)
            }
        }

        super.handleMouseInput()
    }
}
