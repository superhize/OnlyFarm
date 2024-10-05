package be.hize.onlyfarm.features.misc.gui.components

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import gg.essential.universal.USound
import gg.essential.vigilance.utils.onLeftClick
import java.awt.Color

class SimpleButton @JvmOverloads constructor(val t: String, val h: Boolean = false, val w: Boolean = false) :
    UIBlock(Color(0, 0, 0, 80)) {

    val text = UIText(t).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        color = Color(14737632).toConstraint()
    } childOf this

    init {
        this
            .constrain {
                width = if (w) {
                    RelativeConstraint()
                } else {
                    (text.getWidth() + 40).pixels()
                }
                height = if (h) {
                    RelativeConstraint()
                } else {
                    (text.getHeight() + 10).pixels()
                }
            }
            .onMouseEnter {
                animate {
                    setColorAnimation(
                        Animations.OUT_EXP,
                        0.5f,
                        Color(255, 255, 255, 80).toConstraint(),
                        0f
                    )
                }
                text.constrain {
                    color = Color(16777120).toConstraint()
                }
            }.onMouseLeave {
                animate {
                    setColorAnimation(
                        Animations.OUT_EXP,
                        0.5f,
                        Color(0, 0, 0, 80).toConstraint()
                    )
                }
                text.constrain {
                    color = Color(14737632).toConstraint()
                }
            }.onLeftClick {
                USound.playButtonPress()
            }
    }
}
