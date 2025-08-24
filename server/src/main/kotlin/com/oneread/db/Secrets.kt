package com.oneread.db


import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp


object Secrets : Table("secrets") {
val id = uuid("id").uniqueIndex()
val blob = blob("blob")
val contentType = varchar("content_type", length = 100).default("text/plain")
val createdAt = timestamp("created_at")
val expiresAt = timestamp("expires_at")
override val primaryKey = PrimaryKey(id)
}
