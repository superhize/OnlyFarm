package be.hize.onlyfarm.data

import be.hize.onlyfarm.events.GuiRenderEvent
import be.hize.onlyfarm.utils.SimpleTimeMark
import io.github.notenoughupdates.moulconfig.internal.TextRenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object TitleManager {

    private var originalText = ""
    private var display = ""
    private var endTime = SimpleTimeMark.farPast()
    private var heightModifier = 1.8
    private var fontSizeModifier = 4f

    fun sendTitle(text: String, duration: Duration, height: Double, fontSize: Float) {
        originalText = text
        display = "§f$text"
        endTime = SimpleTimeMark.now() + duration
        heightModifier = height
        fontSizeModifier = fontSize
    }

    fun optionalResetTitle(condition: (String) -> Boolean) {
        if (condition(originalText)) {
            sendTitle("", 1.milliseconds, 1.8, 4f)
        }
    }


    @SubscribeEvent
    fun onRenderOverlay(event: GuiRenderEvent.GuiOverlayRenderEvent) {
        if (endTime.isInPast()) return

        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val width = scaledResolution.scaledWidth
        val height = scaledResolution.scaledHeight

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        val renderer = Minecraft.getMinecraft().fontRendererObj

        GlStateManager.pushMatrix()
        GlStateManager.translate((width / 2).toFloat(), (height / heightModifier).toFloat(), 3.0f)
        GlStateManager.scale(fontSizeModifier, fontSizeModifier, fontSizeModifier)
        TextRenderUtils.drawStringCenteredScaledMaxWidth(display, renderer, 0f, 0f, true, 75, 0)
        GlStateManager.popMatrix()
    }
}
