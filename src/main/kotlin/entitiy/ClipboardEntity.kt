package entitiy

import database.ClipboardTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ClipboardEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ClipboardEntity>(ClipboardTable)

    var content by ClipboardTable.content
    var createdAt by ClipboardTable.createdAt
    var updatedAt by ClipboardTable.updatedAt
}