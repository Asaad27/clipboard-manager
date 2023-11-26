package model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ClipboardModel(
    val id: Int? = null,
    val preview: String = "",
    val fullContent: String = "",
    val lastUpdated: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
) {
    override fun equals(other: Any?): Boolean {
        return fullContent.equals((other as ClipboardModel).fullContent, ignoreCase = true)
    }

    override fun hashCode(): Int {
        return fullContent.hashCode()
    }
}
