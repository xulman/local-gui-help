package sc.fiji.gui.help;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HelpManager {

	public HelpManager(final Container theMainAppWindowContentPane) {
		//since keyboard event can arrive only to an element with focus,
		//we have to make sure the pane can be focused (can receive keyboard events)
		theMainAppWindowContentPane.setFocusable(true);
		theMainAppWindowContentPane.addKeyListener(new HelpKeyMonitor());

		//focus this pane whenever mouse arrives over the app window (the main content pane),
		//which makes it see all keyboard events until the focus is changed (e.g. with mouse click or tab)
		doMonitorMouseOvers = true;
		theMainAppWindowContentPane.addMouseListener(new MouseListener() {
			@Override
			public void mouseEntered(MouseEvent e) {
				theMainAppWindowContentPane.requestFocusInWindow();
			}
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
	}

	public HelpManager() {
		doMonitorMouseOvers = false;
	}

	private Component itemWithMouseOver = null;
	private final MouseOverMonitor mouseOverListener = new MouseOverMonitor();
	private final boolean doMonitorMouseOvers;

	class MouseOverMonitor implements MouseListener {
		@Override
		public void mouseEntered(MouseEvent e) {
			itemWithMouseOver = e.getComponent();
		}
		@Override
		public void mouseExited(MouseEvent e) {
			if (itemWithMouseOver == e.getComponent()) itemWithMouseOver = null;
			//NB: to make sure only myself is removed
		}
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
	}

	class HelpKeyMonitor implements KeyListener {
		final Component monitoredComponent;
		public HelpKeyMonitor(final Component forThisComponent) {
			monitoredComponent = forThisComponent;
		}
		public HelpKeyMonitor() {
			monitoredComponent = null;
			//NB: flags that the currently (at the time this listener is triggered)
			//    mouse-over'ed component should be used instead of this.monitoredComponent
		}
		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyPressed(KeyEvent e) {}
		@Override
		public void keyReleased(KeyEvent e) {
			System.out.println("key released: "+e.getKeyChar());
			if (e.getKeyCode() == KeyEvent.VK_H) {
				if (monitoredComponent != null) showItemHelp(monitoredComponent);
				else if (itemWithMouseOver != null) showItemHelp(itemWithMouseOver);
			}
		}
	}

	// ==================================================================================================================
	public void registerComponentHelp(final Component guiComponent, final Path pathToLocalTopic) {
		if (doMonitorMouseOvers) guiComponent.addMouseListener(mouseOverListener);
		guiComponent.addKeyListener(new HelpKeyMonitor(guiComponent));
		//TODO: register the component and its "action"
	}

	public void registerComponentHelp(final Component guiComponent, final URL urlToRemoteTopic) {
		if (doMonitorMouseOvers) guiComponent.addMouseListener(mouseOverListener);
		guiComponent.addKeyListener(new HelpKeyMonitor(guiComponent));
		//TODO: register the component and its "action"
	}

	public static Path constructPathToLocalTopics(final Class<?> appClass, final String topic) {
		try {
			return Paths.get(appClass.getResource(topic+"/1.html").toURI()).getParent();
		} catch (URISyntaxException | NullPointerException e) {
			try {
				System.err.println("Failed finding the local help "+appClass.getSimpleName()+"/"+topic
						+", trying a default placeholder instead...");
				return Paths.get(HelpManager.class.getResource("defaultDescription.html").toURI());
				//NB: notice the name of this framework...
				//TODO: the defaultDescription.html could show a demo how to create such local help
			} catch (URISyntaxException | NullPointerException ex) {
				throw new RuntimeException("Requested help ("
						+appClass.getSimpleName()+"/"+topic+") as well as default substitute help was not found.");
			}
		}
	}

	// ==================================================================================================================
	/**
	 * Starts the help dialog for the given component if that component has been previously registered via
	 * {@link HelpManager#registerComponentHelp(Component, Path)} or {@link HelpManager#registerComponentHelp(Component, URL)}.
	 * If null is given, the method silently quits without showing anything.
	 *
	 * @param guiItem Component of which the help should be displayed.
	 */
	public void showItemHelp(Component guiItem) {
		if (guiItem == null) {
			System.out.println("would show help, but nothing is active!?");
			return;
		}
		System.out.println("showing help for item "+guiItem);
		//TODO: show it in a separate thread, non-modal
	}








	// ==================================================================================================================
	// Actions:

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
}
