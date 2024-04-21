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

import sc.fiji.gui.help.HelpManager.Companion.constructPathToLocalTopics
import sc.fiji.gui.help.HelpManager.Companion.obtain
import java.awt.*
import java.awt.event.KeyEvent
import java.net.MalformedURLException
import java.net.URL
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel

class PlayingAroundApp internal constructor() {
    var panel1: JButton? = null
    var panel2: JButton? = null
    var panel3: JLabel? = null

    val blue: Color = Color(128, 128, 255)
    val green: Color = Color(128, 192, 128)
    val gray: Color = Color(192, 192, 192)

    fun buildAppWindow(): JFrame {
        val contentPane = Panel()
        contentPane.background = gray
        contentPane.layout = GridBagLayout()

        panel1 = JButton("SIMPLE (local help)")
        panel1!!.preferredSize = Dimension(200, 50)
        panel1!!.horizontalAlignment = JLabel.CENTER
        panel1!!.background = blue

        panel2 = JButton("APP  (remote help)")
        panel2!!.preferredSize = Dimension(200, 50)
        panel2!!.horizontalAlignment = JLabel.CENTER
        panel2!!.background = green

        panel3 = JLabel("not used")
        panel3!!.preferredSize = Dimension(200, 50)
        panel3!!.horizontalAlignment = JLabel.CENTER

        //panel3.setBorder(BorderFactory.createEtchedBorder(green,gray));
        val c = GridBagConstraints()
        c.anchor = GridBagConstraints.CENTER
        c.fill = GridBagConstraints.BOTH
        c.gridx = 0
        c.gridy = 0
        c.insets = Insets(50, 50, 5, 50)
        contentPane.add(panel1, c)
        c.gridy = 1
        c.insets = Insets(0, 50, 5, 50)
        contentPane.add(panel2, c)
        c.gridy = 2
        c.insets = Insets(0, 50, 50, 50)
        contentPane.add(panel3, c)

        val f = JFrame("A Simple Demo App")
        f.contentPane = contentPane
        f.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        f.pack()
        f.isVisible = true
        return f
    }


    init {
        buildAppWindow()

        val keys: MutableSet<Int?> = HashSet(1)
        keys.add(KeyEvent.VK_H)
        keys.add(KeyEvent.VK_J)
        val helpManager = obtain()

        helpManager!!.registerComponentHelp(panel1!!,
                constructPathToLocalTopics(PanelControlledWorker::class.java, "Topic1"),
                "HELP from LOCAL FILES")

        helpManager.registerComponentHelp(panel2!!,
                URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.html"),
                "HELP from REMOTE URL")

        helpManager.registerComponentHelp(panel3!!,
                constructPathToLocalTopics(PanelControlledWorker::class.java, "Topic2"),
                "ANOTHER LOCAL FILE HELP")

        panel1!!.addKeyListener(helpManager.getKeyboardListener(keys))
        panel2!!.addKeyListener(helpManager.getKeyboardListener(keys))
        panel3!!.addKeyListener(helpManager.getKeyboardListener(keys))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                PlayingAroundApp()
            } catch (e: MalformedURLException) {
                throw RuntimeException(e)
            }
        }
    }
}
