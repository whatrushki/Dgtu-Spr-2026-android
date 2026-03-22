package app.what.schedule.features.pin.domain.models

sealed interface PinEvent {
    data object Init : PinEvent
    data class DigitPressed(val digit: Int) : PinEvent
    data object DeletePressed : PinEvent
    data object DismissError : PinEvent
}
