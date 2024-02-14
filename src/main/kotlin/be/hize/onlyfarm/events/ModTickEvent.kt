package be.hize.onlyfarm.events

class ModTickEvent(private val tick: Int) : ModEvent() {

    fun isMod(i: Int) = tick % i==0
    fun repeatSeconds(i: Int) = isMod(i * 20)
}