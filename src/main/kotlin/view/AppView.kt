import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import view.AppColors
import viewmodel.ClipboardViewModel

private const val DEBOUNCE_TIME = 300L

@Composable
@Preview
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
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Black)
                        ) {
                            itemsIndexed(clipboardItemState.value) { index, item ->
                                val isLastInClipboard = copiedItemIndexState.value == index
                                val isFocused = focusedIndexState.value == index
                                ClipboardItem(item, isLastInClipboard, isFocused, onItemClicked = {
                                    viewModel.onItemClicked(it)
                                })
                            }
                        }
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

@OptIn(ExperimentalComposeUiApi::class)
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
