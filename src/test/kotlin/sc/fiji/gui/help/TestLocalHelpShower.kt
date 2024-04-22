package sc.fiji.gui.help

fun main() {
    val path = HelpManager.constructPathToLocalTopics<PanelControlledWorker>("Topic1")
    DefaultLocalHelpShower(path, "test dialog").showNonModalHelpNow()
}