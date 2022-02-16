package dev.robocode.tankroyale.gui.ui.components

import dev.robocode.tankroyale.gui.ui.ResourceBundles
import java.awt.Frame
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.KeyStroke

open class RcDialog(owner: Frame? = null, titlePropertyName: String) :
    JDialog(owner, ResourceBundles.UI_TITLES.get(titlePropertyName)) {

    init {
        setupDisposeOnEnterKeyPressed()
    }

    private fun setupDisposeOnEnterKeyPressed() {
        val inputMap = rootPane.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW)
        val enter = "enter"
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enter)
        rootPane.actionMap.put(enter, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                dispose()
            }
        })
    }
}
