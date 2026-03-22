package app.what.schedule.features.pin.domain.models

sealed interface PinAction {
    data object OpenMain : PinAction
    data object OpenAuth : PinAction
}
