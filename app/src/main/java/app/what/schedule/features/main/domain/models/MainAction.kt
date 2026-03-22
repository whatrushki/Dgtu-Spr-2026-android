package app.what.schedule.features.main.domain.models

sealed interface MainAction {
    data object OpenAuth : MainAction
}
