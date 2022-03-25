package dev.robocode.tankroyale.gui.settings

object ConfigSettings : PropertiesStore("Robocode Misc Settings", "config.properties") {

    private const val BOT_DIRECTORIES = "bot-directories"
    private const val TPS = "tps"
    private const val DEFAULT_TPS = 30

    private const val BOT_DIRS_SEPARATOR = ","

    fun getBotDirectories(): List<String> {
        load()
        return properties.getProperty(BOT_DIRECTORIES, "")
            .split(BOT_DIRS_SEPARATOR)
            .filter { it.isNotBlank() }
    }

    fun setBotDirectories(botDirs: List<String>) {
        properties.setProperty(BOT_DIRECTORIES, botDirs
            .filter { it.isNotBlank() }
            .joinToString(separator = BOT_DIRS_SEPARATOR))
        save()
    }

    fun getTps(): Int { // FIXME: Not used? +get/set properties: See ServerSettings
        load()
        var tps = try {
            properties.getProperty(TPS, DEFAULT_TPS.toString()).toInt()
        } catch (e: NumberFormatException) {
            DEFAULT_TPS
        }
        if (tps < 0) tps = DEFAULT_TPS
        return tps
    }

    fun setTps(tps: Int) {
        properties.setProperty(TPS, tps.toString())
        save()
    }
}