package hu.bme.aut.android.virgcserpmonitor.network

interface MessageListener {
    fun onConnectSuccess() // successfully connected
    fun onConnectFailed() // connection failed
    fun onClose() // close
    fun onMessage(text: String?)
}