package app.what.schedule.features.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.what.foundation.core.Feature
import app.what.navigation.core.NavComponent
import app.what.navigation.core.rememberNavigator
import app.what.schedule.features.auth.domain.AuthController
import app.what.schedule.features.auth.domain.models.AuthAction
import app.what.schedule.features.auth.domain.models.AuthEvent
import app.what.schedule.features.auth.navigation.AuthProvider
import app.what.schedule.features.auth.presentation.AuthView
import app.what.schedule.features.pin.navigation.PinProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthFeature(
    override val data: AuthProvider
) : Feature<AuthController, AuthEvent>(),
    NavComponent<AuthProvider>,
    KoinComponent {

    override val controller: AuthController by inject()

    @Composable
    override fun content(modifier: Modifier) {
        val navigator = rememberNavigator()
        val state by controller.collectStates()
        val action by controller.collectActions()

        LaunchedEffect(Unit) {
            listener(AuthEvent.Init)
        }

        LaunchedEffect(action) {
            when (action) {
                AuthAction.OpenPin -> {
                    navigator.c.navigate(PinProvider) {
                        popUpTo(AuthProvider) { inclusive = true }
                    }
                    controller.clearAction()
                }

                null -> Unit
            }
        }

        AuthView(state = state, listener = listener)
    }
}
