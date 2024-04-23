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

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Panel
import java.nio.file.Path
import javax.swing.*
import kotlin.io.path.exists
import kotlin.io.path.readText

class DefaultLocalHelpShower internal constructor(val pathToLocalHelp: Path,
                                                  val dialogTitle: String) : HelpShower {
    var currentPage: Int = 0

    override fun showNonModalHelpNow() {
        val contentPane = Panel().apply {
            layout = GridBagLayout()
        }

        val minSizeImg = Dimension(300, 200)
        val minSizeTxt = Dimension(300, 100)
        val preferredSizeImg = Dimension(600, 400)
        val preferredSizeTxt = Dimension(600, 200)

        val imagePane = JLabel().apply {
            minimumSize = minSizeImg
            preferredSize = preferredSizeImg
        }
        val textPane = JEditorPane().apply {
            isEditable = false
            contentType = "text/html"
            minimumSize = minSizeTxt
            preferredSize = preferredSizeTxt
        }
        //TODO: add buttons!
        val prevB = JButton("Previous")
        val nextB = JButton("Next")
        val closeB = JButton("Got it, close")

        currentPage = 1
        fillPage(imagePane, textPane)
        //
        prevB.addActionListener {
            if (currentPage > 1) {
                --currentPage
                fillPage(imagePane, textPane)
            }
        }
        nextB.addActionListener {
            ++currentPage
            if (currentHTML.exists())
                fillPage(imagePane, textPane)
            else {
                --currentPage
                println("no more pages")
            }
        }

        val c = GridBagConstraints().apply {
            anchor = GridBagConstraints.CENTER
            fill = GridBagConstraints.BOTH
            gridwidth = 3
            gridx = 0
            gridy = 0
        }
        contentPane.add(imagePane, c)
        c.gridy = 1
        contentPane.add(textPane, c)

        //TODO: add buttons!
        c.gridwidth = 1
        c.weightx = 0.33
        c.gridy = 2
        contentPane.add(prevB, c)
        c.gridx = 1
        contentPane.add(nextB, c)
        c.gridx = 2
        contentPane.add(closeB, c)

        val f = JFrame(dialogTitle).apply {
            this.contentPane = contentPane
            pack()
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            isVisible = true
        }

        closeB.addActionListener { f.isVisible = false }
    }

    fun fillPage(imagePane: JLabel, textPane: JEditorPane) {
        //TODO can fail reading the png, provide placeholder then.. like missed file and the path to it
        //TODO the png can exists as a reference, "1.png.url" in which case the content of the file
        //     is the URL to where an image and that image should be displayed
        imagePane.icon = ImageIcon(pathToLocalHelp.resolve("$currentPage.png").toString())
        textPane.text = currentHTML.readText()
    }

    val currentHTML
        get() = pathToLocalHelp.resolve("$currentPage.html")
}
