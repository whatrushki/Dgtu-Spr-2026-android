package app.what.schedule.features.auth.domain.models

sealed interface AuthAction {
    data object OpenPin : AuthAction
}
