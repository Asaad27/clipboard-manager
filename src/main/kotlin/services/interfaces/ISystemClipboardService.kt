package services.interfaces

import model.ClipboardModel

interface ISystemClipboardService {
    fun getCurrentContent(): ClipboardModel?
    fun setCurrentContent(content: ClipboardModel)
}