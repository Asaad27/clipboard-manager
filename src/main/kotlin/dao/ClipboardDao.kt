package dao

import database.ClipboardTable
import database.DriverManager
import entitiy.ClipboardEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.ClipboardModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ClipboardDao(driverManager: DriverManager) : IClipboardDao {

    init {
        driverManager.connect()
        createSchemaIfNotExist()
    }

    override fun upsertContent(model: ClipboardModel) {
        transaction {
            addLogger(StdOutSqlLogger)

            ClipboardEntity.new {
                content = model.fullContent
                createdAt = if (model.id == null) {
                    Clock.System.now().toLocalDateTime(TimeZone.UTC)
                } else {
                    model.lastUpdated
                }
                updatedAt = model.lastUpdated
            }
        }
    }

    override fun getAllContents(): List<ClipboardEntity> {
        return transaction {
            ClipboardTable.selectAll().distinct().map {
                ClipboardEntity.wrapRow(it)
            }
        }
    }

    override fun getContentById(id: Int): ClipboardEntity? {
        return transaction {
            ClipboardTable.select { ClipboardTable.id eq id }.map {
                ClipboardEntity.wrapRow(it)
            }.firstOrNull()
        }
    }

    private fun createSchemaIfNotExist() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.createMissingTablesAndColumns(*arrayOf(ClipboardTable))
        }
    }
}