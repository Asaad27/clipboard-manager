package repository.interfaces

import kotlinx.coroutines.flow.Flow
import model.ClipboardModel

interface ISystemClipboardRepository {
    fun getCurrentContent(): ClipboardModel?
    fun setCurrentContent(content: ClipboardModel)
    fun clipboardFlow(): Flow<ClipboardModel>
}