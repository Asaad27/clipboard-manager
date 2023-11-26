package database

import org.jetbrains.exposed.sql.Database
import java.util.*

class DriverManager() {
    private val uri: String by lazy {
        loadDatabaseUrl()
    }

    private val driver: String by lazy {
        loadDatabaseDriver()
    }

    private val user by lazy {
        loadUser()
    }

    private val password by lazy {
        loadPassword()
    }

    private val properties by lazy {
        Properties()
    }

    fun connect() {
        Database.connect(uri, driver = driver, user = user, password = password)
    }

    private fun loadPassword(): String {
        return getResource("db.password", "")
    }

    private fun loadUser(): String {
        return getResource("db.user", "root")
    }

    private fun loadDatabaseUrl(): String {
        return getResource("db.url", "jdbc:sqlite::memory:clipboard")
    }

    private fun loadDatabaseDriver(): String {
        return getResource("db.driver", "org.sqlite.JDBC")
    }

    private fun getResource(key: String, defaultValue: String? = null): String {
        javaClass.classLoader.getResourceAsStream("dbconfig.properties").use { inputStream ->
            if (inputStream == null) {
                throw IllegalStateException("Missing 'dbconfig.properties'")
            }
            properties.load(inputStream)
        }
        return properties.getProperty(key, defaultValue)
    }
}
