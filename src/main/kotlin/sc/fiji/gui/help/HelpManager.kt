package sc.fiji.gui.help

import java.awt.Component
import java.awt.Desktop
import java.awt.MouseInfo
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * void HM.obtain().getKeyboardAction()
 * -- use for the binding+actions, triggers this manager
 * -- should be bound to "the usual place in the app", could be bound in multiple places
 * -- keyboard focus is (obviously) not altered
 *
 * void HM.obtain().registerComponentHelp(forThisComponent, ....help params....)
 * -- registers the component into the _sorted_ list of "help-enabled components"
 * -- when the manager is triggered, it attempts to find _the first_ mouse-over'ed component from the list
 */
object HelpManager {
    /**
     * Exposes the help manager's entry point (handle) to Action-oriented applications,
     * a method that's executed when the corresponding Action is triggered.
     * @return Reference on the right method from this help manager
     */
    val keyboardAction: Runnable
        get() = Runnable { processHelpKey() }

    /**
     * Creates [KeyListener], to be attached in the application, that
     * activates this help manager when any of the given keys are pressed.
     * @param watchForTheseKeys Set of keys that should trigger this help manager.
     * @return Always a new object that implements the [KeyListener].
     */
    fun getKeyboardListener(watchForTheseKeys: Set<Int>): KeyListener =
        HelpKeyListener(watchForTheseKeys)

    //
    private class HelpKeyListener(watchForTheseKeys: Set<Int>) : KeyListener {
        private val hotKeys: Set<Int> = HashSet(watchForTheseKeys)

        //
        override fun keyTyped(e: KeyEvent) {}
        override fun keyPressed(e: KeyEvent) {}
        @Synchronized
        override fun keyReleased(e: KeyEvent) {
            if (e.keyCode in hotKeys) processHelpKey()
        }
    }

    // ==================================================================================================================
    private infix fun Component.withHelp(helpDialog: HelpShower) = ComponentWithHelp(this, helpDialog)
    private class ComponentWithHelp(val component: Component, val helpDialog: HelpShower)

    private val helpDialogs: MutableList<ComponentWithHelp> = ArrayList(30)

    /**
     * Starts the help dialog for the given component if that component has been previously registered via
     * the family of registering methods, such as [HelpManager.registerComponentHelp].
     * If it wasn't registered or null is given, any particular dialog is thus not available, and nothing is
     * shown consequently leaving the call with false return value.
     *
     * @param guiItem Component of which the help should be displayed.
     * @return true when help dialog for the input component has been registered previously and was thus shown.
     */
    fun showHelpNow(guiItem: Component?): Boolean {
        if (guiItem == null) return false
        for (item in helpDialogs) {
            if (guiItem === item.component) {
                item.helpDialog.showNonModalHelpNow()
                return true
            }
        }
        return false
    }

    /**
     * Scans the registered GUI components to look for the first one under the mouse cursor.
     * The current state (visibility, position and size) of the components is considered,
     * as well as the current mouse position, naturally.
     *
     * This is the entry point that's called after triggering the local help in the client application.
     */
    private fun processHelpKey() {
        for (item in helpDialogs) {
            //Enable to debug:
            //System.out.println("Help: Considering component: " + item.component.getClass().getSimpleName()+", visible="+item.component.isShowing());
            if (item.component.isShowing && isCurrentMousePosOverComponent(item.component)) {
                //Enable to debug:
                //System.out.println("Would be now printing help for component: " + item.component.getClass().getSimpleName());
                item.helpDialog.showNonModalHelpNow()
                return
            }
        }
    }

    private fun isCurrentMousePosOverComponent(component: Component): Boolean {
        val p = MouseInfo.getPointerInfo().location
        val c = component.locationOnScreen
        p.translate(-c.x, -c.y) //NB: the same as ".sub(component.corner)"
        return !(p.x < 0 || p.y < 0 || p.x >= component.width || p.y >= component.height)
    }

    private fun Component.add(helpDialog: HelpShower) {
        val inArea = width * height
        var index = -1
        for (registeredHelp in helpDialogs) {
            val c = registeredHelp.component
            ++index

            //NB: considering the _current_ area of the (previously) registered components
            //    (not a stored area that might no longer be accurate),
            //    against the _currently_ added component
            val cArea = c.width * c.height
            if (inArea < cArea) {
                //'index' points now on the enlisted component that's larger
                helpDialogs.add(index, ComponentWithHelp(this, helpDialog))
                return
            }
        }
        //if we got here, there was no smaller registered component
        helpDialogs += ComponentWithHelp(this, helpDialog)
    }

    /**
     * Unregisters the given guiComponent together with its local help dialog.
     * It returns the status of the [::remove()][List] operation.
     * @param guiComponent The guiComponent that shall no longer provide a local help.
     * @return False if the given guiComponent was null or not present in the list, else True.
     */
    fun Component.unregisterHelp(): Boolean = helpDialogs.removeIf { it.component === this }

    // ==================================================================================================================
    infix fun Component.registerHelp(ownHelpDialog: HelpShower) = add(ownHelpDialog)

    @JvmOverloads
    fun registerComponentHelp(component: Component, pathToLocalTopic: Path, dialogTitle: String, startOnThisPageNumber: Int = 0) {
        //TODO: add the first start page number into the LocalHelpShower
        component.add(DefaultLocalHelpShower(pathToLocalTopic, dialogTitle))
    }

    fun registerComponentHelp(component: Component, urlToRemoteTopic: URL, dialogTitle: String) =
        component.add(DefaultRemoteHelpShower(urlToRemoteTopic, dialogTitle))

    fun registerComponentHelpForWebBrowser(component: Component, urlToRemoteTopic: URL) =
        component.add { openUrlInSystemBrowser(urlToRemoteTopic) }

    /**
     * An aider to obtain an absolute, local filesystem path to the resources folder of the provided class,
     * and this path is concatenated with a relative path to the 'topic'. Typically, a class is used that is responsible for
     * the functionality behind a GUI element for which [HelpManager.registerComponentHelp]
     * is called, and that carries the help resources with it. The path to the class resources folder is yielded by
     * querying for `appClass.getResource(topic+"/1.html")`.
     *
     * @param appClass The class whose resource folder is extracted.
     * @param topic The sub-folder in the resources folder is appended to the constructed path.
     * @return An absolute, local filesystem path to the topic.
     */
    inline infix fun <reified C> constructPathToLocalTopics(topic: String): Path {
        val appClass = C::class.java
        val error = "Requested help (${appClass.simpleName}/$topic) as well as default substitute help was not found."
        try {
            return Paths.get(appClass.getResource("$topic/1.html").toURI()).parent
        } catch (e: URISyntaxException) {
            try {
                System.err.println("Failed finding the local help " + appClass.simpleName + "/" + topic
                                   + ", trying a default placeholder instead...")
                return Paths.get(HelpManager::class.java.getResource("defaultDescription.html").toURI())
                //NB: notice the name of this framework...
            } catch (ex: URISyntaxException) {
                throw RuntimeException(error)
            } catch (ex: NullPointerException) {
                throw RuntimeException(error)
            }
        } catch (e: NullPointerException) {
            try {
                System.err.println("Failed finding the local help ${appClass.simpleName}/$topic, trying a default placeholder instead...")
                return Paths.get(HelpManager::class.java.getResource("defaultDescription.html").toURI())
            } catch (ex: URISyntaxException) {
                throw RuntimeException(error)
            } catch (ex: NullPointerException) {
                throw RuntimeException(error)
            }
        }
    }

    /**
     * An aider to construct URL objects without the hassle of dealing with the potential [MalformedURLException].
     * If invalid input is given, the methods return URL pointing at https://scijava.org/.
     *
     * @param urlAsPlainText URL string to be wrapped into a proper [URL] object.
     * @return URL object wrapped around the textual URL.
     */
    fun constructURL(urlAsPlainText: String): URL {
        return try {
            URI(urlAsPlainText).toURL()
        } catch (e: MalformedURLException) {
            try {
                URI("https://scijava.org/").toURL()
            } catch (ex: MalformedURLException) {
                throw RuntimeException("Total failure: Couldn't construct URL obj around simple valid URL string.")
            }
        }
    }


    /**
     * Opens OS-default web browser on the specified page.
     * @param url URL to the requested content.
     */
    fun openUrlInSystemBrowser(url: URL) = openUrlInSystemBrowser(url.toString())

    /**
     * Opens OS-default web browser on the specified page.
     * @param url URL to the requested content.
     */
    fun openUrlInSystemBrowser(url: String) {
        val myOS = System.getProperty("os.name").lowercase(Locale.getDefault())
        when {
            "mac" in myOS -> Runtime.getRuntime().exec(arrayOf("open", url))
            "nux" in myOS || "nix" in myOS -> Runtime.getRuntime().exec(arrayOf("xdg-open", url))
            Desktop.isDesktopSupported() -> Desktop.getDesktop().browse(URI(url))
            else -> println("Please, open this URL yourself: $url")
        }
    }
}