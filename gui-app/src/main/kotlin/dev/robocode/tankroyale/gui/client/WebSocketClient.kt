package dev.robocode.tankroyale.gui.client

import dev.robocode.tankroyale.gui.model.Message
import dev.robocode.tankroyale.gui.model.MessageConstants
import dev.robocode.tankroyale.gui.util.Event
import kotlinx.serialization.PolymorphicSerializer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletionStage

class WebSocketClient(private val uri: URI) : AutoCloseable {

    val onOpen = Event<Unit>()
    val onClose = Event<Unit>()
    val onMessage = Event<String>()
    val onError = Event<Throwable>()

    private val json = MessageConstants.json

    private val listener = WebSocketListener()

    fun open() {
        connect()
    }

    private fun connect() {
        try {
            val httpClient = HttpClient.newBuilder().build()
            val webSocketBuilder = httpClient.newWebSocketBuilder()
            webSocketBuilder.buildAsync(uri, listener).join()
        } catch (ex: Exception) {
            throw RuntimeException("Could not connect to server: $uri", ex)
        }
    }

    override fun close() {
        listener.websocket?.abort()
    }

    fun isOpen() = listener.websocket != null

    fun send(data: Any) {
        val msg = json.encodeToString(PolymorphicSerializer(Message::class), data as Message)
        listener.websocket?.sendText(msg, true)
    }

    private inner class WebSocketListener : WebSocket.Listener {
        var websocket: WebSocket? = null
        private var payload = StringBuffer()

        override fun onOpen(webSocket: WebSocket) {
            this.websocket = webSocket
            onOpen.fire(Unit)
            super.onOpen(webSocket)
        }

        override fun onClose(webSocket: WebSocket?, statusCode: Int, reason: String?): CompletionStage<*>? {
            this.websocket = null
            onClose.fire(Unit)
            return null
        }

        override fun onError(webSocket: WebSocket?, error: Throwable) {
            System.err.println(error.message)
            onError.fire(error)
        }

        override fun onText(webSocket: WebSocket, data: CharSequence?, last: Boolean): CompletionStage<*>? {
            payload.append(data)
            if (last) {
                onMessage.fire(payload.toString())
                payload.delete(0, payload.length) // clear payload buffer
            }
            return super.onText(webSocket, data, last)
        }
    }
}