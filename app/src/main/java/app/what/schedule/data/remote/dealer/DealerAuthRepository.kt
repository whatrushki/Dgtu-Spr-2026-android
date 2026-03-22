package app.what.schedule.data.remote.dealer

import app.what.schedule.data.local.settings.AppValues
import app.what.foundation.services.AppLogger.Companion.Auditor

class DealerAuthRepository(
    private val apiClient: DealerApiClient,
    private val settings: AppValues
) {
    suspend fun getDealerCenters(): List<DealerCenterUi> =
        apiClient.getDealerCenters().map { center ->
            DealerCenterUi(
                id = center.id,
                title = center.name,
                subtitle = "${center.code} • ${center.city}, ${center.region}"
            )
        }

    suspend fun login(sberId: String, password: String): AuthSessionUi {
        val response = apiClient.login(
            LoginRequestDto(
                sberId = sberId.trim(),
                password = password
            )
        )
        return saveSession(response)
    }

    suspend fun register(
        sberId: String,
        fullName: String,
        dealerCenterId: String,
        role: String,
        position: String?,
        phone: String?,
        email: String?,
        password: String
    ): AuthSessionUi {
        val response = apiClient.register(
            RegisterRequestDto(
                sberId = sberId.trim(),
                fullName = fullName.trim(),
                dealerCenterId = dealerCenterId,
                role = role,
                position = position?.trim().takeUnless { it.isNullOrBlank() },
                phone = phone?.trim().takeUnless { it.isNullOrBlank() },
                email = email?.trim().takeUnless { it.isNullOrBlank() },
                password = password
            )
        )
        return saveSession(response)
    }

    fun hasSession(): Boolean = !settings.dealerBackendToken.get().isNullOrBlank()

    fun currentSberId(): String? = settings.dealerBackendSberId.get()

    fun logout() {
        Auditor.warn("backend-auth", "logout requested, clearing local backend session")
        settings.dealerBackendToken.set(null)
        settings.dealerBackendResolvedBaseUrl.set(null)
        settings.dealerBackendEmployeeId.set(null)
        settings.dealerBackendSberId.set(null)
        settings.dealerBackendEmployeeName.set(null)
    }

    private fun saveSession(response: AuthResponseDto): AuthSessionUi {
        settings.dealerBackendToken.set(response.accessToken)
        settings.dealerBackendEmployeeId.set(response.employee.id)
        settings.dealerBackendSberId.set(response.employee.sberId)
        settings.dealerBackendEmployeeName.set(response.employee.fullName)
        Auditor.info("backend-auth", "backend session saved for ${response.employee.sberId}")
        return AuthSessionUi(
            employeeId = response.employee.id,
            sberId = response.employee.sberId,
            fullName = response.employee.fullName,
            role = response.employee.role,
            level = response.employee.level
        )
    }
}

data class DealerCenterUi(
    val id: String,
    val title: String,
    val subtitle: String
)

data class AuthSessionUi(
    val employeeId: String,
    val sberId: String,
    val fullName: String,
    val role: String,
    val level: String
)
