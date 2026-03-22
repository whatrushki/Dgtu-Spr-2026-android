# Backend

Ktor backend for Sber ID registration.

## Environment

Set these variables before start:

- `SBER_CLIENT_ID`
- `SBER_CLIENT_SECRET`
- `SBER_REDIRECT_URI`
- `SBER_STAND` = `CLOUD_IFT` or `PROM`
- `SBER_SCOPE` = `openid name email`

Optional mTLS variables:

- `SBER_MTLS_KEYSTORE_PATH`
- `SBER_MTLS_KEYSTORE_PASSWORD`
- `SBER_MTLS_KEYSTORE_TYPE`
- `SBER_MTLS_TRUSTSTORE_PATH`
- `SBER_MTLS_TRUSTSTORE_PASSWORD`
- `SBER_MTLS_TRUSTSTORE_TYPE`

Optional runtime variables:

- `SBER_AUTH_COMPLETED_ENABLED`
- `SBER_SESSION_TTL_MINUTES`
- `SBER_CONNECT_TIMEOUT_MS`
- `SBER_REQUEST_TIMEOUT_MS`

## Run

```powershell
.\gradlew.bat :backend:run
```

## Endpoints

- `GET /health`
- `POST /api/v1/auth/sber/session`
- `POST /api/v1/auth/sber/register`
- `GET /api/v1/auth/sber/users`
