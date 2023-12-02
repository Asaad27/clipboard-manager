package services

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.ClipboardModel
import repository.interfaces.IClipboardRepository
import services.interfaces.IClipboardService

class ClipboardService(
    private val clipboardRepository: IClipboardRepository
) : IClipboardService {
    override suspend fun saveClipboardContent(content: ClipboardModel): ClipboardModel {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        content.lastUpdated = now
        return clipboardRepository.save(content)
    }

    override suspend fun getAllClipboardContents(): List<ClipboardModel> {
        return clipboardRepository.getAll()
    }

    override suspend fun searchClipboardContents(query: String): Flow<ClipboardModel> {
        return clipboardRepository.search(query)
    }

    override suspend fun getByContent(fullContent: String): ClipboardModel? {
        return clipboardRepository.getClipboardContentByContent(fullContent)
    }
}