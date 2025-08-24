package com.oneread


import com.oneread.db.DatabaseFactory
import com.oneread.routes.secretRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


fun main() = EngineMain.main(emptyArray())


@Suppress("unused") // referenced in application.conf if using HOCON; here we start programmatically
fun Application.module() {
val cfg = Config()
val db = DatabaseFactory(cfg)
db.connect()


install(CallLogging)
install(StatusPages) {
exception<Throwable> { call, cause ->
// Avoid leaking secrets in logs; log minimal
environment.log.error("Unhandled: ${cause.javaClass.simpleName}")
call.respondText("internal error", status = io.ktor.http.HttpStatusCode.InternalServerError)
}
}
install(ContentNegotiation) {
json(Json { ignoreUnknownKeys = true; explicitNulls = false })
}


routing {
get("/health") { call.respond(mapOf("status" to "ok")) }
secretRoutes(cfg, db)
}


// Background TTL cleanup
val scope = CoroutineScope(Dispatchers.Default)
scope.launch {
while (isActive) {
try {
val deleted = db.cleanupExpired()
if (deleted > 0) log.info("cleanup: deleted $deleted expired secrets")
} catch (t: Throwable) {
log.warn("cleanup failed: ${t.javaClass.simpleName}")
}
delay(Duration.ofSeconds(cfg.cleanupIntervalSeconds))
}
}
}
