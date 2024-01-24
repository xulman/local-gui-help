package sc.fiji.gui.help;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayingAroundApp {
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
			//f.add( buildImageOnlyComponent( PlayingAroundApp.class.getResource("someImg.png") ) );
			//f.add( buildImageOnlyComponent( new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.png") ) );
			//f.add( buildTextHtmlComponent( new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.html") ) );
			buildTextHtmlComponent( new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.html") );
			f.add( buildTextHtmlComponent( PlayingAroundApp.class.getResource("someImg.txt") ) );
		} catch (MalformedURLException e) {
			System.out.println("Ain't adding an image panel 'cause URL is supposedly wrong...");
		}

		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		return f;
	}

	// ==================================================================================================================
	public static void registerComponentHelp(final JComponent guiComponent, final Path pathToLocalTopic) {
		guiComponent.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F1) {
					System.out.println("Would be now starting LOCAL help from "+pathToLocalTopic);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		});
	}

	public static void registerComponentHelp(final JComponent guiComponent, final URL urlToRemoteTopic) {
		guiComponent.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F1) {
					System.out.println("Would be now starting REMOTE help from "+urlToRemoteTopic);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		});
	}

	public static Path constructPathToLocalTopics(final Class<?> appClass, final String topic) {
		try {
			return Paths.get(appClass.getResource(topic+"/1.html").toURI()).getParent();
		} catch (URISyntaxException | NullPointerException e) {
			try {
				System.err.println("Failed finding the local help "+appClass.getSimpleName()+"/"+topic
						+", trying a default placeholder instead...");
				return Paths.get(PlayingAroundApp.class.getResource("defaultDescription.html").toURI());
				//NB: notice the name of this framework...
				//TODO: the defaultDescription.html could show a demo how to create such local help
			} catch (URISyntaxException | NullPointerException ex) {
				throw new RuntimeException("Requested help ("
						+appClass.getSimpleName()+"/"+topic+") as well as default substitute help was not found.");
			}
		}
	}
	// ==================================================================================================================


	JButton panel1;
	JButton panel2;

	PlayingAroundApp() throws MalformedURLException {
		buildAppWindow(); //and sets both panels attribs
		registerComponentHelp(panel1, constructPathToLocalTopics(PanelControlledWorker.class,"PanelHelp"));
		registerComponentHelp(panel2, new URL("https://www.fi.muni.cz/~xulman/files/secret_folder/removeMe.html"));
	}

	void buildAppWindow() {
		final JFrame f = new JFrame("A Simple Demo App");

		final Color blue = new Color(128,128,255);
		final Color green = new Color(128,192,128);
		final Color gray = new Color(192,192,192);

		panel1 = new JButton("SIMPLE");
		panel1.setPreferredSize(new Dimension(200,50));
		panel1.setHorizontalAlignment(JLabel.CENTER);
		//panel1.setBorder(BorderFactory.createEtchedBorder(blue,gray));
		panel1.setBackground(blue);

		panel2 = new JButton("APP");
		panel2.setPreferredSize(new Dimension(200,50));
		panel2.setHorizontalAlignment(JLabel.CENTER);
		//panel2.setBorder(BorderFactory.createEtchedBorder(green,gray));
		panel2.setBackground(green);

		final GridBagLayout gridBagLayout = new GridBagLayout();
		f.setLayout(gridBagLayout);

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		f.add(panel1, c);
		c.gridy = 1;
		f.add(panel2, c);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			new PlayingAroundApp();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
