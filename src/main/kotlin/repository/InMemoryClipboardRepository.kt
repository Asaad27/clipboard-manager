package repository

import model.ClipboardModel
import repository.interfaces.IClipboardRepository

class InMemoryClipboardRepository : IClipboardRepository {
    private val clipboardHistory = mutableListOf<ClipboardModel>()

    override suspend fun save(content: ClipboardModel): ClipboardModel {
        clipboardHistory.add(content)
        return content
    }

    override suspend fun getAll(): List<ClipboardModel> {
        return clipboardHistory
    }

    override suspend fun getById(id: Int): ClipboardModel? {
        return clipboardHistory.firstOrNull { it.id == id }
    }
}