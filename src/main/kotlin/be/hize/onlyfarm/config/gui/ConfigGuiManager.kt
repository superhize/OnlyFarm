package be.hize.onlyfarm.config.gui

import be.hize.onlyfarm.OnlyFarmMod
import be.hize.onlyfarm.config.Features
import io.github.moulberry.moulconfig.gui.GuiScreenElementWrapper
import io.github.moulberry.moulconfig.gui.MoulConfigEditor

object ConfigGuiManager {

    var editor: MoulConfigEditor<Features>? = null

    fun getEditorInstance() = editor
        ?: MoulConfigEditor(OnlyFarmMod.configManager.processor).also { editor = it }

    fun openConfigGui(search: String? = null) {
        val editor = getEditorInstance()

        if (search != null) {
            editor.search(search)
        }
        OnlyFarmMod.screenToOpen = GuiScreenElementWrapper(editor)
    }


}
