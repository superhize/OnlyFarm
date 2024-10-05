package be.hize.onlyfarm.events

import net.minecraft.util.IChatComponent

class ChatEvent(val message: String, val chatComponent: IChatComponent,) : ModEvent()
