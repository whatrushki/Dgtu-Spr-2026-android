package app.what.schedule.features.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import app.what.foundation.core.Feature
import app.what.navigation.core.NavComponent
import app.what.navigation.core.NavProvider
import app.what.navigation.core.rememberNavigator
import app.what.schedule.features.auth.navigation.AuthProvider
import app.what.schedule.features.main.domain.MainController
import app.what.schedule.features.main.domain.models.MainAction
import app.what.schedule.features.main.domain.models.MainEvent
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.navigation.MainBenefitProvider
import app.what.schedule.features.main.navigation.MainGrowthProvider
import app.what.schedule.features.main.navigation.MainServiceProvider
import app.what.schedule.features.main.navigation.MainStatusProvider
import app.what.schedule.features.main.presentation.MainView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseMainFeature<P : NavProvider>(
    override val data: P,
    private val rootTab: MainTab
) : Feature<MainController, MainEvent>(),
    NavComponent<P>,
    KoinComponent {

    override val controller: MainController by inject()

    @Composable
    override fun content(modifier: Modifier) {
        val navigator = rememberNavigator()
        val state by controller.collectStates()
        val action by controller.collectActions()

        LaunchedEffect(rootTab) {
            listener(MainEvent.SelectTab(rootTab))
            listener(MainEvent.Init)
        }

        LaunchedEffect(action) {
            when (action) {
                MainAction.OpenAuth -> {
                    navigator.c.navigate(AuthProvider) {
                        popUpTo(navigator.c.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                    controller.clearAction()
                }

                null -> Unit
            }
        }

        MainView(
            state = state,
            listener = listener
        )
    }
}

class MainStatusFeature(
    override val data: MainStatusProvider
) : BaseMainFeature<MainStatusProvider>(data, MainTab.Status)

class MainGrowthFeature(
    override val data: MainGrowthProvider
) : BaseMainFeature<MainGrowthProvider>(data, MainTab.Rating)

class MainBenefitFeature(
    override val data: MainBenefitProvider
) : BaseMainFeature<MainBenefitProvider>(data, MainTab.FinancialEffect)

class MainServiceFeature(
    override val data: MainServiceProvider
) : BaseMainFeature<MainServiceProvider>(data, MainTab.Support)
