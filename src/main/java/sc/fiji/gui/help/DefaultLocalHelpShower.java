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

		currentPage = 1;
		fillPage(imagePane, textPane);

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		contentPane.add(imagePane, c);
		c.gridy = 1;
		contentPane.add(textPane, c);
		//TODO: add buttons!

		JFrame f = new JFrame(dialogTitle);
		f.setContentPane(contentPane);
		f.pack();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
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