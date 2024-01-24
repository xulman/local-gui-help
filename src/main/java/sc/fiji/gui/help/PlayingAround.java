package sc.fiji.gui.help;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

public class PlayingAround {
	JComponent buildTextHtmlComponent() {
			return null;
	}

	JComponent buildImageOnlyComponent(URL pathToImage) {
		ImageIcon icon = new ImageIcon(pathToImage);
		return new JLabel(icon);
	}

	JFrame buildPanel() {
		JFrame f = new JFrame("PLAYING AROUND");

		f.add( buildImageOnlyComponent(PlayingAround.class.getResource("someImg.png")) );
		try {
			f.add( buildImageOnlyComponent(new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.png")) );
		} catch (MalformedURLException e) {
			System.out.println("Ain't adding an image panel 'cause URL is supposedly wrong...");
		}

		f.pack();
		f.setVisible(true);
		return f;
	}

	public static void main(String[] args) {
		new PlayingAround().buildPanel();
	}
}
