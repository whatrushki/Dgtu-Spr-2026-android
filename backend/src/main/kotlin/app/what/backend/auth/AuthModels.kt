package app.what.backend.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAuthSessionResponse(
    val sessionId: String,
    val state: String,
    val nonce: String,
    val redirectUri: String,
    val scope: String,
    val clientId: String,
    val stand: String
)

@Serializable
data class CompleteRegistrationRequest(
    val sessionId: String,
    val authCode: String,
    val state: String,
    val nonce: String,
    val codeVerifier: String
)

@Serializable
data class RegistrationResponse(
    val partnerUserId: String,
    val sessionToken: String,
    val sberSubjectId: String,
    val displayName: String?,
    val email: String?,
    val phone: String?,
    val isNewUser: Boolean
)

@Serializable
data class UserSummaryResponse(
    val users: List<PartnerUserDto>
)

@Serializable
data class PartnerUserDto(
    val partnerUserId: String,
    val sberSubjectId: String,
    val displayName: String?,
    val email: String?,
    val phone: String?,
    val createdAtEpochMillis: Long
)

data class AuthSession(
    val id: String,
    val state: String,
    val nonce: String,
    val createdAtEpochMillis: Long
)

data class PartnerUserRecord(
    val partnerUserId: String,
    val sberSubjectId: String,
    val displayName: String?,
    val email: String?,
    val phone: String?,
    val createdAtEpochMillis: Long
)

@Serializable
data class TokenExchangeResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    @SerialName("id_token")
    val idToken: String,
    @SerialName("token_type")
    val tokenType: String? = null,
    @SerialName("expires_in")
    val expiresIn: Long? = null
)

@Serializable
data class UserInfoResponse(
    val sub: String,
    @SerialName("sub_alt")
    val subAlt: String? = null,
    val name: String? = null,
    @SerialName("given_name")
    val givenName: String? = null,
    @SerialName("family_name")
    val familyName: String? = null,
    val email: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null
)
