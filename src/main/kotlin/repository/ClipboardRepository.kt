package repository

import adapter.toClipboardModel
import dao.IClipboardDao
import model.ClipboardModel
import repository.interfaces.IClipboardRepository

class ClipboardRepository(private val clipboardDao: IClipboardDao) : IClipboardRepository {


    override suspend fun save(content: ClipboardModel) {
        clipboardDao.upsertContent(content)
    }

    override suspend fun getAll(): List<ClipboardModel> {
        return clipboardDao.getAllContents().map { it.toClipboardModel() }
    }

    override suspend fun getById(id: Int): ClipboardModel? {
        return clipboardDao.getContentById(id)?.toClipboardModel()
    }
}
