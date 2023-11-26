import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.appModule
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import viewmodel.ClipboardViewModel
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent

fun main() = application {
    startKoin {
        modules(appModule)
    }

    val viewModel: ClipboardViewModel by inject(ClipboardViewModel::class.java)
    val icon = painterResource("sharingan.ico")
    Window(
        title = "Sharingan",
        icon = icon,
        visible = true,
        alwaysOnTop = true,
        onCloseRequest = {
            viewModel.onCleared()
            exitApplication()
        }
    ) {
        App(viewModel)
    }
}

fun simulatePaste() {
    val robot = java.awt.Robot()
    val ctrlKey =
        if (System.getProperty("os.name").toLowerCase().contains("mac")) KeyEvent.VK_META else KeyEvent.VK_CONTROL
    robot.keyPress(ctrlKey)
    robot.keyPress(KeyEvent.VK_V)
    robot.keyRelease(KeyEvent.VK_V)
    robot.keyRelease(ctrlKey)
}

fun setClipboardContent(content: String) {
    val stringSelection = StringSelection(content)
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(stringSelection, null)
}