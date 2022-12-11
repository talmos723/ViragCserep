package hu.bme.aut.android.virgcserpmonitor.data

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


object StatesDataHolder {
    val states: MutableList<State> = ArrayList()
    val lock = ReentrantLock()

    var OnChangeOfTempScale: MutableList<(fahrenheit: Boolean) -> Unit> = ArrayList()
    var fahrenheit: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                Preferences.getInstance()?.update("fahrenheit", value)
                lock.withLock { states.forEach { state -> state.fahrenheit = value } }
                OnChangeOfTempScale.forEach { it.invoke(value) }
            }
        }

    init {
        fahrenheit = Preferences.getInstance()?.readBoolean("fahrenheit") ?: false
    }

    fun getSize(): Int = states.size

    fun addState(newState: State): Int {
        lock.withLock {
            states.add(newState)
        }
        return states.size - 1
    }

    fun removeState(position: Int) {
        lock.withLock {
            states.removeAt(position)
        }
    }

    fun removeAllStates() {
        lock.withLock {
            states.clear()
        }
    }

    operator fun get(position: Int) = lock.withLock { states[position] }

    fun getIndex(state: State): Int {
        return lock.withLock { states.indexOf(state) }
    }
}
