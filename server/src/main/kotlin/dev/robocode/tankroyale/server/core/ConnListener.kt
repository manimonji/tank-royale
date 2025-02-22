package dev.robocode.tankroyale.server.dev.robocode.tankroyale.server.core

import dev.robocode.tankroyale.schema.*
import org.java_websocket.WebSocket

/** Connection listener interface. */
interface ConnListener {
    fun onException(exception: Exception)
    fun onBotJoined(conn: WebSocket, handshake: BotHandshake)
    fun onBotLeft(conn: WebSocket, handshake: BotHandshake)
    fun onBotReady(conn: WebSocket, handshake: BotHandshake)
    fun onBotIntent(conn: WebSocket, handshake: BotHandshake, intent: BotIntent)
    fun onObserverJoined(conn: WebSocket, handshake: ObserverHandshake)
    fun onObserverLeft(conn: WebSocket, handshake: ObserverHandshake)
    fun onControllerJoined(conn: WebSocket, handshake: ControllerHandshake)
    fun onControllerLeft(conn: WebSocket, handshake: ControllerHandshake)
    fun onStartGame(gameSetup: GameSetup, botAddresses: Collection<BotAddress>)
    fun onAbortGame()
    fun onPauseGame()
    fun onResumeGame()
    fun onChangeTps(tps: Int)
}
