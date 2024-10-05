package be.hize.onlyfarm.features.misc

import be.hize.onlyfarm.OnlyFarmMod
import be.hize.onlyfarm.events.ChatEvent
import be.hize.onlyfarm.utils.HypixelUtils
import be.hize.onlyfarm.utils.RegexUtils.matchMatcher
import be.hize.onlyfarm.utils.TimeUtils.ticks
import be.hize.onlyfarm.utils.Utils
import com.google.gson.annotations.Expose
import kotlinx.coroutines.launch
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.regex.Pattern

object CustomNotifications {
    val notifications get() = OnlyFarmMod.notifications.customNotifications

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    fun onMessage(event: ChatEvent) {
        if (!HypixelUtils.onSkyblock || notifications.isEmpty()) return
        OnlyFarmMod.coroutineScope.launch {
            val msg = event.message
            for ((pattern, text, displayTicks) in notifications) {
                pattern.matchMatcher(msg) {
                    var title = text
                    (groupCount() downTo 0).forEach { groupIndex ->
                        val g = group(groupIndex)
                        title.forEachIndexed { i, _ -> title = title.replace("%%${i}%%", g) }
                    }
                    title = title.replace("&", "§")
                    Utils.sendTitle(title, displayTicks.ticks)
                }
            }
        }
    }

    data class Notification(
        @Expose val pattern: Pattern,
        @Expose val text: String,
        @Expose val displayTicks: Int,
    )
}
