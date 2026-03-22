package app.what.schedule.features.pin.domain

import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.foundation.services.AppLogger.Companion.Auditor
import app.what.schedule.data.local.settings.AppValues
import app.what.schedule.features.pin.domain.models.PinAction
import app.what.schedule.features.pin.domain.models.PinEvent
import app.what.schedule.features.pin.domain.models.PinStage
import app.what.schedule.features.pin.domain.models.PinState
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.time.Instant

class PinController(
    private val settings: AppValues
) : UIController<PinState, PinAction, PinEvent>(PinState()) {

    override fun obtainEvent(viewEvent: PinEvent) = when (viewEvent) {
        PinEvent.Init -> syncFromStorage()
        is PinEvent.DigitPressed -> appendDigit(viewEvent.digit)
        PinEvent.DeletePressed -> deleteDigit()
        PinEvent.DismissError -> updateState { copy(errorMessage = null) }
    }

    private fun syncFromStorage() {
        val currentSberId = settings.dealerBackendSberId.get()
        if (currentSberId.isNullOrBlank()) {
            Auditor.warn("pin", "no backend session, redirecting to auth")
            setAction(PinAction.OpenAuth)
            return
        }

        val hasSavedPin = !settings.appPinHash.get().isNullOrBlank()
        val stage = if (hasSavedPin && settings.appPinOwnerSberId.get() == currentSberId) {
            PinStage.ENTER
        } else {
            PinStage.CREATE
        }
        Auditor.info("pin", "syncFromStorage hasSavedPin=$hasSavedPin owner=${settings.appPinOwnerSberId.get()} current=$currentSberId stage=$stage")
        updateState {
            copy(
                stage = stage,
                enteredPin = "",
                draftPin = null,
                errorMessage = null
            )
        }
    }

    private fun appendDigit(digit: Int) {
        if (viewState.enteredPin.length >= viewState.pinLength) return

        val nextPin = viewState.enteredPin + digit.toString()
        updateState { copy(enteredPin = nextPin, errorMessage = null) }
        if (nextPin.length == viewState.pinLength) {
            processPin(nextPin)
        }
    }

    private fun deleteDigit() {
        if (viewState.enteredPin.isEmpty()) return
        updateState { copy(enteredPin = enteredPin.dropLast(1), errorMessage = null) }
    }

    private fun processPin(pin: String) {
        when (viewState.stage) {
            PinStage.CREATE -> {
                Auditor.info("pin", "draft pin captured")
                updateState {
                    copy(
                        stage = PinStage.CONFIRM,
                        enteredPin = "",
                        draftPin = pin,
                        errorMessage = null
                    )
                }
            }

            PinStage.CONFIRM -> {
                if (viewState.draftPin == pin) {
                    persistPin(pin)
                } else {
                    Auditor.err("pin", "pin confirmation mismatch")
                    updateState {
                        copy(
                            stage = PinStage.CREATE,
                            enteredPin = "",
                            draftPin = null,
                            errorMessage = "PIN-коды не совпали. Введите новый PIN."
                        )
                    }
                }
            }

            PinStage.ENTER -> {
                val expectedHash = settings.appPinHash.get()
                val actualHash = pin.sha256()
                if (!expectedHash.isNullOrBlank() && expectedHash == actualHash) {
                    Auditor.info("pin", "pin verified")
                    updateState { copy(enteredPin = "", errorMessage = null) }
                    setAction(PinAction.OpenMain)
                } else {
                    Auditor.err("pin", "pin verification failed")
                    updateState {
                        copy(
                            enteredPin = "",
                            errorMessage = "Неверный PIN. Попробуйте ещё раз."
                        )
                    }
                }
            }
        }
    }

    private fun persistPin(pin: String) {
        viewModelScope.launch {
            val hash = pin.sha256()
            settings.appPinHash.set(hash)
            settings.appPinSavedAt.set(Instant.now().toString())
            settings.appPinOwnerSberId.set(settings.dealerBackendSberId.get())
            Auditor.info("pin", "pin saved")
            updateState { copy(stage = PinStage.ENTER, enteredPin = "", draftPin = null, errorMessage = null) }
            setAction(PinAction.OpenMain)
        }
    }

    private fun String.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
