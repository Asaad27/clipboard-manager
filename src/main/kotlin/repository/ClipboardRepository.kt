package repository

import adapter.toClipboardModel
import dao.IClipboardDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import model.ClipboardModel
import repository.interfaces.IClipboardRepository

class ClipboardRepository(private val clipboardDao: IClipboardDao) : IClipboardRepository {
    override suspend fun save(content: ClipboardModel): ClipboardModel {
        return clipboardDao.upsertContent(content).toClipboardModel()
    }

    override suspend fun getAll(): List<ClipboardModel> {
        return clipboardDao.getAllContents().map { it.toClipboardModel() }
    }

    override suspend fun getById(id: Int): ClipboardModel? {
        return clipboardDao.getContentById(id)?.toClipboardModel()
    }

    override suspend fun search(query: String): Flow<List<ClipboardModel>> {
        return clipboardDao.searchContents(query).map { entities -> entities.map { it.toClipboardModel() } }
    }
}
