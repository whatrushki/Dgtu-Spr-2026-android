package app.what.backend.config

enum class SberStand {
    PROM,
    CLOUD_IFT
}

data class SberBackendConfig(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val scope: String,
    val stand: SberStand,
    val sessionTtlMinutes: Long,
    val connectTimeoutMillis: Long,
    val requestTimeoutMillis: Long,
    val analyticsEnabled: Boolean,
    val mtlsKeystorePath: String?,
    val mtlsKeystorePassword: String?,
    val mtlsKeystoreType: String,
    val mtlsTruststorePath: String?,
    val mtlsTruststorePassword: String?,
    val mtlsTruststoreType: String
) {
    val tokenUrl: String
        get() = if (stand == SberStand.PROM) {
            "https://oauth.sber.ru/ru/prod/tokens/v2/oidc"
        } else {
            "https://oauth-ift.sber.ru/ru/prod/tokens/v2/oidc"
        }

    val userInfoUrl: String
        get() = if (stand == SberStand.PROM) {
            "https://oauth.sber.ru/ru/prod/sberbankid/v2.1/userinfo"
        } else {
            "https://oauth-ift.sber.ru/ru/prod/sberbankid/v2.1/userinfo"
        }

    val authCompletedUrl: String
        get() = if (stand == SberStand.PROM) {
            "https://oauth.sber.ru/ru/prod/api/v2/auth/completed"
        } else {
            "https://oauth-ift.sber.ru/ru/prod/api/v2/auth/completed"
        }

    companion object {
        fun fromEnvironment(): SberBackendConfig = SberBackendConfig(
            clientId = env("SBER_CLIENT_ID", "demo-client-id"),
            clientSecret = env("SBER_CLIENT_SECRET", "demo-client-secret"),
            redirectUri = env("SBER_REDIRECT_URI", "app.what.dontknow://sberid/callback"),
            scope = env("SBER_SCOPE", "openid name email"),
            stand = SberStand.valueOf(env("SBER_STAND", "CLOUD_IFT")),
            sessionTtlMinutes = env("SBER_SESSION_TTL_MINUTES", "10").toLong(),
            connectTimeoutMillis = env("SBER_CONNECT_TIMEOUT_MS", "15000").toLong(),
            requestTimeoutMillis = env("SBER_REQUEST_TIMEOUT_MS", "30000").toLong(),
            analyticsEnabled = env("SBER_AUTH_COMPLETED_ENABLED", "false").toBoolean(),
            mtlsKeystorePath = System.getenv("SBER_MTLS_KEYSTORE_PATH"),
            mtlsKeystorePassword = System.getenv("SBER_MTLS_KEYSTORE_PASSWORD"),
            mtlsKeystoreType = env("SBER_MTLS_KEYSTORE_TYPE", "PKCS12"),
            mtlsTruststorePath = System.getenv("SBER_MTLS_TRUSTSTORE_PATH"),
            mtlsTruststorePassword = System.getenv("SBER_MTLS_TRUSTSTORE_PASSWORD"),
            mtlsTruststoreType = env("SBER_MTLS_TRUSTSTORE_TYPE", "JKS")
        )

        private fun env(name: String, defaultValue: String): String =
            System.getenv(name)?.takeIf { it.isNotBlank() } ?: defaultValue
    }
}
