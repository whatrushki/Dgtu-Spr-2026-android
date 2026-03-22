package app.what.schedule.features.auth.domain.models

sealed interface AuthEvent {
    data object Init : AuthEvent
    data object RetryCenters : AuthEvent
    data class SwitchMode(val mode: AuthMode) : AuthEvent

    data class UpdateLoginSberId(val value: String) : AuthEvent
    data class UpdateLoginPassword(val value: String) : AuthEvent
    data object SubmitLogin : AuthEvent

    data class UpdateRegisterSberId(val value: String) : AuthEvent
    data class UpdateRegisterFullName(val value: String) : AuthEvent
    data class SelectDealerCenter(val id: String) : AuthEvent
    data class SelectRole(val role: String) : AuthEvent
    data class UpdateRegisterPosition(val value: String) : AuthEvent
    data class UpdateRegisterPhone(val value: String) : AuthEvent
    data class UpdateRegisterEmail(val value: String) : AuthEvent
    data class UpdateRegisterPassword(val value: String) : AuthEvent
    data object SubmitRegister : AuthEvent
    data object DismissError : AuthEvent
}
