package app.what.schedule.features.pin.domain.models

data class PinState(
    val stage: PinStage = PinStage.CREATE,
    val enteredPin: String = "",
    val draftPin: String? = null,
    val errorMessage: String? = null
) {
    val pinLength: Int = 5
}
