package app.what.schedule.features.auth.navigation

import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import app.what.navigation.core.register
import app.what.schedule.features.auth.AuthFeature
import kotlinx.serialization.Serializable

@Serializable
object AuthProvider : NavProvider()

val authRegistry: Registry = {
    register(AuthFeature::class)
}
