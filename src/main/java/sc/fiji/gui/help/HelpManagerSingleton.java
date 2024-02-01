package sc.fiji.gui.help;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

/**
 void HM.obtain().getKeyboardAction()
 -- use for the binding+actions, triggers this manager
 -- should be bound to "the usual place in the app", could be bound in multiple places
 -- keyboard focus is (obviously) not altered

 void HM.obtain().registerComponentHelp(forThisComponent, ....help params....)
 -- registers the component into the _sorted_ list of "help-enabled components"
 -- when the manager is triggered, it attempts to find _the first_ mouse-over'ed component from the list
 */
public class HelpManagerSingleton {
	private HelpManagerSingleton() {}

	private static HelpManagerSingleton instance = null;

	public static synchronized HelpManagerSingleton obtain() {
		if (instance == null) {
			instance = new HelpManagerSingleton();
		}
		return instance;
	}

	// ==================================================================================================================
	public Runnable getKeyboardAction() {
		return this::processHelpKey;
	}

	public KeyListener getKeyboardListener(final Set<Integer> watchForTheseKeys) {
		return new HelpKeyListener(watchForTheseKeys);
	}
	//
	private class HelpKeyListener implements KeyListener {
		private final Set<Integer> hotKeys;
		HelpKeyListener(final Set<Integer> watchForTheseKeys) {
			hotKeys = new HashSet<>(watchForTheseKeys);
		}
		//
		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyPressed(KeyEvent e) {}
		@Override
		public void keyReleased(KeyEvent e) {
			if (hotKeys.contains( e.getKeyCode() )) processHelpKey();
		}
	}

	// ==================================================================================================================
	class ComponentWithHelp {
		final Component component;
		final HelpShower helpDialog;

		ComponentWithHelp(final Component guiElem, final HelpShower helpDialog) {
			this.component = guiElem;
			this.helpDialog = helpDialog;
		}
	}

	private final List<ComponentWithHelp> helpDialogs = new LinkedList<>();

	/**
	 * Starts the help dialog for the given component if that component has been previously registered via
	 * the family of registering methods, such as {@link HelpManager#registerComponentHelp(Component, Path, String)}.
	 * If it wasn't registered or null was given, any particular dialog is thus not available, and nothing is
	 * shown consequently leaving the call with false return value.
	 *
	 * @param guiItem Component of which the help should be displayed.
	 * @return true when help dialog for the input component has been registered previously and was thus shown.
	 */
	public boolean showItemHelp(Component guiItem) {
		if (guiItem == null) return false;
		for (ComponentWithHelp item : helpDialogs) {
			if (guiItem == item.component) {
				item.helpDialog.showNonModalHelpNow();
				return true;
			}
		}
		return false;
	}

	/**
	 * Scans the registered GUI components to look for the first one under the mouse cursor.
	 * The current state (visibility, position and size) of the components is considered,
	 * as well as the current mouse position, naturally.
	 */
	private void processHelpKey() {
		for (ComponentWithHelp item : helpDialogs) {
			//TODO: remove the printouts
			System.out.println("Help: Considering component: " + item.component.getClass().getSimpleName()+", visible="+item.component.isShowing());
			if (item.component.isShowing() && isCurrentMousePosOverComponent(item.component)) {
				System.out.println("Would be now printing help for component: " + item.component.getClass().getSimpleName());
				item.helpDialog.showNonModalHelpNow();
				return;
			}
		}
	}

	private boolean isCurrentMousePosOverComponent(final Component component) {
		final Point p = MouseInfo.getPointerInfo().getLocation();
		final Point c = component.getLocationOnScreen();
		p.translate( -c.x, -c.y ); //NB: the same as ".sub(component.corner)"
		return !(p.x < 0 || p.y < 0 || p.x >= component.getWidth() || p.y >= component.getHeight());
	}

	private void addComponent(final Component component, final HelpShower helpDialog) {
		final int inArea = component.getWidth() * component.getHeight();
		int index = -1;
		for (ComponentWithHelp registeredHelp : helpDialogs) {
			Component c = registeredHelp.component;
			++index;

			int cArea = c.getWidth() * c.getHeight();
			if (inArea < cArea) {
				//'index' points now on the enlisted component that's larger
				helpDialogs.add( index, new ComponentWithHelp(component,helpDialog) );
				return;
			}
		}
		//if we got here, there was no smaller registered component
		helpDialogs.add(  new ComponentWithHelp(component,helpDialog) );
	}

	/**
	 * Unregisters the given component together with its local help dialog.
	 * It returns the status of the {@link List::remove()} operation.
	 * @param component The component that shall no longer provide a local help.
	 * @return False if the given component was null or not present in the list, else True.
	 */
	public boolean unregisterComponentHelp(final Component component) {
		try {
			return helpDialogs.remove(component);
		} catch (RuntimeException e) {
			return false;
		}
	}

	// ==================================================================================================================
	public void registerComponentHelp(final Component guiComponent, final HelpShower ownHelpDialog) {
		addComponent(guiComponent, ownHelpDialog);
	}

	public void registerComponentHelp(final Component guiComponent, final Path pathToLocalTopic, final String dialogTitle) {
		registerComponentHelp(guiComponent, pathToLocalTopic, dialogTitle, 0);
	}

	public void registerComponentHelp(final Component guiComponent, final Path pathToLocalTopic,
	                                  final String dialogTitle, final int startOnThisPageNumber) {
		//TODO: add the first start page number into the LocalHelpShower
		addComponent(guiComponent, new DefaultLocalHelpShower(pathToLocalTopic, dialogTitle));
	}

	public void registerComponentHelp(final Component guiComponent, final URL urlToRemoteTopic, final String dialogTitle) {
		addComponent(guiComponent, new DefaultRemoteHelpShower(urlToRemoteTopic, dialogTitle));
	}

	public void registerComponentHelpForWebBrowser(final Component guiComponent, final URL urlToRemoteTopic) {
		addComponent(guiComponent, () -> openUrlInSystemBrowser(urlToRemoteTopic));
	}


	/**
	 * An aider to obtain an absolute, local filesystem path to the resources folder of the provided class,
	 * and this path is concatenated with a relative path to the 'topic'. Typically, a class is used that is responsible for
	 * the functionality behind a GUI element for which {@link HelpManager#registerComponentHelp(Component, Path, String)}
	 * is called, and that carries the help resources with it. The path to the class resources folder is yielded by
	 * querying for {@code appClass.getResource(topic+"/1.html")}.
	 *
	 * @param appClass The class whose resource folder is extracted.
	 * @param topic The sub-folder in the resources folder is appended to the constructed path.
	 * @return An absolute, local filesystem path to the topic.
	*/
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


	/**
	 * Opens OS-default web browser on the specified page.
	 * @param url URL to the requested content.
	 */
	public static void openUrlInSystemBrowser(final URL url) {
		openUrlInSystemBrowser(url.toString());
	}

	/**
	 * Opens OS-default web browser on the specified page.
	 * @param url URL to the requested content.
	 */
	public static void openUrlInSystemBrowser(final String url) {
		final String myOS = System.getProperty("os.name").toLowerCase();
		try {
			if (myOS.contains("mac")) {
				Runtime.getRuntime().exec("open "+url);
			}
			else if (myOS.contains("nux") || myOS.contains("nix")) {
				Runtime.getRuntime().exec("xdg-open "+url);
			}
			else if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(url));
			}
			else {
				System.out.println("Please, open this URL yourself: "+url);
			}
		} catch (IOException | URISyntaxException ignored) {}
	}
}