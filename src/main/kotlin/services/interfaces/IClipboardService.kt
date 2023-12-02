package services.interfaces

import kotlinx.coroutines.flow.Flow
import model.ClipboardModel

interface IClipboardService {
    suspend fun saveClipboardContent(content: ClipboardModel): ClipboardModel
    suspend fun getAllClipboardContents(): List<ClipboardModel>
    suspend fun searchClipboardContents(query: String): Flow<ClipboardModel>
    suspend fun getByContent(fullContent: String): ClipboardModel?
}