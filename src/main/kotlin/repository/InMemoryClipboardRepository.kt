package repository

import model.ClipboardModel
import repository.interfaces.IClipboardRepository

class InMemoryClipboardRepository : IClipboardRepository {
    private val clipboardHistory = mutableListOf<ClipboardModel>()

    override suspend fun save(content: ClipboardModel) {
        clipboardHistory.add(content)
    }

    override suspend fun getAll(): List<ClipboardModel> {
        return clipboardHistory
    }

    override suspend fun getById(id: Int): ClipboardModel? {
        return clipboardHistory.firstOrNull { it.id == id }
    }
}