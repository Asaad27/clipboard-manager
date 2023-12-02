package viewmodel.preview

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import model.ClipboardModel
import services.interfaces.IClipboardService
import services.interfaces.ISystemClipboardService
import viewmodel.ClipboardViewModel

class FakeClipboardViewModel : ClipboardViewModel(
    FakeSystemClipboardService(),
    FakeClipboardService()
) {

    init {

        originalClipboardContents.value = fakeClipboardContents
        filteredClipboardContents.value = fakeClipboardContents
    }

    class FakeClipboardService : IClipboardService {
        override suspend fun saveClipboardContent(content: ClipboardModel): ClipboardModel {
            TODO("Not yet implemented")
        }

        override suspend fun getAllClipboardContents(): List<ClipboardModel> {
            TODO("Not yet implemented")
        }

        override suspend fun searchClipboardContents(query: String): Flow<ClipboardModel> {
            TODO("Not yet implemented")
        }

        override suspend fun getByContent(fullContent: String): ClipboardModel? {
            TODO("Not yet implemented")
        }

    }

    class FakeSystemClipboardService : ISystemClipboardService {
        override fun getCurrentContent(): ClipboardModel? {
            TODO("Not yet implemented")
        }

        override fun setCurrentContent(content: ClipboardModel) {
            TODO("Not yet implemented")
        }

        override fun clipboardFlow(): Flow<ClipboardModel> {
            TODO("Not yet implemented")
        }

    }

    companion object {
        val fakeClipboardContents = listOf(
            ClipboardModel(
                fullContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec euismod, nisl eget lacinia lacinia, nisl nisl lacinia nisl, nec lacinia nisl nisl nec nisl. Donec euismod, nisl eget lacinia lacinia, nisl nisl lacinia nisl, nec lacinia nisl nisl nec nisl. Donec euismod, nisl eget lacinia lacinia, nisl nisl lacinia nisl, nec lacinia nisl nisl nec nisl. Donec euismod, nisl eget lacinia lacinia, nisl nisl lacinia nisl, nec lacinia nisl nisl nec nisl. Donec euismod, nisl eget lacinia lacinia, nisl nisl lacinia nisl, nec lacinia nisl nisl nec nisl. Donec euismod, nisl eget lacinia lacinia, nisl nisl lacinia nisl, nec lacinia nisl nisl nec nisl.",
                preview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I once farted in an elevator, it was wrong on so many levels.",
                preview = "I once farted in an elevator, it was wrong on so many levels.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I told my wife she should embrace her mistakes. She gave me a hug",
                preview = "I told my wife she should embrace her mistakes. She gave me a hug",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "Parallel lines have so much in common. It’s a shame they’ll never meet.",
                preview = "Parallel lines have so much in common. It’s a shame they’ll never meet.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "My wife accused me of being immature. I told her to get out of my fort.",
                preview = "My wife accused me of being immature. I told her to get out of my fort.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I don’t trust stairs. They’re always up to something.",
                preview = "I don’t trust stairs. They’re always up to something.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I used to be addicted to soap, but I’m clean now.",
                preview = "I used to be addicted to soap, but I’m clean now.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I’m reading a book about anti-gravity. It’s impossible to put down!",
                preview = "I’m reading a book about anti-gravity. It’s impossible to put down!",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I wasn’t originally going to get a brain transplant, but then I changed my mind.",
                preview = "I wasn’t originally going to get a brain transplant, but then I changed my mind.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I’d tell you a chemistry joke but I know I wouldn’t get a reaction.",
                preview = "I’d tell you a chemistry joke but I know I wouldn’t get a reaction.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "Have you ever tried to eat a clock? It’s very time consuming.",
                preview = "Have you ever tried to eat a clock? It’s very time consuming.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I wondered why the baseball was getting bigger. Then it hit me.",
                preview = "I wondered why the baseball was getting bigger. Then it hit me.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I used to have a fear of hurdles, but I got over it.",
                preview = "I used to have a fear of hurdles, but I got over it.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            ),
            ClipboardModel(
                fullContent = "I used to be a banker, but I lost interest.",
                preview = "I used to be a banker, but I lost interest.",
                lastUpdated = LocalDateTime.parse("2021-01-01T00:00:00")
            )
        )
    }
}