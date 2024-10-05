package be.hize.onlyfarm.commands

import be.hize.onlyfarm.OnlyFarmMod
import be.hize.onlyfarm.config.gui.ConfigGuiManager
import be.hize.onlyfarm.features.misc.TestGui
import be.hize.onlyfarm.features.update.AutoUpdate
import be.hize.onlyfarm.utils.CommandActionRegistry
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.ClientCommandHandler


object Commands {

    private val openMainMenu: (Array<String>) -> Unit = {
        if (it.isNotEmpty()) {
            ConfigGuiManager.openConfigGui(it.joinToString(" "))
        } else {
            ConfigGuiManager.openConfigGui()
        }
    }

    // command -> description
    private val commands = mutableListOf<CommandInfo>()

    enum class CommandCategory(val color: String, val categoryName: String, val description: String) {
        MAIN("§6", "Main Command", "Most useful commands"),
    }

    class CommandInfo(val name: String, val description: String, val category: Commands.CommandCategory)

    private var currentCategory = CommandCategory.MAIN

    fun init() {
        usersMain()
    }

    private fun usersMain() {
        registerCommand("of", "Open main menu", openMainMenu)
        registerCommand("onlyfarm", "Open main menu", openMainMenu)
        registerCommand("ofupdate", "Check for update") { AutoUpdate.onCommand() }
        registerCommand("ofcommands", "Commands list") { commandHelp(it) }

        registerCommand("ofnotif", "Open notifications gui") {
            OnlyFarmMod.screenToOpen = TestGui()
        }
    }


    private fun commandHelp(args: Array<String>) {
        var filter: (String) -> Boolean = { true }
        val title: String
        if (args.size == 1) {
            val searchTerm = args[0].lowercase()
            filter = { it.lowercase().contains(searchTerm) }
            title = "Commands with '§e$searchTerm§7'"
        } else {
            title = "All commands"
        }
        val base = ChatComponentText(" \n§7$title:\n")
        for (command in commands) {
            if (!filter(command.name) && !filter(command.description)) continue
            val category = command.category
            val name = command.name
            val color = category.color
            val text = ChatComponentText("$color/$name")
            text.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$name")

            val hoverText = buildList {
                add("§e/$name")
                add(" §7${command.description}")
                add("")
                add("$color${category.categoryName}")
                add("  §7${category.description}")
            }

            text.chatStyle.chatHoverEvent =
                HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText(hoverText.joinToString("\n")))
            base.appendSibling(text)
            base.appendSibling(ChatComponentText("§7, "))
        }
        base.appendSibling(ChatComponentText("\n "))
        Minecraft.getMinecraft().thePlayer.addChatMessage(base)
    }

    private fun registerCommand(name: String, description: String, function: (Array<String>) -> Unit) {
        ClientCommandHandler.instance.registerCommand(SimpleCommand(name, createCommand(function)))
        ClientCommandHandler.instance.registerCommand(CommandActionRegistry)
        commands.add(CommandInfo(name, description, currentCategory))
    }

    private fun createCommand(function: (Array<String>) -> Unit) = object : SimpleCommand.ProcessCommandRunnable() {
        override fun processCommand(sender: ICommandSender?, args: Array<String>?) {
            if (args != null) function(args.asList().toTypedArray())
        }
    }
}
