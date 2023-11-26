package repository.interfaces

import model.ClipboardModel

interface ISystemClipboardRepository {
    fun getCurrentContent(): ClipboardModel?
    fun setCurrentContent(content: ClipboardModel)
}