package hu.bme.aut.android.virgcserpmonitor.data

import android.content.Context
import androidx.core.content.edit

private const val PREFERENCES_NAME = "settings"

/**
 * Class that handles saving and retrieving user preferences
 */
class Preferences private constructor(context: Context) {

    private val sharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun update(key: String, value: Int) {
        sharedPreferences.edit {
            putInt(key, value)
        }
    }

    fun update(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    fun update(key: String, value: Boolean) {
        sharedPreferences.edit {
            putBoolean(key, value)
        }
    }

    fun readString(key: String, default: String = ""): String {
        return sharedPreferences.getString(key, default) ?: default
    }

    fun readBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    fun readInt(key: String, default: Int = 1): Int {
        return sharedPreferences.getInt(key, default)
    }

    companion object {
        @Volatile
        private var INSTANCE: Preferences? = null

        fun initPreferences(context: Context): Preferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = Preferences(context)
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(): Preferences? {
            return INSTANCE
        }
    }
}