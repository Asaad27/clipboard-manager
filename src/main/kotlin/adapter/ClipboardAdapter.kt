package adapter

import entitiy.ClipboardEntity
import model.ClipboardModel

const val PREVIEW_LENGTH = 50
const val NUMBER_OF_PREVIEW_LINES = 5

fun String.toPreview(): String {
    return this.trimMargin()
        .lines().map { it.trim() }
        .filter { it.isNotEmpty() }.take(NUMBER_OF_PREVIEW_LINES)
        .joinToString(separator = "\n") {
            if (it.length > PREVIEW_LENGTH) {
                it.substring(0, PREVIEW_LENGTH) + "..."
            } else {
                it
            }
        }
}

fun ClipboardEntity.toClipboardModel(): ClipboardModel {
    val entity = this
    return ClipboardModel(
        id = this.id.value,
        preview = entity.content.toPreview(),
        fullContent = entity.content,
    )
}


