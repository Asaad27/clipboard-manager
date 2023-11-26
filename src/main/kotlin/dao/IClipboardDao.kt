package dao

import entitiy.ClipboardEntity
import model.ClipboardModel

interface IClipboardDao {
    fun upsertContent(model: ClipboardModel): ClipboardEntity
    fun getAllContents(): List<ClipboardEntity>
    fun getContentById(id: Int): ClipboardEntity?
}