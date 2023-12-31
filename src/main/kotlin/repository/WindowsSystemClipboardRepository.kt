package repository

import adapter.extractText
import adapter.toPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import model.ClipboardModel
import repository.interfaces.ISystemClipboardRepository
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable

class WindowsSystemClipboardRepository : ClipboardOwner, ISystemClipboardRepository {

    private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    private val clipboardChannel = Channel<ClipboardModel>(Channel.CONFLATED)

    init {
        takeOwnership()
    }

    override fun clipboardFlow(): Flow<ClipboardModel> = flow {
        for (content in clipboardChannel) {
            emit(content)
        }
    }

    override fun lostOwnership(clipboard: Clipboard, contents: Transferable) {
        takeOwnership()
        val newContent = getCurrentContent()
        newContent?.let { clipboardChannel.trySend(it) }
    }

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
        clipboard.setContents(stringSelection, this)
    }

    private fun takeOwnership() {
        repeat(3) {
            try {
                val transferable = clipboard.getContents(this)
                clipboard.setContents(transferable, this)
                return
            } catch (e: IllegalStateException) {
                Thread.sleep(500)
            }
        }
    }
}