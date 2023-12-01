package repository.interfaces

import kotlinx.coroutines.flow.Flow
import model.ClipboardModel

interface IClipboardRepository {
    suspend fun save(content: ClipboardModel): ClipboardModel
    suspend fun getAll(): List<ClipboardModel>
    suspend fun getById(id: Int): ClipboardModel?
    suspend fun search(query: String): Flow<List<ClipboardModel>>
}