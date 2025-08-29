package com.oneread


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable data class CreateSecretRequest(val secret: String, val ttlSeconds: Long? = null)
@Serializable data class CreateSecretResponse(val token: String, val expiresAt: String)
@Serializable data class GetSecretResponse(val secret: String, val contentType: String)


suspend fun put(host: String, text: String, ttl: Long?): String {
    val client = HttpClient(CIO) { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
    client.use {
        val resp: CreateSecretResponse = it.post("$host/secret") {
            contentType(ContentType.Application.Json)
            setBody(CreateSecretRequest(text, ttl))
        }.body()
        return resp.token
    }
}


suspend fun get(host: String, token: String): String {
    val client = HttpClient(CIO) { install(ContentNegotiation) { json() } }
    client.use { return it.get("$host/secret/$token").body<GetSecretResponse>().secret }
}


suspend fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: client put <HOST> <FILE|-> [ttlSeconds] | client get <HOST> <TOKEN>")
        return
    }
    when (args[0]) {
        "put" -> {
        val host = args.getOrNull(1) ?: error("missing host, e.g. http://localhost:8080")
        val path = args.getOrNull(2) ?: "-"
        val ttl = args.getOrNull(3)?.toLong()
        val data = if (path == "-") {
            generateSequence(::readLine).joinToString("\n")
        } else {
            Files.readString(Path.of(path))
        }
        val token = put(host, data, ttl)
        println(token)
        }
        "get" -> {
            val host = args.getOrNull(1) ?: error("missing host")
            val token = args.getOrNull(2) ?: error("missing token")
            println(get(host, token))
        }
        else -> println("unknown command: ${args[0]}")
    }
}
