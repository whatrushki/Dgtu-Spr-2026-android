package app.what.schedule.features.pin.navigation

import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import app.what.navigation.core.register
import app.what.schedule.features.pin.PinFeature
import kotlinx.serialization.Serializable

@Serializable
object PinProvider : NavProvider()

val pinRegistry: Registry = {
    register(PinFeature::class)
}
