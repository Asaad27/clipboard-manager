package services

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

    override suspend fun searchClipboardContents(query: String): List<ClipboardModel> {
        return clipboardRepository.getAll().filter {
            it.fullContent.contains(query, ignoreCase = true)
        }
    }

    override suspend fun getCurrentCopiedContent(): ClipboardModel? {
        return clipboardRepository.getAll().lastOrNull()
    }
}