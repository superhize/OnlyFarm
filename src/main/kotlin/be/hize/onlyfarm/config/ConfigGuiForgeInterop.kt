package be.hize.onlyfarm.config

import be.hize.onlyfarm.config.gui.ConfigGuiManager
import io.github.moulberry.moulconfig.gui.GuiScreenElementWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import org.lwjgl.input.Keyboard
import java.io.IOException

@Suppress("unused")
class ConfigGuiForgeInterop : IModGuiFactory {
    override fun initialize(minecraft: Minecraft) {}
    override fun mainConfigGuiClass() = WrappedOnlyFarmConfig::class.java

    override fun runtimeGuiCategories(): Set<IModGuiFactory.RuntimeOptionCategoryElement>? = null

    override fun getHandlerFor(element: IModGuiFactory.RuntimeOptionCategoryElement): IModGuiFactory.RuntimeOptionGuiHandler? = null

    class WrappedOnlyFarmConfig(private val parent: GuiScreen) :
        GuiScreenElementWrapper(ConfigGuiManager.getEditorInstance()) {
        @Throws(IOException::class)
        override fun handleKeyboardInput() {
            if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                Minecraft.getMinecraft().displayGuiScreen(parent)
                return
            }
            super.handleKeyboardInput()
        }
    }
}
