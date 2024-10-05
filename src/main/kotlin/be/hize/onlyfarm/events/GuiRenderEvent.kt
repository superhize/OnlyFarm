package be.hize.onlyfarm.events

open class GuiRenderEvent : ModEvent() {
    class ChestGuiOverlayRenderEvent : GuiRenderEvent()
    class GuiOverlayRenderEvent : GuiRenderEvent()
}
