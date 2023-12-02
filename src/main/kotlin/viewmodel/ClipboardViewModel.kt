package viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.ClipboardModel
import org.slf4j.LoggerFactory
import services.interfaces.IClipboardService
import services.interfaces.ISystemClipboardService

open class ClipboardViewModel(
    private val systemClipboard: ISystemClipboardService,
    private val clipboardService: IClipboardService
) : ViewModel() {
    private val logger = LoggerFactory.getLogger(javaClass)

    protected val originalClipboardContents = MutableStateFlow<List<ClipboardModel>>(listOf())
    protected val filteredClipboardContents = MutableStateFlow<List<ClipboardModel>>(listOf())
    val clipboardContents = filteredClipboardContents.asStateFlow()

    private var isSearching: Boolean = false
    private var searchJob: Job? = null

    init {
        init(CoroutineScope(Dispatchers.IO + SupervisorJob()))

        viewModelScope.launch {
            monitorClipboard()
        }

        viewModelScope.launch {
            clipboardService.getAllClipboardContents().let {
                originalClipboardContents.value = it
                filteredClipboardContents.value = it.toList()
            }
        }
    }

    private suspend fun monitorClipboard() {
        systemClipboard.clipboardFlow().collect { systemClipboardContent ->
            val content =
                clipboardService.getByContent(systemClipboardContent.fullContent) ?: systemClipboardContent

            clipboardService.saveClipboardContent(content).also {
                logger.debug("saved: {}", it.preview)
                originalClipboardContents.value += it
                if (!isSearching) {
                    filteredClipboardContents.value += it
                    logger.debug("filteredClipboardContents.value: {}", filteredClipboardContents.value.size)
                    logger.debug("originalClipboardContents.value: {}", originalClipboardContents.value.size)
                    logger.debug("clipboardContents.value: {}", clipboardContents.value.size)
                }
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
        searchJob?.cancel()
        isSearching = value.isNotEmpty()
        filteredClipboardContents.value = listOf()
        if (value.isEmpty()) {
            filteredClipboardContents.value = originalClipboardContents.value.toList()
        } else {
            searchJob = viewModelScope.launch {
                clipboardService.searchClipboardContents(value).collect {
                    filteredClipboardContents.value += it
                }
            }
        }
    }

    fun onItemClicked(item: ClipboardModel) {
        logger.debug("onItemClicked: {}", item)
        viewModelScope.launch {
            systemClipboard.setCurrentContent(item)
        }
    }
}