package com.oneread.db


import com.oneread.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.time.Instant
import java.util.UUID
import javax.sql.DataSource
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction


class DatabaseFactory(private val config: Config) {
fun connect(): Database {
val ds = hikari(config)
val db = Database.connect(ds)
transaction(db) { SchemaUtils.createMissingTablesAndColumns(Secrets) }
return db
}


fun saveSecret(
encrypted: ByteArray,
contentType: String,
ttlSeconds: Long,
): Pair<UUID, Instant> = transaction {
val id = UUID.randomUUID()
val now = Clock.System.now()
val expires = now.plus(ttlSeconds, kotlinx.datetime.DateTimeUnit.SECOND)
Secrets.insert {
it[Secrets.id] = id
it[blob] = java.io.ByteArrayInputStream(encrypted)
it[Secrets.contentType] = contentType
it[createdAt] = now.toJavaInstant()
it[expiresAt] = expires.toJavaInstant()
}
id to expires.toJavaInstant()
}


/** Returns plaintext + contentType, and deletes the row atomically. */
fun consumeSecret(id: UUID): Pair<ByteArray, String>? = transaction {
val row = Secrets.select { Secrets.id eq id }.singleOrNull() ?: return@transaction null
val now = Clock.System.now().toJavaInstant()
if (row[Secrets.expiresAt].isBefore(now)) {
// Expired — delete and return null
Secrets.deleteWhere { Secrets.id eq id }
return@transaction null
}
val bytes = row[Secrets.blob].binaryStream.readAllBytes()
val type = row[Secrets.contentType]
// One-time: delete on read
Secrets.deleteWhere { Secrets.id eq id }
bytes to type
}


fun cleanupExpired(): Int = transaction {
val now = Clock.System.now().toJavaInstant()
Secrets.deleteWhere { Secrets.expiresAt lessEq now }
}


private fun hikari(cfg: Config): DataSource {
val hc = HikariConfig().apply {
jdbcUrl = cfg.dbUrl
driverClassName = cfg.dbDriver
cfg.dbUser?.let { username = it }
cfg.dbPassword?.let { password = it }
maximumPoolSize = 5
isAutoCommit = false
transactionIsolation = "TRANSACTION_REPEATABLE_READ"
validate()
}
return HikariDataSource(hc)
}
}
