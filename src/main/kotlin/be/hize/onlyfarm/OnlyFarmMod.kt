package be.hize.onlyfarm

import be.hize.onlyfarm.commands.Commands
import be.hize.onlyfarm.config.ConfigFileType
import be.hize.onlyfarm.config.ConfigManager
import be.hize.onlyfarm.config.Features
import be.hize.onlyfarm.config.Notifications
import be.hize.onlyfarm.data.ChatManager
import be.hize.onlyfarm.data.MinecraftData
import be.hize.onlyfarm.data.RenderGuiData
import be.hize.onlyfarm.data.ScoreboardData
import be.hize.onlyfarm.data.TitleManager
import be.hize.onlyfarm.events.ModTickEvent
import be.hize.onlyfarm.features.misc.CustomNotifications
import be.hize.onlyfarm.features.other.ButtonOnPause
import be.hize.onlyfarm.features.update.AutoUpdate
import be.hize.onlyfarm.utils.HypixelUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod(
    modid = OnlyFarmMod.MOD_ID,
    clientSideOnly = true,
    useMetadata = true,
    guiFactory = "be.hize.onlyfarm.config.ConfigGuiForgeInterop",
    name = "OnlyFarm")
internal class OnlyFarmMod {

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        loadModule(this)

        loadModule(MinecraftData())
        loadModule(HypixelUtils)
        loadModule(ScoreboardData)
        loadModule(ChatManager())

        loadModule(ButtonOnPause())
        loadModule(CustomNotifications)
        loadModule(TitleManager)
        loadModule(RenderGuiData)

        Commands.init()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        configManager = ConfigManager()
        configManager.firstLoad()
        Runtime.getRuntime().addShutdownHook(Thread { configManager.saveConfig(ConfigFileType.FEATURES, "shutdown-hook") })

        loadModule(AutoUpdate)
    }

    private fun loadModule(obj: Any) {
        modules.add(obj)
        MinecraftForge.EVENT_BUS.register(obj)
    }

    @SubscribeEvent
    fun onTick(event: ModTickEvent) {
        if (screenToOpen != null) {
            screenTicks++
            if (screenTicks == 5) {
                Minecraft.getMinecraft().thePlayer.closeScreen()
                Minecraft.getMinecraft().displayGuiScreen(screenToOpen)
                screenTicks = 0
                screenToOpen = null
            }
        }
    }

    companion object {
        const val MOD_ID = "onlyfarm"

        @JvmStatic
        val version: String
            get() = Loader.instance().indexedModList[MOD_ID]!!.version

        @JvmStatic
        var feature: Features = Features()
        lateinit var configManager: ConfigManager
        lateinit var notifications: Notifications
        val modules: MutableList<Any> = ArrayList()

        private val globalJob: Job = Job(null)
        val coroutineScope = CoroutineScope(
            CoroutineName("OnlyFarm") + SupervisorJob(globalJob)
        )
        var screenToOpen: GuiScreen? = null
        private var screenTicks = 0
    }
}
