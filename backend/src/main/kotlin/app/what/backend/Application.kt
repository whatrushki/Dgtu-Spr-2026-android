package app.what.backend

import app.what.backend.auth.AuthRoutes
import app.what.backend.auth.InMemoryAuthSessionStore
import app.what.backend.auth.InMemoryPartnerUserStore
import app.what.backend.config.SberBackendConfig
import app.what.backend.sber.SberIdApiClient
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                explicitNulls = false
            }
        )
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val status = if (cause is IllegalArgumentException) {
                HttpStatusCode.BadRequest
            } else {
                HttpStatusCode.InternalServerError
            }

            call.respond(
                status = status,
                message = ApiErrorResponse(
                    code = status.value.toString(),
                    message = cause.message ?: "Unexpected server error"
                )
            )
        }
    }

    val config = SberBackendConfig.fromEnvironment()
    val authSessionStore = InMemoryAuthSessionStore(config.sessionTtlMinutes)
    val partnerUserStore = InMemoryPartnerUserStore()
    val sberIdApiClient = SberIdApiClient(config)

    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "ok",
                    stand = config.stand.name,
                    redirectUri = config.redirectUri
                )
            )
        }

        AuthRoutes(
            config = config,
            authSessionStore = authSessionStore,
            partnerUserStore = partnerUserStore,
            sberIdApiClient = sberIdApiClient
        ).register(this)
    }
}

@Serializable
data class HealthResponse(
    val status: String,
    val stand: String,
    val redirectUri: String
)

@Serializable
data class ApiErrorResponse(
    val code: String,
    val message: String
)
