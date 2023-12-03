package view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ClipboardModel
import org.slf4j.LoggerFactory
import viewmodel.ClipboardViewModel
import viewmodel.preview.FakeClipboardViewModel

private const val DEBOUNCE_TIME = 300L
private val logger = LoggerFactory.getLogger("AppView")

@Preview
@Composable
fun AppPreview() {
    val viewModel = FakeClipboardViewModel()
    App(viewModel)
}

@Preview
@Composable
fun ClipboardItemStandaloneFocusedPreview() {
    val item = ClipboardModel(
        id = 1,
        preview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        fullContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    )
    ClipboardItem(item, isLastInClipboard = false, isFocused = true, onItemClicked = {})
}

@Preview
@Composable
fun ClipboardItemListContextualWithFocusedElementPreview() {
    val clipboardItems = FakeClipboardViewModel.fakeClipboardContents
    val clipboardItemsState = remember { mutableStateOf(clipboardItems) }
    val focusedIndexState = remember { mutableStateOf<Int?>(FakeClipboardViewModel.fakeClipboardContents.size - 1) }
    val copiedItemIndexState = remember { mutableStateOf<Int?>(null) }
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusable()
                    .weight(1f)
            ) {
                ClipboardList(
                    clipboardItems = clipboardItemsState,
                    copiedItemIndex = copiedItemIndexState,
                    focusedItemIndex = focusedIndexState,
                    lazyListState = lazyListState
                ) {}
            }
        }
    }
}

@Preview
@Composable
fun ClipboardCopiedItemBoxPreview() {
    val scrollState = rememberScrollState()
    val longText = "a".repeat(100000)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .verticalScroll(scrollState)
            .background(Color.Black)

    ) {
        Text(longText, modifier = Modifier.padding(8.dp), color = Color.LightGray)
    }
}

@Composable
fun App(viewModel: ClipboardViewModel) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val clipboardItemState = viewModel.clipboardContents.collectAsState()
    val copiedItemIndexState = remember { mutableStateOf<Int?>(null) }
    val focusedIndexState = remember { mutableStateOf<Int?>(null) }
    val focusRequester = remember { FocusRequester() }
    val searchTextState = remember { mutableStateOf("") }


    LaunchedEffect(focusedIndexState.value) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(clipboardItemState.value, focusedIndexState.value, copiedItemIndexState.value) {
        scope.launch {
            if (clipboardItemState.value.isNotEmpty()) {
                val elementToScrollTo = focusedIndexState.value ?: (clipboardItemState.value.size - 1)
                lazyListState.animateScrollToItem(elementToScrollTo)
                copiedItemIndexState.value = maxOf(clipboardItemState.value.size - 1, 0)
            }
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp).border(1.dp, Color.Black)) {
                Column {
                    Box(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .focusable()
                            .weight(1f)
                            .onKeyEvent { onKeyEvent(it, focusedIndexState, clipboardItemState, viewModel) }
                    ) {
                        ClipboardList(
                            clipboardItems = clipboardItemState,
                            copiedItemIndex = copiedItemIndexState,
                            focusedItemIndex = focusedIndexState,
                            lazyListState = lazyListState
                        ) { viewModel.onItemClicked(item = it) }
                    }

                    clipboardItemState.value.getOrNull(copiedItemIndexState.value ?: 0)?.let {
                        ClipboardCopiedItemBox(it)
                    }

                    SearchBar(searchTextState) {
                        viewModel.onSearchClipboardContent(searchTextState.value)
                    }
                }
            }
        }
    }
}

@Composable
fun ClipboardList(
    clipboardItems: State<List<ClipboardModel>>,
    copiedItemIndex: MutableState<Int?>,
    focusedItemIndex: MutableState<Int?>,
    lazyListState: LazyListState,
    onItemClicked: (ClipboardModel) -> Unit
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black)
    ) {
        itemsIndexed(clipboardItems.value) { index, item ->
            val isLastInClipboard = copiedItemIndex.value == index
            val isFocused = focusedItemIndex.value == index
            ClipboardItem(item, isLastInClipboard, isFocused, onItemClicked = onItemClicked)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClipboardItem(
    item: ClipboardModel,
    isLastInClipboard: Boolean,
    isFocused: Boolean,
    onItemClicked: (ClipboardModel) -> Unit
) {
    val (isHovered, setHovered) = remember { mutableStateOf(false) }
    val (isPressed, setPressed) = remember { mutableStateOf(false) }
    val backgroundColor = when {
        isPressed -> AppColors.LightBloodyRed
        isHovered || isFocused -> AppColors.LightPurple
        else -> Color.Transparent
    }
    val borderColor = when {
        isLastInClipboard -> AppColors.DarkPurple
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, borderColor)
            .background(backgroundColor)
            .clickable { onItemClicked(item) }
            .pointerHoverIcon(PointerIcon.Hand)
            .onPointerEvent(PointerEventType.Press) {
                setPressed(true)
            }
            .onPointerEvent(PointerEventType.Release) {
                setPressed(false)
            }
            .onPointerEvent(PointerEventType.Enter) {
                setHovered(true)
            }
            .onPointerEvent(PointerEventType.Exit) {
                setHovered(false)
            }
    ) {
        Text(item.preview, modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun ClipboardCopiedItemBox(item: ClipboardModel) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .verticalScroll(scrollState)
            .padding(4.dp)
            .border(1.dp, Color.Green)
            .background(Color.Black)

    ) {
        Text(item.fullContent, modifier = Modifier.padding(8.dp), color = Color.White)
    }
}


@Composable
fun SearchBar(searchTextState: MutableState<String>, onSearchTextChanged: (String) -> Unit) {
    val scope = rememberCoroutineScope()

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = searchTextState.value,
            label = { Text("Search") },
            onValueChange = {
                searchTextState.value = it
            },
            modifier = Modifier.weight(1f).padding(8.dp),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
        )
    }

    LaunchedEffect(searchTextState.value) {
        scope.launch {
            delay(timeMillis = DEBOUNCE_TIME)
            onSearchTextChanged(searchTextState.value)
        }
    }
}

fun onKeyEvent(
    keyEvent: KeyEvent,
    focusedIndex: MutableState<Int?>,
    clipboardItems: State<List<ClipboardModel>>,
    viewModel: ClipboardViewModel
): Boolean {
    if (keyEvent.type != KeyEventType.KeyDown) {
        return false
    }

    val clipboardSize = clipboardItems.value.size
    return when (keyEvent.key) {
        Key.DirectionDown -> {
            focusedIndex.value = minOf(((focusedIndex.value ?: -1) + 1), clipboardSize - 1)
            true
        }

        Key.DirectionUp -> {
            focusedIndex.value = maxOf((focusedIndex.value ?: clipboardSize) - 1, 0)
            true
        }

        Key.Enter -> {
            focusedIndex.value?.let {
                viewModel.onItemClicked(clipboardItems.value[it])
            }
            true
        }

        else -> false
    }
}
