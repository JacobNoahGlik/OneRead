package com.oneread


import java.nio.file.Path


data class Config(
    val port: Int = System.getenv("SECRET_DROP_PORT")?.toIntOrNull() ?: 8080,
    val dataDir: Path = Path.of(System.getenv("SECRET_DROP_DATA_DIR") ?: "server"),
    val dbUrl: String = System.getenv("SECRET_DROP_DB_URL")
    ?: "jdbc:sqlite:${dataDir.resolve("oneread.db").toAbsolutePath()}",
    val dbDriver: String = System.getenv("SECRET_DROP_DB_DRIVER") ?: "org.sqlite.JDBC",
    val dbUser: String? = System.getenv("SECRET_DROP_DB_USER"),
    val dbPassword: String? = System.getenv("SECRET_DROP_DB_PASSWORD"),
    val keysetPath: Path = Path.of(System.getenv("SECRET_DROP_KEYSET_PATH") ?: "server/keyset.json"),
    val cleanupIntervalSeconds: Long = System.getenv("SECRET_DROP_CLEANUP_INTERVAL_SECONDS")?.toLongOrNull() ?: 60L,
    val defaultTtlSeconds: Long = System.getenv("SECRET_DROP_DEFAULT_TTL_SECONDS")?.toLongOrNull() ?: 300L,
    val maxSecretBytes: Int = System.getenv("SECRET_DROP_MAX_SECRET_BYTES")?.toIntOrNull() ?: 64 * 1024,
)
