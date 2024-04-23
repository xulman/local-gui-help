package sc.fiji.gui.help

import java.nio.file.Path
import javax.swing.ImageIcon

operator fun Path.div(fileName: String): Path = resolve(fileName)

fun ImageIcon(path: Path) = ImageIcon(path.toAbsolutePath().toString())