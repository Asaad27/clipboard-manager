package services

import model.ClipboardModel
import repository.interfaces.ISystemClipboardRepository
import services.interfaces.ISystemClipboardService

class WindowsSystemClipboardService(private val systemClipboardRepository: ISystemClipboardRepository) :
    ISystemClipboardService {
    override fun getCurrentContent(): ClipboardModel? {
        return systemClipboardRepository.getCurrentContent()
    }

    override fun setCurrentContent(content: ClipboardModel) {
        systemClipboardRepository.setCurrentContent(content)
    }
}