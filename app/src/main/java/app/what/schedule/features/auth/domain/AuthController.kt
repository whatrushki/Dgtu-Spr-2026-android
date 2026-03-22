package app.what.schedule.features.auth.domain

import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.foundation.services.AppLogger.Companion.Auditor
import app.what.schedule.data.remote.dealer.DealerAuthRepository
import app.what.schedule.features.auth.domain.models.AuthAction
import app.what.schedule.features.auth.domain.models.AuthEvent
import app.what.schedule.features.auth.domain.models.AuthMode
import app.what.schedule.features.auth.domain.models.AuthState
import kotlinx.coroutines.launch

class AuthController(
    private val authRepository: DealerAuthRepository
) : UIController<AuthState, AuthAction, AuthEvent>(AuthState()) {

    override fun obtainEvent(viewEvent: AuthEvent) = when (viewEvent) {
        AuthEvent.Init -> loadDealerCenters()
        AuthEvent.RetryCenters -> loadDealerCenters(force = true)
        is AuthEvent.SwitchMode -> updateState { copy(mode = viewEvent.mode, errorMessage = null) }
        is AuthEvent.UpdateLoginSberId -> updateState { copy(loginSberId = viewEvent.value, errorMessage = null) }
        is AuthEvent.UpdateLoginPassword -> updateState { copy(loginPassword = viewEvent.value, errorMessage = null) }
        AuthEvent.SubmitLogin -> submitLogin()
        is AuthEvent.UpdateRegisterSberId -> updateState { copy(registerSberId = viewEvent.value, errorMessage = null) }
        is AuthEvent.UpdateRegisterFullName -> updateState { copy(registerFullName = viewEvent.value, errorMessage = null) }
        is AuthEvent.SelectDealerCenter -> updateState { copy(selectedDealerCenterId = viewEvent.id, errorMessage = null) }
        is AuthEvent.SelectRole -> updateState { copy(selectedRole = viewEvent.role, errorMessage = null) }
        is AuthEvent.UpdateRegisterPosition -> updateState { copy(registerPosition = viewEvent.value, errorMessage = null) }
        is AuthEvent.UpdateRegisterPhone -> updateState { copy(registerPhone = viewEvent.value, errorMessage = null) }
        is AuthEvent.UpdateRegisterEmail -> updateState { copy(registerEmail = viewEvent.value, errorMessage = null) }
        is AuthEvent.UpdateRegisterPassword -> updateState { copy(registerPassword = viewEvent.value, errorMessage = null) }
        AuthEvent.SubmitRegister -> submitRegister()
        AuthEvent.DismissError -> updateState { copy(errorMessage = null) }
    }

    private fun loadDealerCenters(force: Boolean = false) {
        if (viewState.isLoadingCenters && !force) return

        viewModelScope.launch {
            updateState { copy(isLoadingCenters = true, errorMessage = null) }
            runCatching { authRepository.getDealerCenters() }
                .onSuccess { centers ->
                    updateState {
                        copy(
                            dealerCenters = centers,
                            selectedDealerCenterId = selectedDealerCenterId ?: centers.firstOrNull()?.id,
                            isLoadingCenters = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    Auditor.err("auth", "failed to load dealer centers: ${error.message}", error)
                    updateState {
                        copy(
                            isLoadingCenters = false,
                            errorMessage = error.message ?: "Не удалось загрузить дилерские центры"
                        )
                    }
                }
        }
    }

    private fun submitLogin() {
        if (viewState.isSubmitting) return
        val sberId = viewState.loginSberId.trim()
        val password = viewState.loginPassword
        if (sberId.isBlank() || password.isBlank()) {
            updateState { copy(errorMessage = "Введите Sber ID и пароль") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isSubmitting = true, errorMessage = null) }
            runCatching { authRepository.login(sberId, password) }
                .onSuccess {
                    Auditor.info("auth", "login success for ${it.sberId}")
                    updateState { copy(isSubmitting = false, errorMessage = null) }
                    setAction(AuthAction.OpenPin)
                }
                .onFailure { error ->
                    Auditor.err("auth", "login failed: ${error.message}", error)
                    updateState {
                        copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Не удалось выполнить вход"
                        )
                    }
                }
        }
    }

    private fun submitRegister() {
        if (viewState.isSubmitting) return
        if (viewState.registerSberId.isBlank() ||
            viewState.registerFullName.isBlank() ||
            viewState.selectedDealerCenterId.isNullOrBlank() ||
            viewState.registerPassword.isBlank()
        ) {
            updateState { copy(errorMessage = "Заполните обязательные поля регистрации") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isSubmitting = true, errorMessage = null) }
            runCatching {
                authRepository.register(
                    sberId = viewState.registerSberId,
                    fullName = viewState.registerFullName,
                    dealerCenterId = viewState.selectedDealerCenterId.orEmpty(),
                    role = viewState.selectedRole,
                    position = viewState.registerPosition,
                    phone = viewState.registerPhone,
                    email = viewState.registerEmail,
                    password = viewState.registerPassword
                )
            }
                .onSuccess {
                    Auditor.info("auth", "register success for ${it.sberId}")
                    updateState { copy(isSubmitting = false, errorMessage = null) }
                    setAction(AuthAction.OpenPin)
                }
                .onFailure { error ->
                    Auditor.err("auth", "register failed: ${error.message}", error)
                    updateState {
                        copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Не удалось завершить регистрацию"
                        )
                    }
                }
        }
    }
}
