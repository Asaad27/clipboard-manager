import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import di.appModule
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import view.App
import viewmodel.ClipboardViewModel

fun main() = application {
    startKoin {
        modules(appModule)
    }

    val viewModel: ClipboardViewModel by inject(ClipboardViewModel::class.java)
    val shouldMinimize by viewModel.shouldMinimize.collectAsState()
    val windowState = rememberWindowState(width = 400.dp, height = 1000.dp)

    val icon = painterResource("icons/sharingan.png")
    Window(
        title = "Sharingan",
        icon = icon,
        visible = true,
        alwaysOnTop = true,
        state = windowState,
        onCloseRequest = {
            viewModel.onCleared()
            exitApplication()
        }
    ) {
        if (shouldMinimize) {
            windowState.isMinimized = true
            viewModel.onWindowMinimized()
        }

        App(viewModel)
    }
}