package app.what.backend.sber

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.util.Base64

data class IdTokenClaims(
    val audiences: List<String>,
    val nonce: String? = null,
    val sub: String? = null,
    val iss: String? = null
)

fun decodeIdTokenClaims(idToken: String): IdTokenClaims {
    val segments = idToken.split(".")
    require(segments.size >= 2) { "Invalid id_token format" }
    val payload = Base64.getUrlDecoder().decode(segments[1])
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    val jsonObject = json.decodeFromString<JsonObject>(payload.decodeToString())
    val audienceElement = jsonObject["aud"]
        ?: throw IllegalArgumentException("ID token does not contain aud claim")
    val audiences = when (audienceElement) {
        is JsonPrimitive -> listOf(audienceElement.content)
        is JsonArray -> audienceElement.map { it.jsonPrimitive.content }
        else -> throw IllegalArgumentException("Unsupported aud claim format")
    }

    return IdTokenClaims(
        audiences = audiences,
        nonce = jsonObject["nonce"]?.jsonPrimitive?.contentOrNull,
        sub = jsonObject["sub"]?.jsonPrimitive?.contentOrNull,
        iss = jsonObject["iss"]?.jsonPrimitive?.contentOrNull
    )
}
