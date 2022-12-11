package hu.bme.aut.android.virgcserpmonitor.data

data class TimeStamp(
    var date: String,
    var hour: Int,
    var minutes: Int,
    var seconds: Int
) {
    var year = date.split(" ")[0].replace(".", "").toInt()
    var month = decodeMonth()
    var dayOfMonth = date.split(" ")[2].replace(".", "").toInt()

    override fun toString(): String {
        return "$date - ${String.format("%02d", hour)}:${
            String.format(
                "%02d",
                minutes
            )
        }:${String.format("%02d", seconds)}"
    }

    fun decodeMonth(): Int {
        return when (date.split(" ")[1]) {
            "January" -> 1
            "February" -> 2
            "March" -> 3
            "April" -> 4
            "May" -> 5
            "June" -> 6
            "July" -> 7
            "August" -> 8
            "September" -> 9
            "October" -> 10
            "November" -> 11
            "December" -> 12
            else -> -1
        }
    }
}