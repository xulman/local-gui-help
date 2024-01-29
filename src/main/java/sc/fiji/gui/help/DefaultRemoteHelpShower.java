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
