package sc.fiji.gui.help;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowStateListener;

/**
 void HM.obtain().getKeyboardAction()
 -- use for the binding+actions, triggers this manager
 -- should be bound to "the usual place in the app", could be bound in multiple places
 -- keyboard focus is (obviously) not altered

 void HM.obtain().registerComponentHelp(forThisComponent, ....help params....)
 -- add mouse listeners to inform if/where a mouse is
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
		guiComponent.addMouseListener( new MouseOverMonitor(guiComponent) );
		//helpDialogs.put(guiComponent, ownHelpDialog);
	}

	//TODO: if e(vent).getComponent() is used well, the 'monitoredComponent' private attrib is useless,
	//      and then we would be good with just one instance of this class....
	class MouseOverMonitor implements MouseListener {
		public MouseOverMonitor(final Component monitoredComponent) {
			this.monitoredComponent = monitoredComponent;
		}
		final Component monitoredComponent;

		@Override
		public void mouseEntered(MouseEvent e) {
			itemWithMouseOver = e.getComponent(); //NB: should be the same as 'monitoredComponent'
		}
		@Override
		public void mouseExited(MouseEvent e) {
			//NB: to make sure only myself is removed
			//    (just in case somebody else managed to set this attrib already,
			//     which could happen when mouseEntered() on that somebody was called
			//     before mouseExited() of the actual mouse-over'ed item
			//     (theoretically, it shouldn't happen) )
			if (itemWithMouseOver == e.getComponent()) { //NB: should be the same as 'monitoredComponent'
				//is mouse leaving this (container) component through its outside boundary?
				//(in contrast to "leaving into" another (child) component that is inside/over
				// this (container) component)
				if (!isCurrentMousePosOverComponent(monitoredComponent)) {
					//left towards outside
					itemWithMouseOver = null;
				}
			}
		}
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
	}

	private boolean isCurrentMousePosOverComponent(final Component component) {
		final Point p = MouseInfo.getPointerInfo().getLocation();
		final Point c = component.getLocationOnScreen();
		p.translate( -c.x, -c.y ); //NB: the same as ".sub(component.corner)"
		return !(p.x < 0 || p.y < 0 || p.x >= component.getWidth() || p.y >= component.getHeight());
	}
}
