package be.hize.onlyfarm.utils

import kotlin.time.Duration.Companion.milliseconds

object TimeUtils {
    val Int.ticks get() = (this * 50).milliseconds
}
