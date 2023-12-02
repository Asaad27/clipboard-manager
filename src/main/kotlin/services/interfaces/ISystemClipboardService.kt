package services.interfaces

import kotlinx.coroutines.flow.Flow
import model.ClipboardModel

interface ISystemClipboardService {
    fun getCurrentContent(): ClipboardModel?
    fun setCurrentContent(content: ClipboardModel)
    fun clipboardFlow(): Flow<ClipboardModel>
}