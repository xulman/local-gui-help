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
import java.net.MalformedURLException;
import java.net.URL;

import static sc.fiji.gui.help.HelpManager.constructPathToLocalTopics;

public class PlayingAroundApp {

	JButton panel1;
	JButton panel2;
	JLabel panel3;

	PlayingAroundApp() throws MalformedURLException {
		JFrame mainWindowFrame = buildAppWindow(); //and sets all GUI items attribs

		//HelpManager helpManager = new HelpManager(); //no global mouse monitoring
		HelpManager helpManager = new HelpManager(mainWindowFrame.getContentPane());

		helpManager.registerComponentHelp(panel1,
				constructPathToLocalTopics(PanelControlledWorker.class,"Topic1"),
				"HELP from LOCAL FILES");

		helpManager.registerComponentHelp(panel2,
				new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.html"),
				"HELP from REMOTE URL");

		helpManager.registerComponentHelp(panel3,
				constructPathToLocalTopics(PanelControlledWorker.class,"Topic2"),
				"ANOTHER LOCAL FILE HELP");
	}

	JFrame buildAppWindow() {
		final Panel contentPane = new Panel();
		contentPane.setBackground(gray);
		contentPane.setLayout(new GridBagLayout());

		panel1 = new JButton("SIMPLE (local help)");
		panel1.setPreferredSize(new Dimension(200,50));
		panel1.setHorizontalAlignment(JLabel.CENTER);
		//panel1.setBorder(BorderFactory.createEtchedBorder(blue,gray));
		panel1.setBackground(blue);

		panel2 = new JButton("APP  (remote help)");
		panel2.setPreferredSize(new Dimension(200,50));
		panel2.setHorizontalAlignment(JLabel.CENTER);
		//panel2.setBorder(BorderFactory.createEtchedBorder(green,gray));
		panel2.setBackground(green);

		panel3 = new JLabel("not used");
		panel3.setPreferredSize(new Dimension(200,50));
		panel3.setHorizontalAlignment(JLabel.CENTER);
		//panel3.setBorder(BorderFactory.createEtchedBorder(green,gray));

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(50,50,5,50);
		contentPane.add(panel1, c);
		c.gridy = 1;
		c.insets = new Insets(0,50,5,50);
		contentPane.add(panel2, c);
		c.gridy = 2;
		c.insets = new Insets(0,50,50,50);
		contentPane.add(panel3, c);

		final JFrame f = new JFrame("A Simple Demo App");
		f.setContentPane( contentPane );
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		return f;
	}

	final Color blue = new Color(128,128,255);
	final Color green = new Color(128,192,128);
	final Color gray = new Color(192,192,192);


	public static void main(String[] args) {
		try {
			new PlayingAroundApp();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
