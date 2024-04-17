package sc.fiji.gui.help;

public class TestLocalHelpShower {
	public static void main(String[] args) {
		new DefaultLocalHelpShower(
				HelpManager.constructPathToLocalTopics(PanelControlledWorker.class, "Topic1"),
				"test dialog").showNonModalHelpNow();
	}
}