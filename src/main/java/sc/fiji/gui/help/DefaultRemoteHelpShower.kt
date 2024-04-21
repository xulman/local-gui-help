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
import java.io.IOException
import java.net.URL
import javax.swing.JEditorPane
import javax.swing.JFrame
import javax.swing.JLabel

class DefaultRemoteHelpShower internal constructor(val urlToRemoteHelp: URL, val dialogTitle: String) : HelpShower {
    override fun showNonModalHelpNow() {
        val contentPane = Panel()
        contentPane.layout = GridBagLayout()

        //TODO: make these a common static attrib, part of the iface?
        val minSize = Dimension(300, 300)
        val preferredSize = Dimension(600, 600)

        //TODO: make the link click-able
        val urlHeader = JLabel("Showing content from: $urlToRemoteHelp")
        val textPane = JEditorPane()
        textPane.isEditable = false
        textPane.contentType = "text/html"
        try {
            textPane.page = urlToRemoteHelp
        } catch (e: IOException) {
            textPane.text = "FALLBACK CONTENT because failed opening the URL:<br/>$urlToRemoteHelp"
        }
        textPane.minimumSize = minSize
        textPane.preferredSize = preferredSize

        val c = GridBagConstraints()
        c.anchor = GridBagConstraints.CENTER
        c.fill = GridBagConstraints.BOTH
        c.gridx = 0
        c.gridy = 1
        contentPane.add(textPane, c)
        c.gridy = 0
        c.insets = Insets(5, 5, 10, 5)
        contentPane.add(urlHeader, c)

        //TODO: add close button! (to make it look similar to the sibbling help dialog)
        val f = JFrame(dialogTitle)
        f.contentPane = contentPane
        f.pack()
        f.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        f.isVisible = true
    }
}