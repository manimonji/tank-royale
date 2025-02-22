package dev.robocode.tankroyale.booter.model

import kotlinx.serialization.Serializable

@Serializable
data class BotInfo(
    val name: String,
    val version: String,
    val gameTypes: String,
    val authors: String,
    val description: String?,
    val homepage: String? = null,
    val countryCodes: String,
    val platform: String? = null,
    val programmingLang: String? = null,
)