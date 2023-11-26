package repository

import adapter.extractText
import adapter.toPreview
import model.ClipboardModel
import repository.interfaces.ISystemClipboardRepository
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class WindowsSystemClipboardRepository : ISystemClipboardRepository {

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    override fun getCurrentContent(): ClipboardModel? {
        return try {
            val textContent = clipboard.extractText()
            if (textContent.isEmpty()) return null
            val preview = textContent.toPreview()
            ClipboardModel(preview = preview, fullContent = textContent)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun setCurrentContent(content: ClipboardModel) {
        val stringSelection = StringSelection(content.fullContent)
        clipboard.setContents(stringSelection, null)
    }
}