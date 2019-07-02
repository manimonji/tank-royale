package net.robocode2.gui.settings

object ServerSettings : PropertiesStore("Robocode Server Config", "server.properties") {

    private const val SERVER_ADDRESS_PROPERTY = "server.address"
    private const val DEFAULT_SERVER_ADDRESS = "localhost"

    private const val SERVER_PORT_PROPERTY = "server.port"
    private const val DEFAULT_SERVER_PORT: UShort = 55000u

    private const val REMOTE_SERVER_PROPERTY = "remote.server"
    private const val DEFAULT_REMOTE_SERVER = false

    val endpoint: String get() = "ws://$address:$port"

    var address: String
        get() =
            if (useRemoteServer)
                properties.getProperty(SERVER_ADDRESS_PROPERTY, DEFAULT_SERVER_ADDRESS)
            else
                DEFAULT_SERVER_ADDRESS
        set(value) {
            properties.setProperty(SERVER_ADDRESS_PROPERTY, value)
        }

    var port: UShort
        get() = properties.getProperty(SERVER_PORT_PROPERTY, "$DEFAULT_SERVER_PORT")!!.toUShort()
        set(value) {
            properties.setProperty(SERVER_PORT_PROPERTY, "$value")
        }

    var useRemoteServer: Boolean
        get() = properties.getProperty(REMOTE_SERVER_PROPERTY, "$DEFAULT_REMOTE_SERVER")!!.toBoolean()
        set(useRemoteServer) {
            properties.setProperty(REMOTE_SERVER_PROPERTY, "$useRemoteServer")
        }

    init {
        resetToDefault()
        load()
    }

    fun resetToDefault() {
        address = DEFAULT_SERVER_ADDRESS
        port = DEFAULT_SERVER_PORT
        useRemoteServer = DEFAULT_REMOTE_SERVER
    }
}