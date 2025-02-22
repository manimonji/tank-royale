package dev.robocode.tankroyale.gui.ui.newbattle

import dev.robocode.tankroyale.gui.model.BotInfo
import dev.robocode.tankroyale.gui.util.Event

object BotSelectionChannel {

    val onBotDirectorySelected = Event<BotInfo>()
    val onJoinedBotSelected = Event<BotInfo>()
    val onBotSelected = Event<BotInfo>()

    val onSelectedBotListUpdated = Event<List<BotInfo>>()
}
