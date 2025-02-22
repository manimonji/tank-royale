package dev.robocode.tankroyale.server.mapper

import dev.robocode.tankroyale.schema.BotHandshake
import dev.robocode.tankroyale.schema.BotInfo

object BotHandshakeToBotInfoMapper {
    fun map(botHandshake: BotHandshake, hostName: String, port: Int): BotInfo {
        val botInfo = BotInfo()
        botHandshake.apply {
            botInfo.name = name
            botInfo.version = version
            botInfo.authors = authors.toList()
            botInfo.description = description
            botInfo.homepage = homepage
            botInfo.countryCodes = countryCodes.toList()
            botInfo.gameTypes = gameTypes.toList()
            botInfo.platform = platform
            botInfo.programmingLang = programmingLang
            botInfo.host = hostName
            botInfo.port = port
        }
        return botInfo
    }
}