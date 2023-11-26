package repository.interfaces

import model.ClipboardModel

interface IClipboardRepository {
    suspend fun save(content: ClipboardModel)
    suspend fun getAll(): List<ClipboardModel>
    suspend fun getById(id: Int): ClipboardModel?
}