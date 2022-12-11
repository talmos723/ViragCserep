package hu.bme.aut.android.virgcserpmonitor.data

import android.util.Log
import hu.bme.aut.android.virgcserpmonitor.network.MessageListener
import hu.bme.aut.android.virgcserpmonitor.network.WebSocketManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object DataAccess : MessageListener {

    var OnDataAdded: MutableList<(state: State, newPos: Int) -> Unit> = ArrayList()
    var OnRefres: MutableList<() -> Unit> = ArrayList()
    var OnDataLoaded: MutableList<() -> Unit> = ArrayList()
    private var canRefresh = true

    override fun onConnectSuccess() {
        Log.i("websocket", " Connected successfully")
    }

    override fun onConnectFailed() {
        Log.i("websocket", " Connection failed")
    }

    override fun onClose() {
        Log.i("websocket", " Closed successfully")
    }

    override fun onMessage(text: String?) {
        Log.i("websocket", " Receive message: $text")
        if (text != null) {
            val answer = JSONObject(text)
            val datas = answer.getJSONArray("datas")
            addText(datas)
        }
    }

    fun loadStateData() {
        WebSocketManager.init(NetworkConstVals.webSocketLink, this)
        WebSocketManager.connect()
        WebSocketManager.close()
    }

    fun refresh() { //TODO: beszarik az app, ha 2000 adatról 5-re csökkentjük az adatok számát
        if (!canRefresh) {
            return
        }
        thread {
            Log.i("DataAccess", "Refresh TimeOut Started")
            canRefresh = false
            OnRefres.forEach { it.invoke() }
            StatesDataHolder.removeAllStates()
            loadStateData()
            TimeUnit.SECONDS.sleep(2)   //timeout to not spam the refresh method
            canRefresh = true
            Log.i("DataAccess", "Refresh TimeOut Ended")
        }
    }

    private fun addText(jsonArrayOfStates: JSONArray?) {
        if (jsonArrayOfStates != null) {
            //Assuming that websocket sending datas in the order from oldest to newest and
            //we show them in order from newest to oldest
            for (i in 0 until jsonArrayOfStates.length()) {
                val data = JSONObject(jsonArrayOfStates.get(i).toString())
                val timestampJson = JSONObject(data.get("timeStamp").toString())
                val timestamp = TimeStamp(
                    timestampJson.get("date").toString(),
                    timestampJson.get("hour").toString().toInt(),
                    timestampJson.get("minutes").toString().toInt(),
                    timestampJson.get("seconds").toString().toInt()
                )
                val stateJson = JSONObject(data.get("esp8266").toString())
                val state = State(
                    _temperature = stateJson.get("Temperature").toString().toInt(),
                    humidity = stateJson.get("Humidity").toString().toInt(),
                    waterLevel = stateJson.get("WaterLevel").toString().toInt(),
                    light = stateJson.get("Light").toString().toInt(),
                    soilMoisture = stateJson.get("SoilMoisture").toString().toInt(),
                    rain = stateJson.get("Rain").toString().toInt(),
                    time = timestamp,
                    fahrenheit = StatesDataHolder.fahrenheit
                )
                val pos = StatesDataHolder.addState(state)
                OnDataAdded.forEach { it.invoke(state, pos) }
            }
            OnDataLoaded.forEach { it.invoke() }
        }
    }
}