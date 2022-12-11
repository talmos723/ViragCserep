package hu.bme.aut.android.virgcserpmonitor.data

object NetworkConstVals {
    var numberOfDatasPerRequest: Int = 1
        set(value) {
            Preferences.getInstance()?.update("numberOfDatasPerRequest", value)
            field = value
        }
    var fromValue: String? = null
        set(value) {
            Preferences.getInstance()?.update("fromValueOnRequest", value ?: "null")
            field = value
        }
    var toValue: String? = null
        set(value) {
            Preferences.getInstance()?.update("toValueOnRequest", value ?: "null")
            field = value
        }
    var ip = "10.152.69.0"
    val ip2 = "192.168.0.23"
    val port = "8001"
    val id = "almos723"
    val webSocketLink: String
        get() {
            var link = "ws://$ip:$port/?id=$id&num=$numberOfDatasPerRequest"
            if (fromValue != null)
                link += "&from=$fromValue"
            if (toValue != null)
                link += "&to=$toValue"
            return link
        }


    init {
        numberOfDatasPerRequest =
            Preferences.getInstance()?.readInt("numberOfDatasPerRequest") ?: 20
        fromValue = Preferences.getInstance()?.readString("fromValueOnRequest")
        toValue = Preferences.getInstance()?.readString("toValueOnRequest")
        if (fromValue == "null" || fromValue == "")
            fromValue = null
        if (toValue == "null" || toValue == "")
            toValue = null
        if (numberOfDatasPerRequest == 0 && fromValue == null && toValue == null)
            numberOfDatasPerRequest = 1
    }
}