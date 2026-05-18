package sc.fiji.gui.help.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;

import sc.fiji.gui.help.HelpManager;

public class HowToIntegrateHelpManager extends JFrame {
	private final JButton button1, button2;
	private final JPanel panel;

	public HowToIntegrateHelpManager() throws MalformedURLException {
		//priklad kodu, ktery je z nejake hypoteticke vetsi aplikace a
		//ktery vytvari dve testovaci tlacitka a dialog, jez je zobrazuje
		button1 = new JButton("Unknown functionality");
		button2 = new JButton("Less obvious functionality");
		panel = setupPanel();

		//takto se k tlacitkum pripoji URL s jejich dokumentaci
		HelpManager.obtain().registerComponentHelpForWebBrowser(button1, new URL("http://app.website.doc/topic1"));
		HelpManager.obtain().registerComponentHelpForWebBrowser(button2, new URL("http://app.website.doc/topic3"));

		//takto se aktivuje Ctrl+H klavesova zkratka, ktera spousti napovedu
		panel.getInputMap().put(KeyStroke.getKeyStroke("ctrl H"), "local-gui-help");
		panel.getActionMap().put("local-gui-help", HelpManager.obtain().getKeyboardAction());
	}

	private JPanel setupPanel() {
		//.........

		setTitle("How to integrate and use the local GUI help feature");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 150);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,20,0,20);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(button1, gbc);
		gbc.gridy = 1;
		panel.add(button2, gbc);

		gbc.insets = new Insets(20,0,10,0);
		gbc.fill = 0;
		gbc.gridx = 0;
		gbc.gridy = 3;
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(ignore -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
		panel.add(closeBtn, gbc);

		add(panel);
		setVisible(true);

		return panel;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				new HowToIntegrateHelpManager();
			}
			catch (MalformedURLException e) { /* empty */ }
		});
	}
}