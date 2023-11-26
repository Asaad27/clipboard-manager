package adapter

import java.awt.datatransfer.Clipboard


fun Clipboard.extractText(): String {
    return try {
        if (isDataFlavorAvailable(java.awt.datatransfer.DataFlavor.stringFlavor)) {
            getData(java.awt.datatransfer.DataFlavor.stringFlavor) as String
        } else {
            ""
        }
    } catch (e: Exception) {
        throw e
    }
}

