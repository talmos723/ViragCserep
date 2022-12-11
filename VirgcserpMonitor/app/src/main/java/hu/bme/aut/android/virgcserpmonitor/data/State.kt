package hu.bme.aut.android.virgcserpmonitor.data

import hu.bme.aut.android.virgcserpmonitor.R

data class State(
    var _temperature: Int,
    var humidity: Int,
    var light: Int,
    var waterLevel: Int,
    var time: TimeStamp,
    var soilMoisture: Int,
    var rain: Int,
    var fahrenheit: Boolean = false
) {
    var temperature: Int
        get() {
            return if (fahrenheit)
                (_temperature * 1.8 + 32).toInt()
            else
                _temperature
        }
        set(value) {
            _temperature = value
        }

    fun getDayLightSymbolResource(): Int {
        return if (rain != 0) {
            R.drawable.rain_symbol
        } else {
            if (light > MIN_LIGHT_FOR_NIGHT)
                if (time.hour in FROM_HOUR_IS_DAY..TO_HOUR_IS_DAY)
                    R.drawable.sun_symbol
                else
                    R.drawable.bulb_symbol
            else
                R.drawable.moon_symbol
        }
    }

    operator fun compareTo(other: State): Int {
        //val date = Date(time.date)
        //val otherDate = Date(other.time.date)
        var res = time.year.compareTo(other.time.year)
        if (res == 0) {
            res = time.decodeMonth().compareTo(time.decodeMonth())
            if (res == 0) {
                res = time.dayOfMonth.compareTo(other.time.dayOfMonth)
                if (res == 0) {
                    res = time.hour.compareTo(other.time.hour)
                    if (res == 0) {
                        res = time.minutes.compareTo(other.time.minutes)
                        if (res == 0) {
                            res = time.seconds.compareTo(other.time.seconds)

                        }
                    }
                }
            }
        }

        return res
    }

    companion object {
        val MIN_LIGHT_FOR_NIGHT = 150
        val FROM_HOUR_IS_DAY = 7
        val TO_HOUR_IS_DAY = 18
    }
}