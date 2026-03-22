package app.what.schedule

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.what.navigation.core.NavigationHost
import app.what.navigation.core.ProvideGlobalDialog
import app.what.navigation.core.ProvideGlobalSheet
import app.what.schedule.data.local.settings.AppValues
import app.what.schedule.data.local.settings.ProvideGLobalAppValues
import app.what.schedule.features.auth.navigation.AuthProvider
import app.what.schedule.features.auth.navigation.authRegistry
import app.what.schedule.features.main.navigation.MainStatusProvider
import app.what.schedule.features.main.navigation.mainRegistry
import app.what.schedule.features.pin.navigation.PinProvider
import app.what.schedule.features.pin.navigation.pinRegistry
import app.what.schedule.ui.theme.AppTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setNavigationBarContrastEnforced(false)
            }

            val settings = koinInject<AppValues>()
            val startDestination = if (!settings.dealerBackendToken.get().isNullOrBlank()) {
                PinProvider
            } else {
                AuthProvider
            }

            ProvideGLobalAppValues(settings) {
                AppTheme {
                    ProvideGlobalDialog {
                        ProvideGlobalSheet {
                            NavigationHost(
                                start = startDestination
                            ) {
                                authRegistry()
                                mainRegistry()
                                pinRegistry()
                            }
                        }
                    }
                }
            }
        }
    }
}
