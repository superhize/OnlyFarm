package be.hize.onlyfarm.events

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event

abstract class ModEvent : Event() {
    private val eventName by lazy {
        this::class.simpleName
    }

    fun postAndCatch(): Boolean {
        return runCatching {
            postWithoutCatch()
        }.onFailure {

        }.getOrDefault(isCanceled)
    }

    private fun postWithoutCatch() = MinecraftForge.EVENT_BUS.post(this)
}