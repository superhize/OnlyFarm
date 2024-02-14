package be.hize.onlyfarm.utils

import be.hize.onlyfarm.data.ScoreboardData
import be.hize.onlyfarm.events.HypixelJoinEvent
import be.hize.onlyfarm.events.ModTickEvent
import be.hize.onlyfarm.events.ScoreboardUpdateEvent
import be.hize.onlyfarm.events.WorldChangeEvent
import be.hize.onlyfarm.utils.StringUtils.removeColor
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

object HypixelUtils {

    private var onMain = false
    private var onAlpha = false
    private var skyBlock = false
    val onHypixel get() = (onMain || onAlpha) && Minecraft.getMinecraft().thePlayer != null
    val onSkyblock get() = skyBlock && onHypixel

    @SubscribeEvent
    fun onDisconnect(event: FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        onMain = false
        onAlpha = false
        skyBlock = false
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldChangeEvent) {
        skyBlock = false
    }

    @SubscribeEvent
    fun onTick(event: ModTickEvent) {
        if (!onHypixel) return
        if (!event.isMod(5)) return
        val inSkyblock = checkScoreboard()
        if (skyBlock == inSkyblock) return
        skyBlock = inSkyblock
    }

    private fun checkScoreboard(): Boolean {
        val minecraft = Minecraft.getMinecraft()
        val world = minecraft.theWorld ?: return false
        val obj = world.scoreboard.getObjectiveInDisplaySlot(1) ?: return false
        val displayName = obj.displayName
        val title = displayName.removeColor()
        return title.contains("SKYBLOCK") || title.contains("SKIBLOCK")
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (event.scoreboard.isEmpty()) return
        if (!onHypixel) {
            val last = event.scoreboard.last()
            onMain = last == "§ewww.hypixel.net"
            onAlpha = last == "§ealpha.hypixel.net"

            if (onHypixel) {
                HypixelJoinEvent().postAndCatch()
            }
        }
        if (!onHypixel) return
    }

}
