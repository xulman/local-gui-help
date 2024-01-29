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
package sc.fiji.gui.help;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class DefaultRemoteHelpShower implements HelpShower {
	final String dialogTitle;
	final URL urlToRemoteHelp;

	DefaultRemoteHelpShower(final URL urlToRemoteHelp, final String dialogWindowTitle) {
		this.urlToRemoteHelp = urlToRemoteHelp;
		this.dialogTitle = dialogWindowTitle;
	}

	@Override
	public void showNonModalHelpNow() {
		final Panel contentPane = new Panel();
		contentPane.setLayout(new GridBagLayout());

		//TODO: make these a common static attrib, part of the iface?
		final Dimension minSize = new Dimension(300, 300);
		final Dimension preferredSize = new Dimension(600, 600);

		final JEditorPane textPane = new JEditorPane();
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		try {
			textPane.setPage(urlToRemoteHelp);
		} catch (IOException e) {
			textPane.setText("FALL BACK CONTENT because failed opening the URL:<br/>"+urlToRemoteHelp);
		}
		textPane.setMinimumSize(minSize);
		textPane.setPreferredSize(preferredSize);

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		contentPane.add(textPane, c);

		JFrame f = new JFrame(dialogTitle);
		f.setContentPane(contentPane);
		f.pack();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}
}
