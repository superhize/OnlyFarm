package be.hize.onlyfarm.features.misc

import be.hize.onlyfarm.OnlyFarmMod
import be.hize.onlyfarm.config.ConfigFileType
import be.hize.onlyfarm.features.misc.gui.components.SimpleButton
import be.hize.onlyfarm.utils.MessageMode
import be.hize.onlyfarm.utils.showPlayerMessage
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.universal.UKeyboard
import gg.essential.vigilance.utils.onLeftClick
import java.awt.Color

class TestGui : WindowScreen(ElementaVersion.V2, newGuiScale = 2) {

    private val scrollComponent: ScrollComponent
    private val components = HashMap<UIContainer, Entry>()

    private data class Entry(
        val container: UIContainer,
        val regex: UITextInput,
        val displayText: UITextInput,
        val ticks: UITextInput,
    )

    init {
        UIText("Custom Notifications").childOf(window).constrain {
            x = CenterConstraint()
            y = RelativeConstraint(0.075f)
            height = 14.pixels()
        }

        scrollComponent = ScrollComponent(
            innerPadding = 4f,
        ).childOf(window).constrain {
            x = CenterConstraint()
            y = 15.percent()
            width = 90.percent()
            height = 70.percent() + 2.pixels()
        }

        val bottomButtons = UIContainer().childOf(window).constrain {
            x = CenterConstraint()
            y = 90.percent()
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        SimpleButton("Save and Exit").childOf(bottomButtons).constrain {
            x = 0.pixels()
            y = 0.pixels()
        }.onLeftClick {
            saveNotifications()
            mc.displayGuiScreen(null)
        }

        SimpleButton("Add Notification").childOf(bottomButtons).constrain {
            x = SiblingConstraint(5f)
            y = 0.pixels()
        }.onLeftClick {
            addNewNotification()
        }

        for (notif in CustomNotifications.notifications.sortedBy { it.text }) {
            addNewNotification(notif.pattern.pattern(), notif.text, notif.displayTicks)
        }
    }

    private fun addNewNotification(regex: String = "", text: String = "", ticks: Int = 20) {
        val container = UIContainer().childOf(scrollComponent).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
            width = 80.percent()
            height = 9.5.percent()
        }.effect(OutlineEffect(Color(0, 243, 255), 1f))

        val triggerMessage = UITextInput("Trigger Regex").childOf(container).constrain {
            x = 5.pixels()
            y = CenterConstraint()
            width = 40.percent()
        }.apply {
            onLeftClick {
                grabWindowFocus()
            }
            setText(regex)
        }

        val displayText = UITextInput("Display Text").childOf(container).constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
            width = 32.percent()
        }.apply {
            onLeftClick {
                grabWindowFocus()
            }
            setText(text)
        }

        val displayTicks = UITextInput("Ticks").childOf(container).constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
            width = 5.percent()
        }.apply {
            onLeftClick {
                grabWindowFocus()
            }
            setText(ticks.toString())
        }
        triggerMessage.apply {
            onKeyType { _, keyCode ->
                if (keyCode == UKeyboard.KEY_TAB) displayText.grabWindowFocus()
                triggerMessage.setText(triggerMessage.getText().replace("&&", "§"))
            }
        }
        displayText.apply {
            onKeyType { _, keyCode ->
                if (keyCode == UKeyboard.KEY_TAB) displayTicks.grabWindowFocus()
                displayText.setText(displayText.getText().replace("&&", "§"))
            }
        }
        displayTicks.apply {
            onKeyType { _, keyCode ->
                if (keyCode == UKeyboard.KEY_TAB) triggerMessage.grabWindowFocus()
            }
        }

        SimpleButton("Remove").childOf(container).constrain {
            x = 85.percent()
            y = CenterConstraint()
            height = 75.percent()
        }.onLeftClick {
            scrollComponent.removeChild(container)
            components.remove(container)
        }

        components[container] = Entry(container, triggerMessage, displayText, displayTicks)
    }

    override fun onScreenClose() {
        super.onScreenClose()
        CustomNotifications.notifications.clear()

        for ((_, triggerRegex, displayText, displayTicks) in components.values) {
            if (triggerRegex.getText().isBlank() ||
                displayText.getText().isBlank() ||
                displayTicks.getText()
                    .isBlank()
            ) continue
            runCatching {
                CustomNotifications.notifications.add(
                    CustomNotifications.Notification(
                        triggerRegex.getText().replace("%%MC_IGN%%", mc.session.username).toPattern(),
                        displayText.getText(),
                        displayTicks.getText().toInt(),
                    ),
                )
                saveNotifications()
            }.onFailure {
                it.printStackTrace()
                showPlayerMessage(MessageMode.ERROR) {
                    text("Invalid notification: ${triggerRegex.getText()}")
                }
            }
        }
    }

    private fun saveNotifications() {
        OnlyFarmMod.configManager.saveConfig(ConfigFileType.NOTIFICATIONS, "saving-data")
    }

}
