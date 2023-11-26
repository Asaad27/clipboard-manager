package services.interfaces

import model.ClipboardModel

interface IClipboardService {
    suspend fun saveClipboardContent(content: ClipboardModel): ClipboardModel
    suspend fun getAllClipboardContents(): List<ClipboardModel>
    suspend fun searchClipboardContents(query: String): List<ClipboardModel>
    suspend fun getCurrentCopiedContent(): ClipboardModel?
}