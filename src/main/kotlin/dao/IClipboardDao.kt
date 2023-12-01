package dao

import entitiy.ClipboardEntity
import kotlinx.coroutines.flow.Flow
import model.ClipboardModel

interface IClipboardDao {
    fun upsertContent(model: ClipboardModel): ClipboardEntity
    fun getAllContents(): List<ClipboardEntity>
    fun getContentById(id: Int): ClipboardEntity?
    fun searchContents(query: String): Flow<List<ClipboardEntity>>
}