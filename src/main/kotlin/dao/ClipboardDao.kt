package dao

import database.ClipboardTable
import database.ClipboardTable.content
import database.DriverManager
import entitiy.ClipboardEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override fun upsertContent(model: ClipboardModel): ClipboardEntity {
        return transaction {
            addLogger(StdOutSqlLogger)

            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            if (model.id != null) {
                ClipboardTable.update({ ClipboardTable.id eq model.id }) {
                    it[content] = model.fullContent
                    it[updatedAt] = now
                }
                ClipboardEntity[model.id]
            } else {
                ClipboardEntity.new {
                    content = model.fullContent
                    createdAt = now
                    updatedAt = now
                }
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

    override fun searchContents(query: String): Flow<List<ClipboardEntity>> {
        return flow {
            val queryResult = transaction {
                ClipboardTable.select { content like "%$query%" }.distinct().map {
                    ClipboardEntity.wrapRow(it)
                }
            }
            emit(queryResult)
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