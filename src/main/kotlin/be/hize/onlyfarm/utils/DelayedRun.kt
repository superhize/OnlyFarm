package be.hize.onlyfarm.utils

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration

object DelayedRun {

    private val tasks = mutableListOf<Pair<() -> Any, SimpleTimeMark>>()
    private val futureTasks = ConcurrentLinkedQueue<Pair<() -> Any, SimpleTimeMark>>()

    fun runDelayed(duration: Duration, run: () -> Unit): SimpleTimeMark {
        val time = SimpleTimeMark.now() + duration
        futureTasks.add(Pair(run, time))
        return time
    }

    /** Runs in the next full Tick so the delay is between 50ms to 100ms**/
    fun runNextTick(run: () -> Unit) {
        futureTasks.add(Pair(run, SimpleTimeMark.farPast()))
    }

    fun checkRuns() {
        tasks.removeIf { (runnable, time) ->
            val inPast = time.isInPast()
            if (inPast) {
                try {
                    runnable()
                } catch (e: Exception) {}
            }
            inPast
        }
        futureTasks.drainTo(tasks)
    }

    inline fun <reified E, reified L : MutableCollection<E>> Queue<E>.drainTo(list: L): L {
        while (true) {
            list.add(this.poll() ?: break)
        }
        return list
    }
}
