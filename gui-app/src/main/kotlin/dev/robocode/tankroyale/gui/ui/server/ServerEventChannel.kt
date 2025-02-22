package dev.robocode.tankroyale.gui.ui.server

import dev.robocode.tankroyale.gui.util.Event

object ServerEventChannel {
    val onStartServer = Event<Unit>()
    val onRestartServer = Event<Unit>()
    val onStopServer = Event<Unit>()
}