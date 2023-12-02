package repository

import kotlinx.coroutines.flow.Flow
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

    override suspend fun search(query: String): Flow<ClipboardModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getClipboardContentByContent(fullContent: String): ClipboardModel? {
        TODO("Not yet implemented")
    }

}