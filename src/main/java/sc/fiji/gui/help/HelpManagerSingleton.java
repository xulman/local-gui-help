package sc.fiji.gui.help;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
	public static int HELP_KEY1 = KeyEvent.VK_H;
	public static int HELP_KEY2 = KeyEvent.VK_F1;

	//TODO: getKeyboardListener for the given list of keys... Set<int> keys
	public KeyListener getKeyboardListener() {
		return singletonKeyListener;
	}
	public Runnable getKeyboardAction() {
		return this::processHelpKey;
	}

	private final KeyListener singletonKeyListener = new KeyListener() {
		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyPressed(KeyEvent e) {}
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == HELP_KEY1 || e.getKeyCode() == HELP_KEY2) processHelpKey();
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

}
