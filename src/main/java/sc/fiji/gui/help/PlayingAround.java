package sc.fiji.gui.help;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PlayingAround {
	JComponent buildTextHtmlComponent(URL pathToText) {
		JEditorPane editorPane;
		try {
			editorPane = new JEditorPane( pathToText );
			editorPane.setEditable( false );
		} catch (IOException e) {
			System.err.println("choosing fall-back content");
			editorPane = new JEditorPane();
			editorPane.setText("FAILED OPENING THE CONTENT HERE.");
		}
		return editorPane;
	}

	JComponent buildImageOnlyComponent(URL pathToImage) {
		ImageIcon icon = new ImageIcon(pathToImage);
		return new JLabel(icon);
	}

	JFrame buildPanel() {
		JFrame f = new JFrame("PLAYING AROUND");

		try {
			//f.add( buildImageOnlyComponent( PlayingAround.class.getResource("someImg.png") ) );
			//f.add( buildImageOnlyComponent( new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.png") ) );
			//f.add( buildTextHtmlComponent( new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.html") ) );
			buildTextHtmlComponent( new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.html") );
			f.add( buildTextHtmlComponent( PlayingAround.class.getResource("someImg.txt") ) );
		} catch (MalformedURLException e) {
			System.out.println("Ain't adding an image panel 'cause URL is supposedly wrong...");
		}

		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		return f;
	}

	public static void main(String[] args) {
		new PlayingAround().buildPanel();
	}
}
