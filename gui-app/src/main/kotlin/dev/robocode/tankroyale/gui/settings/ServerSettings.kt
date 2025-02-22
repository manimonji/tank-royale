package dev.robocode.tankroyale.gui.settings

import dev.robocode.tankroyale.gui.ui.server.WsUrl
import dev.robocode.tankroyale.gui.util.RegisterWsProtocol
import java.net.URI
import java.util.*
import javax.crypto.KeyGenerator
import kotlin.collections.ArrayList


object ServerSettings : PropertiesStore("Robocode Server Settings", "server.properties") {

    const val DEFAULT_PORT = 7654
    const val DEFAULT_SCHEME = "ws"
    const val DEFAULT_URL = "$DEFAULT_SCHEME://localhost"

    private const val SERVER_URL = "server-url"
    private const val CONTROLLER_SECRETS = "controllers-secrets"
    private const val BOT_SECRETS = "bots-secrets"
    private const val USER_URLS = "user-urls"
    private const val GAME_TYPE = "game-type"

    init {
        RegisterWsProtocol // work-around for ws:// with URI class
    }

    var serverUrl: String
        get() {
            val url = properties.getProperty(SERVER_URL, DEFAULT_URL)
            return WsUrl(url).origin
        }
        set(value) {
            properties.setProperty(SERVER_URL, value)
        }

    val serverPort: Int get() = URI(serverUrl).port

    var controllerSecrets: Set<String>
        get() = getPropertyAsSet(CONTROLLER_SECRETS).ifEmpty {
            controllerSecrets = setOf(generateSecret())
            controllerSecrets
        }
        set(value) {
            setPropertyBySet(CONTROLLER_SECRETS, value)
            save()
        }

    var botSecrets: Set<String>
        get() = getPropertyAsSet(BOT_SECRETS).ifEmpty {
            botSecrets = setOf(generateSecret())
            botSecrets
        }
        set(value) {
            setPropertyBySet(BOT_SECRETS, value)
            save()
        }

    private fun generateSecret(): String {
        val secretKey = KeyGenerator.getInstance("AES").generateKey()
        val encodedKey = Base64.getEncoder().encodeToString(secretKey.encoded)
        // Remove trailing '=='
        return encodedKey.substring(0, encodedKey.length - 2)
    }

    var userUrls: List<String>
        get() {
            val urls = properties.getProperty(USER_URLS, "")
            return if (urls.isBlank()) {
                listOf(serverUrl)
            } else {
                urls.split(",")
            }
        }
        set(value) {
            val list = ArrayList(value)
            list.remove(DEFAULT_URL)
            properties.setProperty(USER_URLS, list.joinToString(","))
        }

    var gameType: GameType
        get() {
            val displayName = properties.getProperty(GAME_TYPE, GameType.CLASSIC.displayName)
            return GameType.from(displayName)
        }
        set(value) {
            properties.setProperty(GAME_TYPE, value.displayName)
        }

    init {
        resetToDefault()
        load()
    }

    private fun resetToDefault() {
        serverUrl = DEFAULT_URL
        userUrls = emptyList()
    }
}
/*
fun main() {
    with(ServerSettings) {
        println("serverUrl: $serverUrl")
        println("port: $serverPort")
        println("userUrls: $userUrls")
        println("generateSecret: ${generateSecret()}")

        userUrls = listOf("ws://1.2.3.4:90", "wss://localhost:900")
    }
}
*/