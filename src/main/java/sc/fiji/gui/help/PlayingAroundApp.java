package sc.fiji.gui.help;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
		guiComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H,0), "local_gui_help_key");
		guiComponent.getActionMap().put("local_gui_help_key", new LocalFilesHelp(pathToLocalTopic));
	}

	public static void registerComponentHelp(final JComponent guiComponent, final URL urlToRemoteTopic) {
		guiComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H,0), "local_gui_help_key2");
		guiComponent.getActionMap().put("local_gui_help_key2", new RemoteContentHelp(urlToRemoteTopic));
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

	/**
	 * Action associated with its own help data, will show the relevant GUI help dialog.
	 * Here, the help data is stored locally in some folder.
	 */
	static class LocalFilesHelp extends AbstractAction {
		final Path pathToMyLocalTopic;
		public LocalFilesHelp(final Path pathToLocalTopic) {
			super();
			this.pathToMyLocalTopic = pathToLocalTopic;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Would be now starting LOCAL help from "+pathToMyLocalTopic);
		}
	}
	/**
	 * Action associated with its own help data, will show the relevant GUI help dialog.
	 * Here, the help data will be fetched from the given URL.
	 */
	static class RemoteContentHelp extends AbstractAction {
		final URL urlToMyRemoteTopic;
		public RemoteContentHelp(final URL pathToRemoteTopic) {
			super();
			this.urlToMyRemoteTopic = pathToRemoteTopic;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Would be now starting REMOTE help from "+urlToMyRemoteTopic);
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

	final Color blue = new Color(128,128,255);
	final Color green = new Color(128,192,128);
	final Color gray = new Color(192,192,192);

	void buildAppWindow() {
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

		JButton panel3 = new JButton("not used");
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

		f.getContentPane().setFocusable(true);
		f.getContentPane().addKeyListener(new MyKeyListener("APP window"));
		f.getContentPane().addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {
				System.out.println("mouse entered the app");
				f.getContentPane().requestFocusInWindow();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				System.out.println("mouse left the app");
			}
		});

		f.pack();
		f.setVisible(true);
	}

	public static class MyKeyListener implements KeyListener {
		public MyKeyListener(final String ownerName) { this.owner = ownerName; }
		final String owner;

		@Override
		public void keyTyped(KeyEvent e) {
			System.out.println(owner+": key typed");
		}
		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println(owner+": key pressed");
		}
		@Override
		public void keyReleased(KeyEvent e) {
			System.out.println(owner+": key released");
		}
	}

	public static void main(String[] args) {
		try {
			new PlayingAroundApp();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
