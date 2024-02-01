package sc.fiji.gui.help;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowStateListener;

/**
 void HM.obtain().getKeyboardAction()
 -- use for the binding+actions, triggers this manager
 -- should be bound to "the usual place in the app", could be bound in multiple places
 -- keyboard focus is (obviously) not altered

 void HM.obtain().registerComponentHelp(forThisComponent, ....help params....)
 -- important when this manager is triggered
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
	};

	private void processHelpKey() {
		if (itemWithMouseOver != null) {
			if (isCurrentMousePosOverComponent(itemWithMouseOver)) {
				System.out.println("Would be now printing help for component: " + itemWithMouseOver.getClass().getSimpleName());
				//showItemHelp(itemWithMouseOver);
			} else {
				itemWithMouseOver = null;
			}
		}
	}

	private Component itemWithMouseOver = null;

	// ==================================================================================================================
	public void registerComponentHelp(final Component guiComponent, final HelpShower ownHelpDialog) {
		//helpDialogs.put(guiComponent, ownHelpDialog);
	}

		}

		}
			}
		}
	}

	private boolean isCurrentMousePosOverComponent(final Component component) {
		final Point p = MouseInfo.getPointerInfo().getLocation();
		final Point c = component.getLocationOnScreen();
		p.translate( -c.x, -c.y ); //NB: the same as ".sub(component.corner)"
		return !(p.x < 0 || p.y < 0 || p.x >= component.getWidth() || p.y >= component.getHeight());
	}
}
