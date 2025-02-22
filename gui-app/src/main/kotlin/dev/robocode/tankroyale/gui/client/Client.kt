package dev.robocode.tankroyale.gui.client

import dev.robocode.tankroyale.gui.model.*
import dev.robocode.tankroyale.gui.settings.GamesSettings
import dev.robocode.tankroyale.gui.settings.ServerSettings
import dev.robocode.tankroyale.gui.ui.server.ServerEventChannel
import dev.robocode.tankroyale.gui.ui.tps.TpsEventChannel
import dev.robocode.tankroyale.gui.util.Event
import dev.robocode.tankroyale.gui.util.Version
import kotlinx.serialization.PolymorphicSerializer
import java.lang.Thread.sleep
import java.net.URI
import java.util.*

object Client : AutoCloseable {

    init {
        TpsEventChannel.onTpsChanged.subscribe(Client) { changeTps(it.tps) }

        ServerEventChannel.apply {
            onRestartServer.subscribe(Client) {
                isGamePaused = false
                isGameRunning = false
            }
            onStopServer.subscribe(Client) {
                isGamePaused = false
                isGameRunning = false
            }
        }
    }

    // public events
    val onConnected = Event<Unit>()

    val onBotListUpdate = Event<BotListUpdate>()

    val onGameStarted = Event<GameStartedEvent>()
    val onGameEnded = Event<GameEndedEvent>()
    val onGameAborted = Event<GameAbortedEvent>()
    val onGamePaused = Event<GamePausedEvent>()
    val onGameResumed = Event<GameResumedEvent>()

    private val onRoundStarted = Event<RoundStartedEvent>()
    private val onRoundEnded = Event<RoundEndedEvent>()

    val onTickEvent = Event<TickEvent>()

    var currentGameSetup: GameSetup? = null

    var isGameRunning: Boolean = false

    var isGamePaused: Boolean = false
        private set

    private val isConnected: Boolean get() = websocket.isOpen()

    private var participants = listOf<Participant>()
    private var bots = setOf<BotInfo>()

    val joinedBots: Set<BotInfo>
        get() {
            return bots
        }

    private var websocket: WebSocketClient = WebSocketClient(URI(ServerSettings.serverUrl))

    private val json = MessageConstants.json

    private var gameTypes = setOf<String>()

    private var lastStartGame: StartGame? = null

    private var tps: Int? = null

    override fun close() {
        stopGame()

        if (isConnected) websocket.close()
    }

    fun connect(url: String) {
        websocket = WebSocketClient(URI(url))
        with(websocket) { // not apply() here, as new websocket is owner of the events below
            onOpen.subscribe(websocket) { onConnected.fire(Unit) }
            onMessage.subscribe(websocket) { onMessage(it) }
            onError.subscribe(websocket) { onError.fire(it) }

            open() // must be called after onOpen.subscribe()
        }
    }

    fun startGame(botAddresses: Set<BotAddress>) {
        if (isGameRunning) {
            stopGame()
        }

        val displayName = ServerSettings.gameType.displayName
        val gameSetup = GamesSettings.games[displayName]!!

        if (isConnected) {
            lastStartGame = StartGame(gameSetup.toGameSetup(), botAddresses)
            websocket.send(lastStartGame!!)
        }
    }

    fun stopGame() {
        if (isGameRunning && websocket.isOpen()) {
            websocket.send(StopGame())
        }
        isGamePaused = false
    }

    fun restartGame() {
        resumeGame()
        sleep(10) // let resume take effect
        stopGame()
        sleep(10) // let stop take effect
        websocket.send(lastStartGame!!)
    }

    fun pauseGame() {
        if (isGameRunning && !isGamePaused) {
            websocket.send(PauseGame())
        }
    }

    fun resumeGame() {
        if (isGameRunning && isGamePaused) {
            websocket.send(ResumeGame())
        }
    }

    fun getParticipant(id: Int): Participant = participants.first { participant -> participant.id == id }

    private fun changeTps(tps: Int) {
        if (isGameRunning && tps != this.tps) {
            this.tps = tps
            websocket.send(ChangeTps(tps))
        }
    }

    private fun onMessage(msg: String) {
        when (val type = json.decodeFromString(PolymorphicSerializer(Message::class), msg)) {
            is TickEvent -> handleTickEvent(type)
            is ServerHandshake -> handleServerHandshake(type)
            is BotListUpdate -> handleBotListUpdate(type)
            is GameStartedEvent -> handleGameStarted(type)
            is GameEndedEvent -> handleGameEnded(type)
            is GameAbortedEvent -> handleGameAborted(type)
            is GamePausedEvent -> handleGamePaused(type)
            is GameResumedEvent -> handleGameResumed(type)
            is RoundStartedEvent -> handleRoundStarted(type)
            is RoundEndedEvent -> handleRoundEnded(type)
            is TpsChangedEvent -> handleTpsChanged(type)
            else -> throw IllegalArgumentException("Unknown content type: $type")
        }
    }

    private fun handleServerHandshake(serverHandshake: ServerHandshake) {
        gameTypes = serverHandshake.gameTypes

        val handshake = ControllerHandshake(
            name = "Robocode Tank Royale UI",
            version = "${Version.version}",
            author = "Flemming N. Larsen",
            secret = ServerSettings.controllerSecrets.first()
        )
        websocket.send(handshake)
    }

    private fun handleBotListUpdate(botListUpdate: BotListUpdate) {
        bots = Collections.unmodifiableSet(botListUpdate.bots)
        onBotListUpdate.fire(botListUpdate)
    }

    private fun handleGameStarted(gameStartedEvent: GameStartedEvent) {
        isGameRunning = true
        currentGameSetup = gameStartedEvent.gameSetup
        participants = gameStartedEvent.participants

        onGameStarted.fire(gameStartedEvent)
    }

    private fun handleGameEnded(gameEndedEvent: GameEndedEvent) {
        isGameRunning = false
        isGamePaused = false
        onGameEnded.fire(gameEndedEvent)
    }

    private fun handleGameAborted(gameAbortedEvent: GameAbortedEvent) {
        isGameRunning = false
        isGamePaused = false
        onGameAborted.fire(gameAbortedEvent)
    }

    private fun handleGamePaused(gamePausedEvent: GamePausedEvent) {
        isGamePaused = true
        onGamePaused.fire(gamePausedEvent)
    }

    private fun handleGameResumed(gameResumedEvent: GameResumedEvent) {
        isGamePaused = false
        onGameResumed.fire(gameResumedEvent)
    }

    private fun handleRoundStarted(roundStartedEvent: RoundStartedEvent) {
        onRoundStarted.fire(roundStartedEvent)
    }

    private fun handleRoundEnded(roundEndedEvent: RoundEndedEvent) {
        onRoundEnded.fire(roundEndedEvent)
    }

    private fun handleTickEvent(tickEvent: TickEvent) {
        onTickEvent.fire(tickEvent)
    }

    private fun handleTpsChanged(tpsChangedEvent: TpsChangedEvent) {
        TpsEventChannel.onTpsChanged.fire(tpsChangedEvent)
    }
}