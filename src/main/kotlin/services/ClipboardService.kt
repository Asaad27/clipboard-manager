package services

import kotlinx.coroutines.flow.Flow
import model.ClipboardModel
import repository.interfaces.IClipboardRepository
import services.interfaces.IClipboardService

class ClipboardService(
    private val clipboardRepository: IClipboardRepository
) : IClipboardService {
    override suspend fun saveClipboardContent(content: ClipboardModel): ClipboardModel {
        return clipboardRepository.save(content)
    }

    override suspend fun getAllClipboardContents(): List<ClipboardModel> {
        return clipboardRepository.getAll()
    }

    override suspend fun searchClipboardContents(query: String): Flow<List<ClipboardModel>> {
        return clipboardRepository.search(query)
    }

    override suspend fun getCurrentCopiedContent(): ClipboardModel? {
        throw NotImplementedError()
    }

    override suspend fun getClipboardContentById(id: Int): ClipboardModel? {
        return clipboardRepository.getById(id)
    }
}