package com.oneread.routes


import com.oneread.Config
import com.oneread.db.DatabaseFactory
import com.oneread.crypto.Crypto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.UUID
import kotlinx.serialization.Serializable


@Serializable
data class CreateSecretRequest(
    val secret: String,
    val ttlSeconds: Long? = null,
    val contentType: String? = null,
)


@Serializable
data class CreateSecretResponse(val token: String, val expiresAt: Instant)


@Serializable
data class GetSecretResponse(val secret: String, val contentType: String)


fun Route.secretRoutes(cfg: Config, db: DatabaseFactory) {
    val aead = Crypto.loadOrCreateAead(cfg.keysetPath)
    
    
    post("/secret") {
        val req = call.receive<CreateSecretRequest>()
        val bytes = req.secret.toByteArray(StandardCharsets.UTF_8)
        if (bytes.size > cfg.maxSecretBytes) {
            return@post call.respond(
                HttpStatusCode.PayloadTooLarge,
                mapOf("error" to "secret too large (>${cfg.maxSecretBytes} bytes)"),
            )
        }
        val ttl = req.ttlSeconds ?: cfg.defaultTtlSeconds
        val aad = ByteArray(0) // optional associated data; can add later
        val sealed = aead.encrypt(bytes, aad)
        val (id, expires) = db.saveSecret(sealed, req.contentType ?: "text/plain", ttl)
        call.respond(HttpStatusCode.Created, CreateSecretResponse(id.toString(), expires))
    }
    
    
    get("/secret/{token}") {
        val token = call.parameters["token"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val id = try { UUID.fromString(token) } catch (_: IllegalArgumentException) {
            return@get call.respond(HttpStatusCode.BadRequest)
        }
        val pair = db.consumeSecret(id) ?: return@get call.respond(HttpStatusCode.NotFound)
        val (cipher, type) = pair
        val plaintext = try {
            Crypto.loadOrCreateAead(cfg.keysetPath).decrypt(cipher, ByteArray(0))
        } catch (e: Exception) {
            return@get call.respond(HttpStatusCode.InternalServerError)
        }
        call.respond(GetSecretResponse(String(plaintext, StandardCharsets.UTF_8), type))
    }
}
