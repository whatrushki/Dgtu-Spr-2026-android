package app.what.schedule.features.pin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.what.foundation.core.Feature
import app.what.navigation.core.NavComponent
import app.what.navigation.core.rememberNavigator
import app.what.schedule.features.auth.navigation.AuthProvider
import app.what.schedule.features.main.navigation.MainStatusProvider
import app.what.schedule.features.pin.domain.PinController
import app.what.schedule.features.pin.domain.models.PinAction
import app.what.schedule.features.pin.domain.models.PinEvent
import app.what.schedule.features.pin.navigation.PinProvider
import app.what.schedule.features.pin.presentation.PinView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PinFeature(
    override val data: PinProvider
) : Feature<PinController, PinEvent>(),
    NavComponent<PinProvider>,
    KoinComponent {

    override val controller: PinController by inject()

    @Composable
    override fun content(modifier: Modifier) {
        val navigator = rememberNavigator()
        val viewState by controller.collectStates()
        val action by controller.collectActions()
        var isOpeningMain by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            listener(PinEvent.Init)
        }

        LaunchedEffect(action) {
            when (action) {
                PinAction.OpenMain -> {
                    isOpeningMain = true
                    kotlinx.coroutines.delay(900)
                    navigator.c.navigate(MainStatusProvider) {
                        popUpTo(PinProvider) { inclusive = true }
                    }
                    isOpeningMain = false
                    controller.clearAction()
                }

                PinAction.OpenAuth -> {
                    navigator.c.navigate(AuthProvider) {
                        popUpTo(PinProvider) { inclusive = true }
                    }
                    controller.clearAction()
                }

                null -> Unit
            }
        }

        PinView(
            state = viewState,
            isOpeningMain = isOpeningMain,
            listener = listener
        )
    }
}
