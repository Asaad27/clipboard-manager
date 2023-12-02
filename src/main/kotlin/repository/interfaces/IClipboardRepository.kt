package repository.interfaces

import kotlinx.coroutines.flow.Flow
import model.ClipboardModel

interface IClipboardRepository {
    suspend fun save(content: ClipboardModel): ClipboardModel
    suspend fun getAll(): List<ClipboardModel>
    suspend fun search(query: String): Flow<ClipboardModel>
    suspend fun getClipboardContentByContent(fullContent: String): ClipboardModel?
}