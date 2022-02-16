package dev.robocode.tankroyale.gui.ui.about

import dev.robocode.tankroyale.gui.MainWindow
import dev.robocode.tankroyale.gui.ui.components.RcDialog
import dev.robocode.tankroyale.gui.ui.extensions.JComponentExt.addButton
import dev.robocode.tankroyale.gui.util.Event
import dev.robocode.tankroyale.gui.util.JavaVersion
import dev.robocode.tankroyale.gui.util.Version
import net.miginfocom.swing.MigLayout
import java.awt.Container
import java.net.URL
import javax.swing.*


object AboutBox : RcDialog(MainWindow, "about_dialog") {

    private val onOk = Event<JButton>()

    private val url: URL? = AboutBox.javaClass.classLoader.getResource("gfx/Tank.png")?.toURI()?.toURL()
    private val version = Version.version
    private val javaVersion = JavaVersion.version
    private val javaVendor = JavaVersion.vendor
    private val javaWordSize = JavaVersion.wordSize

    init {
        val panel = JPanel(MigLayout("fill, insets 0 0 5 0", "[center]"))
        panel.add(htmlPane(), "wrap")

        contentPane.add(panel)
        pack()

        panel.addButton("ok", onOk).apply {
            requestFocus()
        }
        pack()
        setLocationRelativeTo(MainWindow) // center on main window

        isResizable = false

        onOk.subscribe(this) {
            dispose()
        }
    }

    private fun htmlPane(): Container =
        JEditorPane("text/html; charset=UTF8", html()).apply {
            isEditable = false
        }

    private fun html(): String = """
        <table style="border-spacing: 10px">
            <tr>
                <td valign="top"><image width="64" height="64" src="$url"></td>
                <td><span style="font-family: Arial, Helvetica, sans-serif;">
                    <b>Robocode Tank Royale</b>
                    <br>
                    Version: $version<br>
                    <br>
                    Copyright © 2022 Flemming N&oslash;rnberg Larsen<br>
                    <br>
                    Running on <strong>Java $javaVersion ($javaWordSize)</strong> by $javaVendor
                 </span></td>
            </tr>
        </table>
    """.trimIndent()
}

fun main() {
    AboutBox.isVisible = true
}