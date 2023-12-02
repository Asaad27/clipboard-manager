package viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.ClipboardModel
import org.slf4j.LoggerFactory
import services.interfaces.IClipboardService
import services.interfaces.ISystemClipboardService

class ClipboardViewModel(
    private val systemClipboard: ISystemClipboardService,
    private val clipboardService: IClipboardService
) : ViewModel() {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val _originalClipboardContents = MutableStateFlow<List<ClipboardModel>>(listOf())
    private val _filteredClipboardContents = MutableStateFlow<List<ClipboardModel>>(listOf())
    val clipboardContents = _filteredClipboardContents.asStateFlow()

    private var isSearching: Boolean = false
    private var searchJob: Job? = null

    init {
        init(CoroutineScope(Dispatchers.IO + SupervisorJob()))

        viewModelScope.launch {
            monitorClipboard()
        }

        viewModelScope.launch {
            clipboardService.getAllClipboardContents().let {
                _originalClipboardContents.value = it
                _filteredClipboardContents.value = it.toList()
            }
        }
    }

    private suspend fun monitorClipboard() {
        systemClipboard.clipboardFlow().collect { systemClipboardContent ->
            val content =
                clipboardService.getByContent(systemClipboardContent.fullContent) ?: systemClipboardContent

            clipboardService.saveClipboardContent(content).also {
                logger.debug("saved: {}", it)
                _originalClipboardContents.value += it
                if (!isSearching) {
                    _filteredClipboardContents.value += it
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
        _filteredClipboardContents.value = listOf()
        if (value.isEmpty()) {
            _filteredClipboardContents.value = _originalClipboardContents.value.toList()
        } else {
            searchJob = viewModelScope.launch {
                clipboardService.searchClipboardContents(value).collect {
                    _filteredClipboardContents.value += it
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