package app.what.schedule.features.main.navigation

import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import app.what.navigation.core.register
import app.what.schedule.features.main.MainBenefitFeature
import app.what.schedule.features.main.MainGrowthFeature
import app.what.schedule.features.main.MainServiceFeature
import app.what.schedule.features.main.MainStatusFeature
import kotlinx.serialization.Serializable

@Serializable
object MainStatusProvider : NavProvider()

@Serializable
object MainGrowthProvider : NavProvider()

@Serializable
object MainBenefitProvider : NavProvider()

@Serializable
object MainServiceProvider : NavProvider()

val mainRegistry: Registry = {
    register(MainStatusFeature::class)
    register(MainGrowthFeature::class)
    register(MainBenefitFeature::class)
    register(MainServiceFeature::class)
}
