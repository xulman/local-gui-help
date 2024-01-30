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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HelpManager {
	public static int HELP_KEY1 = KeyEvent.VK_H;
	public static int HELP_KEY2 = KeyEvent.VK_F1;

	public HelpManager(final Container theMainAppWindowContentPane) {
		this.theMainAppWindowContentPane = theMainAppWindowContentPane;

		//since keyboard event can arrive only to an element with focus,
		//we have to make sure the pane can be focused (can receive keyboard events)
		theMainAppWindowContentPane.setFocusable(true);
		theMainAppWindowContentPane.addKeyListener(new HelpKeyMonitor());

		//show no help when help key is pressed with mouse over the monitored main window,
		//it can be overridden later from the client code
		helpDialogs.put(theMainAppWindowContentPane, noHelpShower);

		//focuses this pane whenever mouse arrives over the app window (the main content pane),
		//which makes it see all keyboard events until the focus is changed (e.g. with mouse click or tab)
		doMonitorMouseOvers = true;
		theMainAppWindowContentPane.addMouseListener(new MouseListener() {
			@Override
			public void mouseEntered(MouseEvent e) {
				theMainAppWindowContentPane.requestFocusInWindow();
				itemWithMouseOver = theMainAppWindowContentPane;
			}
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {
				if (itemWithMouseOver == theMainAppWindowContentPane) {
					final int mouseX = e.getX();
					final int mouseY = e.getY();
					//is mouse leaving this (container) component through its outside boundary?
					//(in contrast to "leaving into" another (child) component that is inside/over
					// this (container) component)
					if (mouseX < 0 || mouseX >= theMainAppWindowContentPane.getWidth()
							|| mouseY < 0 || mouseY >= theMainAppWindowContentPane.getHeight()) {
						//left outside
						itemWithMouseOver = null;
					}
				}
			}
		});
	}

	public HelpManager() {
		doMonitorMouseOvers = false;
		theMainAppWindowContentPane = null;
	}

	private final Component theMainAppWindowContentPane;
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
			//    (just in case somebody else managed to set this attrib already,
			//     which could happen when mouseEntered() on that somebody was called
			//     before mouseExited() of the actual mouse-over'ed item
			//     (theoretically, it shouldn't happen) )
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
			//TODO: this goes into the debug branch
			//System.out.println("key released: "+e.getKeyChar());
			if (e.getKeyCode() == HELP_KEY1 || e.getKeyCode() == HELP_KEY2) {
				if (monitoredComponent != null) showItemHelp(monitoredComponent);
				else if (itemWithMouseOver != null) showItemHelp(itemWithMouseOver);
			}
		}
	}

	// ==================================================================================================================
	public void registerComponentHelp(final Component guiComponent, final Path pathToLocalTopic, final String dialogTitle) {
		if (doMonitorMouseOvers) guiComponent.addMouseListener(mouseOverListener);
		guiComponent.addKeyListener(new HelpKeyMonitor(guiComponent));
		helpDialogs.put(guiComponent, new DefaultLocalHelpShower(pathToLocalTopic, dialogTitle));
	}

	public void registerComponentHelp(final Component guiComponent, final URL urlToRemoteTopic, final String dialogTitle) {
		if (doMonitorMouseOvers) guiComponent.addMouseListener(mouseOverListener);
		guiComponent.addKeyListener(new HelpKeyMonitor(guiComponent));
		helpDialogs.put(guiComponent, new DefaultRemoteHelpShower(urlToRemoteTopic, dialogTitle));
	}

	public void registerComponentHelp(final Component guiComponent, final HelpShower ownHelpDialog) {
		if (doMonitorMouseOvers) guiComponent.addMouseListener(mouseOverListener);
		guiComponent.addKeyListener(new HelpKeyMonitor(guiComponent));
		helpDialogs.put(guiComponent, ownHelpDialog);
	}

	public void registerComponentHelp(final Path pathToLocalTopic, final String dialogTitle) {
		if (theMainAppWindowContentPane != null)
			helpDialogs.put(theMainAppWindowContentPane, new DefaultLocalHelpShower(pathToLocalTopic, dialogTitle));
	}

	public void registerComponentHelp(final URL urlToRemoteTopic, final String dialogTitle) {
		if (theMainAppWindowContentPane != null)
			helpDialogs.put(theMainAppWindowContentPane, new DefaultRemoteHelpShower(urlToRemoteTopic, dialogTitle));
	}

	public void registerComponentHelp(final HelpShower ownHelpDialog) {
		if (theMainAppWindowContentPane != null) helpDialogs.put(theMainAppWindowContentPane, ownHelpDialog);
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

	/**
	 * An aider to construct URL objects without the hassle of dealing with the potential {@link MalformedURLException}.
	 * If invalid input is given, the methods return URL pointing at https://scijava.org/.
	 *
	 * @param urlAsPlainText URL string to be wrapped into a proper {@link URL} object.
	 * @return URL object wrapped around the textual URL.
	 */
	public static URL constructURL(final String urlAsPlainText) {
		try {
			return new URL(urlAsPlainText);
		} catch (MalformedURLException e) {
			try {
				return new URL("https://scijava.org/");
			} catch (MalformedURLException ex) {
				throw new RuntimeException("Total failure: Couldn't construct URL obj around simple valid URL string.");
			}
		}
	}

	// ==================================================================================================================
	/**
	 * Starts the help dialog for the given component if that component has been previously registered via
	 * the family of registering methods, such as {@link HelpManager#registerComponentHelp(Component, Path, String)}.
	 * If null is given, the method silently quits without showing anything.
	 *
	 * @param guiItem Component of which the help should be displayed.
	 */
	public void showItemHelp(Component guiItem) {
		if (guiItem == null) {
			//TODO: add log consumer to this class
			//TODO: this goes into the debug branch
			//System.err.println("would show help, but nothing is active!?");
			return;
		}
		//TODO: this goes into the debug branch
		//System.out.println("showing help for item "+guiItem);
		helpDialogs.get(guiItem).showNonModalHelpNow();
		//TODO: make resilient when the guiItem is by chance not found!
		//TODO: show it in a separate thread, non-modal
	}

	private final Map<Component, HelpShower> helpDialogs = new HashMap<>(10);
	private static final HelpShower noHelpShower = () -> { /* does nothing intentionally */ };
}
