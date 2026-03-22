package app.what.backend.auth

import app.what.backend.config.SberBackendConfig
import app.what.backend.sber.IdTokenClaims
import app.what.backend.sber.SberIdApiClient
import app.what.backend.sber.decodeIdTokenClaims
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.security.SecureRandom
import java.util.Base64

class AuthRoutes(
    private val config: SberBackendConfig,
    private val authSessionStore: AuthSessionStore,
    private val partnerUserStore: PartnerUserStore,
    private val sberIdApiClient: SberIdApiClient
) {
    fun register(route: Route) {
        route.route("/api/v1/auth/sber") {
            post("/session") {
                val session = authSessionStore.create()
                call.respond(
                    CreateAuthSessionResponse(
                        sessionId = session.id,
                        state = session.state,
                        nonce = session.nonce,
                        redirectUri = config.redirectUri,
                        scope = config.scope,
                        clientId = config.clientId,
                        stand = config.stand.name
                    )
                )
            }

            post("/register") {
                val request = call.receive<CompleteRegistrationRequest>()
                val authSession = authSessionStore.get(request.sessionId)
                    ?: throw IllegalArgumentException("Unknown or expired auth session")

                if (request.state != authSession.state) {
                    throw IllegalArgumentException("State validation failed")
                }
                if (request.nonce != authSession.nonce) {
                    throw IllegalArgumentException("Nonce validation failed before token exchange")
                }

                val tokens = sberIdApiClient.exchangeAuthorizationCode(
                    authCode = request.authCode,
                    codeVerifier = request.codeVerifier
                )
                val claims = decodeIdTokenClaims(tokens.idToken)
                validateIdToken(claims, authSession)

                val userInfo = sberIdApiClient.loadUserInfo(tokens.accessToken)
                val (partnerUser, isNewUser) = partnerUserStore.upsert(userInfo)

                if (config.analyticsEnabled) {
                    sberIdApiClient.sendAuthCompleted(tokens.accessToken)
                }

                authSessionStore.delete(request.sessionId)

                call.respond(
                    HttpStatusCode.OK,
                    RegistrationResponse(
                        partnerUserId = partnerUser.partnerUserId,
                        sessionToken = generatePartnerSessionToken(),
                        sberSubjectId = partnerUser.sberSubjectId,
                        displayName = partnerUser.displayName,
                        email = partnerUser.email,
                        phone = partnerUser.phone,
                        isNewUser = isNewUser
                    )
                )
            }

            get("/users") {
                call.respond(
                    UserSummaryResponse(
                        users = partnerUserStore.getAll().map {
                            PartnerUserDto(
                                partnerUserId = it.partnerUserId,
                                sberSubjectId = it.sberSubjectId,
                                displayName = it.displayName,
                                email = it.email,
                                phone = it.phone,
                                createdAtEpochMillis = it.createdAtEpochMillis
                            )
                        }
                    )
                )
            }
        }
    }

    private fun validateIdToken(
        claims: IdTokenClaims,
        authSession: AuthSession
    ) {
        if (claims.audiences.none { it == config.clientId }) {
            throw IllegalArgumentException("ID token audience does not match backend clientId")
        }
        if (claims.nonce != authSession.nonce) {
            throw IllegalArgumentException("Nonce claim does not match auth session")
        }
    }

    private fun generatePartnerSessionToken(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
