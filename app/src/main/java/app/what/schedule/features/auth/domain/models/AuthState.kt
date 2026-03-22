package app.what.schedule.features.auth.domain.models

import app.what.schedule.data.remote.dealer.DealerCenterUi

data class AuthState(
    val mode: AuthMode = AuthMode.Login,
    val dealerCenters: List<DealerCenterUi> = emptyList(),
    val selectedDealerCenterId: String? = null,
    val selectedRole: String = "SALES_MANAGER",
    val loginSberId: String = "",
    val loginPassword: String = "",
    val registerSberId: String = "",
    val registerFullName: String = "",
    val registerPosition: String = "",
    val registerPhone: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val isLoadingCenters: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedDealerCenter: DealerCenterUi?
        get() = dealerCenters.firstOrNull { it.id == selectedDealerCenterId }
}
