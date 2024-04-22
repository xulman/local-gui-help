/*-
 * #%L
 * A framework for Java apps that attaches and shows simple help dialogs/wizzards to developer-chosen GUI controls.
 * %%
 * Copyright (C) 2024 Vladimir Ulman
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package sc.fiji.gui.help

import java.awt.*
import java.awt.event.KeyEvent
import java.net.URI
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel

fun main() {
    PlayingAroundApp()
}

class PlayingAroundApp internal constructor() {
    lateinit var panel1: JButton
    lateinit var panel2: JButton
    lateinit var panel3: JLabel

    val blue = Color(128, 128, 255)
    val green = Color(128, 192, 128)
    val gray = Color(192, 192, 192)

    fun buildAppWindow(): JFrame {
        val contentPane = Panel().apply {
            background = gray
            layout = GridBagLayout()
        }
        panel1 = JButton("SIMPLE (local help)").apply {
            preferredSize = Dimension(200, 50)
            horizontalAlignment = JLabel.CENTER
            background = blue
        }
        panel2 = JButton("APP  (remote help)").apply {
            preferredSize = Dimension(200, 50)
            horizontalAlignment = JLabel.CENTER
            background = green
        }
        panel3 = JLabel("not used").apply {
            preferredSize = Dimension(200, 50)
            horizontalAlignment = JLabel.CENTER
        }
        //panel3.setBorder(BorderFactory.createEtchedBorder(green,gray));
        val c = GridBagConstraints().apply {
            anchor = GridBagConstraints.CENTER
            fill = GridBagConstraints.BOTH
            gridx = 0
            gridy = 0
            insets = Insets(50, 50, 5, 50)
        }
        contentPane.add(panel1, c)
        c.gridy = 1
        c.insets = Insets(0, 50, 5, 50)
        contentPane.add(panel2, c)
        c.gridy = 2
        c.insets = Insets(0, 50, 50, 50)
        contentPane.add(panel3, c)

        return JFrame("A Simple Demo App").apply {
            this.contentPane = contentPane
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            pack()
            isVisible = true
        }
    }


    init {
        buildAppWindow()

        val keys = hashSetOf(KeyEvent.VK_H, KeyEvent.VK_J)

        HelpManager.registerComponentHelp(panel1,
                                          HelpManager.constructPathToLocalTopics<PanelControlledWorker>("Topic1"),
                                          "HELP from LOCAL FILES")

        HelpManager.registerComponentHelp(panel2,
                                          URI("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.html").toURL(),
                                          "HELP from REMOTE URL")

        HelpManager.registerComponentHelp(panel3,
                                          HelpManager.constructPathToLocalTopics<PanelControlledWorker>("Topic2"),
                                          "ANOTHER LOCAL FILE HELP")

        panel1.addKeyListener(HelpManager.getKeyboardListener(keys))
        panel2.addKeyListener(HelpManager.getKeyboardListener(keys))
        panel3.addKeyListener(HelpManager.getKeyboardListener(keys))
    }
}
