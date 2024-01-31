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
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultLocalHelpShower implements HelpShower {
	final String dialogTitle;
	final Path pathToLocalHelp;
	int currentPage;

	DefaultLocalHelpShower(final Path pathToLocalHelp, final String dialogWindowTitle) {
		this.pathToLocalHelp = pathToLocalHelp;
		this.dialogTitle = dialogWindowTitle;
	}

	@Override
	public void showNonModalHelpNow() {
		final Panel contentPane = new Panel();
		contentPane.setLayout(new GridBagLayout());

		final Dimension minSizeImg = new Dimension(300, 200);
		final Dimension minSizeTxt = new Dimension(300, 100);
		final Dimension preferredSizeImg = new Dimension(600, 400);
		final Dimension preferredSizeTxt = new Dimension(600, 200);

		final JLabel imagePane = new JLabel();
		imagePane.setMinimumSize(minSizeImg);
		imagePane.setPreferredSize(preferredSizeImg);

		final JEditorPane textPane = new JEditorPane();
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.setMinimumSize(minSizeTxt);
		textPane.setPreferredSize(preferredSizeTxt);

		//TODO: add buttons!
		final JButton prevB = new JButton("Previous");
		final JButton nextB = new JButton("Next");
		final JButton closeB = new JButton("Got it, close");

		currentPage = 1;
		fillPage(imagePane, textPane);
		//
		prevB.addActionListener((l) -> {
					if (currentPage > 1) {
						--currentPage;
						fillPage(imagePane, textPane);
					}
				});
		nextB.addActionListener((l) -> {
				++currentPage;
				fillPage(imagePane, textPane);
		});

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		contentPane.add(imagePane, c);
		c.gridy = 1;
		contentPane.add(textPane, c);

		//TODO: add buttons!
		c.gridwidth = 1;
		c.weightx = 0.33;
		c.gridy = 2;
		contentPane.add(prevB, c);
		c.gridx = 1;
		contentPane.add(nextB, c);
		c.gridx = 2;
		contentPane.add(closeB, c);

		JFrame f = new JFrame(dialogTitle);
		f.setContentPane(contentPane);
		f.pack();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);

		closeB.addActionListener((l) -> f.setVisible(false));
	}

	void fillPage(final JLabel imagePane, final JEditorPane textPane) {
		//TODO can fail reading the png, provide placeholder then.. like missed file and the path to it
		imagePane.setIcon(new ImageIcon(pathToLocalHelp.resolve(currentPage + ".png").toString()));
		textPane.setText(readCompleteFile(pathToLocalHelp.resolve(currentPage + ".html")));
	}

	public static String readCompleteFile(final Path path) {
		try {
			return new String(Files.readAllBytes(path));
		} catch (IOException e) {
			return "FALL BACK CONTENT because failed opening the file:<br/>"+path;
		}
	}
}
