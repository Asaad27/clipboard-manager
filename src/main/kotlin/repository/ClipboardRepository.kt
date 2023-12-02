package repository

import adapter.toClipboardModel
import cache.Cache
import cache.strategy.LruStrategy
import database.ClipboardTable
import database.DriverManager
import entitiy.ClipboardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import model.ClipboardModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import repository.interfaces.IClipboardRepository

class ClipboardRepository(
    driverManager: DriverManager
) : IClipboardRepository {

    private val cache: Cache<Long, ClipboardEntity>

    init {
        driverManager.connect()
        createSchemaIfNotExist()
        cache = Cache(LruStrategy())
    }

    override suspend fun save(content: ClipboardModel): ClipboardModel {
        val contentHash = content.fullContent.hashCode().toLong()
        val existingElement = cache.get(contentHash)

        return transaction {
            //addLogger(StdOutSqlLogger)
            existingElement?.let {
                it.updatedAt = content.lastUpdated
                return@transaction it
            }

            ClipboardEntity.new {
                this.content = content.fullContent
                createdAt = content.lastUpdated
                updatedAt = content.lastUpdated

            }
        }.also { cache.put(contentHash, it) }.toClipboardModel()
    }

    override suspend fun getAll(): List<ClipboardModel> {
        return transaction {
            ClipboardTable.selectAll()
                .distinct()
                .map {
                    ClipboardEntity.wrapRow(it)
                }
        }.map { it.toClipboardModel() }
    }

    override suspend fun search(query: String): Flow<ClipboardModel> = channelFlow {
        newSuspendedTransaction(Dispatchers.IO) {
            ClipboardTable
                .select { ClipboardTable.content like "%$query%" }
                .forEach { row ->
                    val clipboardEntity = ClipboardEntity.wrapRow(row)
                    send(clipboardEntity.toClipboardModel())
                }
        }
    }

    override suspend fun getClipboardContentByContent(fullContent: String): ClipboardModel? {
        val contentHash = fullContent.hashCode().toLong()
        val existingElement = cache.get(contentHash)
        existingElement?.let {
            return it.toClipboardModel()
        }
        return transaction {
            ClipboardTable
                .select { ClipboardTable.content eq fullContent }
                .map { ClipboardEntity.wrapRow(it) }
                .firstOrNull()
        }?.toClipboardModel()
    }

    private fun createSchemaIfNotExist() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.createMissingTablesAndColumns(*arrayOf(ClipboardTable))
        }
    }
}
