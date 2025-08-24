package com.oneread


import com.oneread.db.DatabaseFactory
import com.oneread.routes.secretRoutes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.decodeFromString


@Serializable data class CreateSecretRequest(val secret: String, val ttlSeconds: Long? = 60)
@Serializable data class CreateSecretResponse(val token: String)
@Serializable data class GetSecretResponse(val secret: String)


class SecretRoutesTest {
@Test
fun createAndConsume() = testApplication {
val cfg = Config(defaultTtlSeconds = 60)
val db = DatabaseFactory(cfg)
db.connect()


application {
install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
routing { secretRoutes(cfg, db) }
}


val created = client.post("/secret") {
contentType(ContentType.Application.Json)
setBody(Json.encodeToString(CreateSecretRequest("swordfish")))
}.also { assertEquals(HttpStatusCode.Created, it.status) }
.bodyAsText()
val token = Json.decodeFromString<CreateSecretResponse>(created).token


val got = client.get("/secret/$token")
assertEquals(HttpStatusCode.OK, got.status)
val body = Json.decodeFromString<GetSecretResponse>(got.bodyAsText())
assertEquals("swordfish", body.secret)


// One-time: second fetch returns 404
val again = client.get("/secret/$token")
assertEquals(HttpStatusCode.NotFound, again.status)
}
}
