package viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.ClipboardModel
import services.interfaces.IClipboardService
import services.interfaces.ISystemClipboardService

const val DELAY_TIME = 1000L

class ClipboardViewModel(
    private val systemClipboard: ISystemClipboardService,
    private val clipboardService: IClipboardService
) : ViewModel() {

    private val _clipboardContents = MutableStateFlow<List<ClipboardModel>>(listOf())
    val clipboardContents = _clipboardContents.asStateFlow()

    private var clipboardJob: Job? = null

    init {
        init(CoroutineScope(Dispatchers.IO + SupervisorJob()))

        clipboardJob = viewModelScope.launch {
            monitorClipboard()
        }

        viewModelScope.launch {
            clipboardService.getAllClipboardContents().let {
                _clipboardContents.value = it
            }
        }
    }

    private suspend fun monitorClipboard() {
        while (clipboardJob?.isActive == true) {
            try {
                val systemClipboardContent = systemClipboard.getCurrentContent() ?: continue
                val lastContent = _clipboardContents.value.lastOrNull() ?: ClipboardModel()
                if (systemClipboardContent != lastContent) {
                    viewModelScope.launch {
                        clipboardService.saveClipboardContent(systemClipboardContent).also {
                            println("saved: $it")
                            _clipboardContents.value += it
                        }
                    }
                }

                delay(DELAY_TIME)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun onCleared() {
        try {
            viewModelScope.cancel()
        } catch (e: UninitializedPropertyAccessException) {
            throw e
        }
    }

    fun onSearchClipboardContent(value: String) {
        viewModelScope.launch {
            clipboardService.searchClipboardContents(value).let {
                _clipboardContents.value = it
            }
        }
    }

    fun onItemClicked(item: ClipboardModel) {
        println("onItemClicked: $item")
        viewModelScope.launch {
            systemClipboard.setCurrentContent(item)
        }
    }
}