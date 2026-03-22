package app.what.backend.sber

import app.what.backend.auth.TokenExchangeResponse
import app.what.backend.auth.UserInfoResponse
import app.what.backend.config.SberBackendConfig
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.SecureRandom
import java.time.Duration
import java.util.UUID
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class SberIdApiClient(
    private val config: SberBackendConfig
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(config.connectTimeoutMillis))
        .sslContext(buildSslContext(config))
        .build()

    fun exchangeAuthorizationCode(
        authCode: String,
        codeVerifier: String
    ): TokenExchangeResponse {
        val form = formBody(
            "grant_type" to "authorization_code",
            "code" to authCode,
            "redirect_uri" to config.redirectUri,
            "client_id" to config.clientId,
            "client_secret" to config.clientSecret,
            "code_verifier" to codeVerifier
        )

        val request = HttpRequest.newBuilder()
            .uri(URI.create(config.tokenUrl))
            .timeout(Duration.ofMillis(config.requestTimeoutMillis))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("rquid", randomRequestId())
            .POST(HttpRequest.BodyPublishers.ofString(form))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        require(response.statusCode() in 200..299) {
            "Token exchange failed: HTTP ${response.statusCode()} ${response.body()}"
        }
        return json.decodeFromString<TokenExchangeResponse>(response.body())
    }

    fun loadUserInfo(accessToken: String): UserInfoResponse {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(config.userInfoUrl))
            .timeout(Duration.ofMillis(config.requestTimeoutMillis))
            .header("Authorization", "Bearer $accessToken")
            .header("x-introspect-rquid", randomRequestId())
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        require(response.statusCode() in 200..299) {
            "User info request failed: HTTP ${response.statusCode()} ${response.body()}"
        }
        return json.decodeFromString<UserInfoResponse>(response.body())
    }

    fun sendAuthCompleted(accessToken: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(config.authCompletedUrl))
            .timeout(Duration.ofMillis(config.requestTimeoutMillis))
            .header("Authorization", "Bearer $accessToken")
            .header("rquid", randomRequestId())
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        require(response.statusCode() in 200..299) {
            "Auth completed event failed: HTTP ${response.statusCode()} ${response.body()}"
        }
    }

    private fun formBody(vararg items: Pair<String, String>): String =
        items.joinToString("&") { (key, value) ->
            "${urlEncode(key)}=${urlEncode(value)}"
        }

    private fun urlEncode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)

    private fun randomRequestId(): String = UUID.randomUUID().toString()

    private fun buildSslContext(config: SberBackendConfig): SSLContext {
        val sslContext = SSLContext.getInstance("TLS")
        val keyManagers = if (!config.mtlsKeystorePath.isNullOrBlank()) {
            val keyStore = KeyStore.getInstance(config.mtlsKeystoreType)
            java.io.FileInputStream(config.mtlsKeystorePath).use { input ->
                keyStore.load(input, config.mtlsKeystorePassword.orEmpty().toCharArray())
            }
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore, config.mtlsKeystorePassword.orEmpty().toCharArray())
            }.keyManagers
        } else {
            null
        }

        val trustManagers = if (!config.mtlsTruststorePath.isNullOrBlank()) {
            val trustStore = KeyStore.getInstance(config.mtlsTruststoreType)
            java.io.FileInputStream(config.mtlsTruststorePath).use { input ->
                trustStore.load(input, config.mtlsTruststorePassword.orEmpty().toCharArray())
            }
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(trustStore)
            }.trustManagers
        } else {
            null
        }

        sslContext.init(keyManagers, trustManagers, SecureRandom())
        return sslContext
    }
}
