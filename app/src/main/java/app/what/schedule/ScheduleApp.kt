package app.what.schedule

import android.app.Application
import app.what.foundation.data.settings.PreferenceStorage
import app.what.foundation.services.AppLogger
import app.what.foundation.services.AppLogger.Companion.Auditor
import app.what.foundation.services.auto_update.InstallSource
import app.what.foundation.services.auto_update.getInstallSource
import app.what.foundation.services.crash.CrashHandler
import app.what.schedule.data.local.settings.AppValues
import app.what.schedule.data.remote.dealer.DealerApiClient
import app.what.schedule.data.remote.dealer.DealerAuthRepository
import app.what.schedule.data.remote.dealer.DealerBackendRepository
import app.what.schedule.features.auth.domain.AuthController
import app.what.schedule.features.dev.presentation.NetworkMonitorPlugin
import app.what.schedule.features.main.domain.MainController
import app.what.schedule.features.news.domain.NewsController
import app.what.schedule.features.onboarding.domain.OnboardingController
import app.what.schedule.features.pin.domain.PinController
import app.what.schedule.features.schedule.domain.ScheduleController
import app.what.schedule.features.settings.domain.SettingsController
import app.what.schedule.libs.FileManager
import app.what.schedule.libs.GoogleDriveParser
import app.what.schedule.utils.AppUtils
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ScheduleApp : Application() {
    override fun onCreate() {
        super.onCreate()


        AppLogger.initialize(applicationContext)
        CrashHandler.initialize(applicationContext, CrashActivity::class.java)

        val koinApp = startKoin {
            androidContext(this@ScheduleApp)
            modules(generalModule, controllers)
        }

        val koin = koinApp.koin
        koin.get<AppValues>()

        SingletonImageLoader.setSafe {
            ImageLoader.Builder(this)
                .crossfade(true)
                .components {
                    add(KtorNetworkFetcherFactory({ koin.get<HttpClient>() }))
                }
                .build()
        }

        val source = getInstallSource(this)

        when (source) {
            InstallSource.APK -> Auditor.debug("d", "install source Apk")
            InstallSource.RuStore -> Auditor.debug("d", "install source RuStore")
        }
    }
}

val controllers = module {
    singleOf(::AuthController)
    singleOf(::SettingsController)
    singleOf(::NewsController)
    singleOf(::ScheduleController)
    singleOf(::OnboardingController)
    singleOf(::PinController)
    singleOf(::MainController)
}

val generalModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) }

    singleOf(::AppValues) bind PreferenceStorage::class
    singleOf(::DealerApiClient)
    singleOf(::DealerAuthRepository)
    singleOf(::AppUtils)
    singleOf(::GoogleDriveParser)
    singleOf(::FileManager)
    singleOf(::DealerBackendRepository)

//
//    single {
//        Room.databaseBuilder(
//            androidContext(),
//            AppDatabase::class.java,
//            "schedule.db"
//        )
//            .fallbackToDestructiveMigration(true)
//            .build()
//    }

    single {
        HttpClient(CIO) {
            install(NetworkMonitorPlugin)

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Auditor.debug("ktor", message)
                    }
                }
            }

            install(ContentNegotiation) {
                json(Json {
                    classDiscriminator = "type"
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    explicitNulls = false
                })
            }

            install(HttpTimeout) {
                this@HttpClient.expectSuccess = false
                requestTimeoutMillis = 60 * 1000
            }

        }
    }
}
