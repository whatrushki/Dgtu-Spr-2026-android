package app.what.schedule.data.remote.dealer

import android.os.Build
import app.what.schedule.BuildConfig
import app.what.schedule.data.local.settings.AppValues
import app.what.foundation.services.AppLogger.Companion.Auditor
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException

class DealerApiClient(
    private val httpClient: HttpClient,
    private val settings: AppValues
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    suspend fun getDealerCenters(): List<DealerCenterResponseDto> =
        request(
            method = HttpMethod.Get,
            path = "/auth/dealer-centers",
            requiresAuth = false,
            retryPolicy = RetryPolicy.SafeRead
        )

    suspend fun login(request: LoginRequestDto): AuthResponseDto =
        request(
            method = HttpMethod.Post,
            path = "/auth/login-sber",
            requiresAuth = false,
            retryPolicy = RetryPolicy.None,
            body = request
        )

    suspend fun register(request: RegisterRequestDto): AuthResponseDto =
        request(
            method = HttpMethod.Post,
            path = "/auth/register",
            requiresAuth = false,
            retryPolicy = RetryPolicy.None,
            body = request
        )

    suspend fun getProfile(): ProfileResponseDto =
        authRequest(HttpMethod.Get, "/api/v1/profile", RetryPolicy.SafeRead)

    suspend fun getStatus(): StatusResponseDto =
        authRequest(HttpMethod.Get, "/api/v1/status", RetryPolicy.SafeRead)

    suspend fun getRatingDetail(): RatingDetailResponseDto =
        authRequest(HttpMethod.Get, "/api/v1/rating/detail", RetryPolicy.SafeRead)

    suspend fun getFinancialEffect(): FinancialEffectResponseDto =
        authRequest(HttpMethod.Get, "/api/v1/financial-effect", RetryPolicy.SafeRead)

    suspend fun getDailyResults(date: String? = null): DailyResultResponseDto =
        authRequest(
            method = HttpMethod.Get,
            path = "/api/v1/daily-results",
            retryPolicy = RetryPolicy.SafeRead,
            query = if (date == null) emptyMap() else mapOf("date" to date)
        )

    suspend fun postDailyResults(request: DailyResultRequestDto): DailyResultResponseDto =
        authRequest(
            method = HttpMethod.Post,
            path = "/api/v1/daily-results",
            retryPolicy = RetryPolicy.None,
            body = request
        )

    suspend fun getPrivileges(): List<PrivilegeItemResponseDto> =
        authRequest(HttpMethod.Get, "/api/v1/privileges", RetryPolicy.SafeRead)

    suspend fun getTasks(): List<TaskResponseDto> =
        authRequest(HttpMethod.Get, "/api/v1/tasks", RetryPolicy.SafeRead)

    suspend fun getLeaderboard(type: String = "dealer"): List<LeaderboardItemDto> =
        authRequest(
            method = HttpMethod.Get,
            path = "/api/v1/leaderboard",
            retryPolicy = RetryPolicy.SafeRead,
            query = mapOf("type" to type)
        )

    suspend fun getMyPosition(type: String = "dealer"): MyPositionResponseDto =
        authRequest(
            method = HttpMethod.Get,
            path = "/api/v1/leaderboard/my-position",
            retryPolicy = RetryPolicy.SafeRead,
            query = mapOf("type" to type)
        )

    suspend fun calculateScenario(request: ScenarioRequestDto): ScenarioResponseDto =
        authRequest(
            method = HttpMethod.Post,
            path = "/api/v1/calculator",
            retryPolicy = RetryPolicy.RepeatableMutation,
            body = request
        )

    suspend fun getLearningModules(): List<LearningModuleResponseDto> =
        authRequest(HttpMethod.Get, "/api/v1/learning/modules", RetryPolicy.SafeRead)

    suspend fun getLearningQuiz(moduleId: String): LearningQuizResponseDto =
        authRequest(
            method = HttpMethod.Get,
            path = "/api/v1/learning/quiz",
            retryPolicy = RetryPolicy.SafeRead,
            query = mapOf("moduleId" to moduleId)
        )

    suspend fun getLearningAttempts(): List<LearningAttemptResponseDto> =
        authRequest(HttpMethod.Get, "/api/v1/learning/attempts", RetryPolicy.SafeRead)

    suspend fun submitLearningQuiz(request: LearningQuizSubmitRequestDto): LearningQuizSubmitResponseDto =
        authRequest(
            method = HttpMethod.Post,
            path = "/api/v1/learning/quiz/submit",
            retryPolicy = RetryPolicy.None,
            body = request
        )

    suspend fun completeLearningModule(moduleId: String): LearningModuleResponseDto =
        authRequest(
            method = HttpMethod.Post,
            path = "/api/v1/learning/complete",
            retryPolicy = RetryPolicy.None,
            body = LearningCompleteRequestDto(moduleId)
        )

    suspend fun getSupportTickets(): List<SupportTicketResponseDto> =
        authRequest(HttpMethod.Get, "/api/v1/support/tickets", RetryPolicy.SafeRead)

    suspend fun createSupportTicket(request: SupportTicketRequestDto): SupportTicketResponseDto =
        authRequest(
            method = HttpMethod.Post,
            path = "/api/v1/support/ticket",
            retryPolicy = RetryPolicy.None,
            body = request
        )

    suspend fun askSupportAssistant(request: AssistantAskRequestDto): AssistantAskResponseDto =
        authRequest(
            method = HttpMethod.Post,
            path = "/api/v1/support/assistant/ask",
            retryPolicy = RetryPolicy.None,
            body = request
        )

    suspend fun getSupportAssistantHistory(): List<AssistantHistoryItemDto> =
        authRequest(HttpMethod.Get, "/api/v1/support/assistant/history", RetryPolicy.SafeRead)

    suspend fun getNews(level: String? = null): List<NewsResponseDto> =
        authRequest(
            method = HttpMethod.Get,
            path = "/api/v1/news",
            retryPolicy = RetryPolicy.SafeRead,
            query = if (level == null) emptyMap() else mapOf("level" to level)
        )

    private suspend inline fun <reified T> authRequest(
        method: HttpMethod,
        path: String,
        retryPolicy: RetryPolicy,
        query: Map<String, String> = emptyMap(),
        body: Any? = null
    ): T {
        val token = settings.dealerBackendToken.get()
        if (token.isNullOrBlank()) {
            throw DealerUnauthorizedException("Не найдена активная backend-сессия")
        }
        return request(
            method = method,
            path = path,
            requiresAuth = true,
            retryPolicy = retryPolicy,
            query = query,
            body = body,
            token = token
        )
    }

    private suspend inline fun <reified T> request(
        method: HttpMethod,
        path: String,
        requiresAuth: Boolean,
        retryPolicy: RetryPolicy,
        query: Map<String, String> = emptyMap(),
        body: Any? = null,
        token: String? = null
    ): T {
        var lastError: Throwable? = null
        for (baseUrl in candidateBaseUrls()) {
            val normalizedBaseUrl = baseUrl.normalizeBaseUrl()
            val retries = retryPolicy.maxAttempts

            for (attemptIndex in 0 until retries) {
                val attempt = attemptIndex + 1
                try {
                    Auditor.debug("backend-api", "${method.value} $normalizedBaseUrl$path attempt=$attempt/$retries")
                    val response = httpClient.request("$normalizedBaseUrl$path") {
                        this.method = method
                        if (requiresAuth) {
                            bearerAuth(token ?: error("token is required"))
                        }
                        if (body != null) {
                            contentType(ContentType.Application.Json)
                            setBody(body)
                        }
                        query.forEach { (key, value) -> parameter(key, value) }
                    }

                    val status = response.status
                    when {
                        status.value in 200..299 -> {
                            settings.dealerBackendResolvedBaseUrl.set(normalizedBaseUrl)
                            val raw = response.bodyAsText()
                            return try {
                                json.decodeFromString<T>(raw)
                            } catch (error: SerializationException) {
                                Auditor.err(
                                    "backend-api",
                                    "decode failed for ${method.value} $normalizedBaseUrl$path body=${raw.take(1200)}",
                                    error
                                )
                                throw error
                            }
                        }

                        status == HttpStatusCode.Unauthorized -> {
                            val message = response.readErrorMessage()
                            throw DealerUnauthorizedException(message)
                        }

                        status.value in 500..599 && retryPolicy.shouldRetryStatus(status) && attempt < retries -> {
                            val message = response.readErrorMessage()
                            Auditor.warn(
                                "backend-api",
                                "${method.value} $normalizedBaseUrl$path server error=${status.value}, retrying: $message"
                            )
                            delay(retryPolicy.delayFor(attempt))
                        }

                        else -> {
                            val message = response.readErrorMessage()
                            throw DealerApiException(message, status.value)
                        }
                    }
                } catch (error: Throwable) {
                    val retryable = error.isRetryable() && retryPolicy.shouldRetryError(error) && attempt < retries
                    Auditor.err(
                        "backend-api",
                        "${method.value} $normalizedBaseUrl$path failed on attempt=$attempt/$retries: ${error.message}",
                        error
                    )
                    lastError = error
                    if (error is DealerUnauthorizedException) throw error
                    if (retryable) {
                        delay(retryPolicy.delayFor(attempt))
                    } else {
                        break
                    }
                }
            }
        }

        throw lastError ?: DealerApiException("Не удалось подключиться к backend", null)
    }

    private fun candidateBaseUrls(): List<String> = buildList {
        settings.dealerBackendResolvedBaseUrl.get()?.let(::add)
        add(BuildConfig.DEALER_BACKEND_BASE_URL)
        if (isProbablyEmulator()) {
            add("http://10.0.2.2:8080")
            add("http://127.0.0.1:8080")
        }
    }.distinct()

    private fun isProbablyEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val product = Build.PRODUCT.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        return fingerprint.contains("generic") ||
            fingerprint.contains("emulator") ||
            model.contains("emulator") ||
            model.contains("sdk") ||
            product.contains("sdk") ||
            manufacturer.contains("genymotion")
    }

    private fun String.normalizeBaseUrl(): String = trimEnd('/')

    private suspend fun io.ktor.client.statement.HttpResponse.readErrorMessage(): String {
        val raw = bodyAsText()
        return runCatching { json.decodeFromString(ErrorResponseDto.serializer(), raw).error }
            .getOrElse {
                raw.takeIf { text -> text.isNotBlank() } ?: "Backend вернул ошибку ${status.value}"
            }
    }

    private fun Throwable.isRetryable(): Boolean =
        this is IOException || this is HttpRequestTimeoutException
}

class DealerApiException(
    message: String,
    val statusCode: Int?
) : Exception(message)

class DealerUnauthorizedException(message: String) : Exception(message)

private enum class RetryPolicy(val maxAttempts: Int) {
    None(1),
    SafeRead(3),
    RepeatableMutation(2);

    fun shouldRetryStatus(status: HttpStatusCode): Boolean =
        this != None && status.value in 500..599

    fun shouldRetryError(error: Throwable): Boolean =
        this != None && error !is SerializationException

    fun delayFor(attempt: Int): Long = when (attempt) {
        1 -> 350L
        else -> 900L
    }
}

@Serializable
data class ErrorResponseDto(
    val error: String = ""
)

@Serializable
data class LoginRequestDto(
    val sberId: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val sberId: String,
    val fullName: String,
    val dealerCenterId: String,
    val role: String,
    val position: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val password: String
)

@Serializable
data class AuthResponseDto(
    val accessToken: String = "",
    val employee: EmployeeDto = EmployeeDto()
)

@Serializable
data class EmployeeDto(
    val id: String = "",
    val sberId: String = "",
    val fullName: String = "",
    val role: String = "",
    val position: String = "",
    val phone: String? = null,
    val email: String? = null,
    val dealerCenterId: String = "",
    val level: String = "SILVER",
    val totalPoints: Int = 0,
    val registeredAt: String = ""
)

@Serializable
data class DealerCenterResponseDto(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val city: String = "",
    val region: String = ""
)

@Serializable
data class FinancialForecastDto(
    val annualExtraIncomeRub: Long = 0,
    val mortgageSavingRub: Long = 0,
    val cashbackDmsPremierRub: Long = 0,
    val totalRub: Long = 0
)

@Serializable
data class ProfileResponseDto(
    val fullName: String = "",
    val dealerCenterCode: String = "",
    val dealerCenterName: String = "",
    val position: String = "",
    val sberId: String = "",
    val level: String = "SILVER",
    val registeredAt: String = "",
    val phone: String? = null,
    val email: String? = null
)

@Serializable
data class StatusResponseDto(
    val level: String = "SILVER",
    val badgeUrl: String = "",
    val totalPoints: Int = 0,
    val nextLevelPoints: Int = 0,
    val progressPercent: Double = 0.0,
    val toNextLevelText: String = "",
    val financialForecast: FinancialForecastDto = FinancialForecastDto(0, 0, 0, 0)
)

@Serializable
data class RatingDetailResponseDto(
    val totalPoints: Int = 0,
    val volumePoints: Int = 0,
    val dealsPoints: Int = 0,
    val sharePoints: Int = 0,
    val conversionPoints: Int = 0,
    val additionalProductsPoints: Int = 0,
    val howCalculatedText: String = "",
    val howToImproveText: String = "",
    val calculatedAt: String = ""
)

@Serializable
data class ScenarioRequestDto(
    val dealsDelta: Int,
    val volumeDeltaRub: Long,
    val shareDeltaPercent: Double,
    val productsDelta: Int
)

@Serializable
data class ScenarioResponseDto(
    val simulatedStatus: StatusResponseDto,
    val simulatedIncomeRub: Long,
    val simulatedMortgageSavingRub: Long
)

@Serializable
data class PrivilegeItemResponseDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val status: String = "",
    val financialEffectRub: Long = 0,
    val levelRequired: String = "SILVER",
    val iconUrl: String? = null
)

@Serializable
data class DailyResultEntryDto(
    val id: String = "",
    val date: String = "",
    val dealCount: Int = 0,
    val volumeRub: Long = 0,
    val bankSharePercent: Double = 0.0,
    val additionalProductsCount: Int = 0,
    val createdAt: String = ""
)

@Serializable
data class DailyResultRequestDto(
    val date: String,
    val dealCount: Int,
    val volumeRub: Long,
    val bankSharePercent: Double,
    val additionalProductsCount: Int = 0
)

@Serializable
data class DailyResultResponseDto(
    val date: String = "",
    val totalDeals: Int = 0,
    val totalVolumeRub: Long = 0,
    val averageSharePercent: Double = 0.0,
    val additionalProductsCount: Int = 0,
    val items: List<DailyResultEntryDto> = emptyList()
)

@Serializable
data class FinancialEffectResponseDto(
    val headlineRub: Long = 0,
    val level: String = "SILVER",
    val breakdown: FinancialForecastDto = FinancialForecastDto(0, 0, 0, 0)
)

@Serializable
data class TaskResponseDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val pointsReward: Int = 0,
    val deadline: String = "",
    val targetValue: Int = 0,
    val metricType: String = "",
    val currentValue: Int = 0,
    val completed: Boolean = false,
    val progressPercent: Double = 0.0
)

@Serializable
data class LeaderboardItemDto(
    val position: Int = 0,
    val employeeId: String = "",
    val fullName: String = "",
    val dealerCenterCode: String = "",
    val totalPoints: Int = 0,
    val level: String = "SILVER"
)

@Serializable
data class MyPositionResponseDto(
    val type: String = "dealer",
    val position: Int = 0,
    val totalParticipants: Int = 0,
    val totalPoints: Int = 0,
    val level: String = "SILVER"
)

@Serializable
data class LearningModuleResponseDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val videoUrl: String? = null,
    val durationMin: Int? = null,
    val quizPassPercent: Int? = null,
    val attemptsCount: Int? = null,
    val lastScorePercent: Int? = null,
    val pointsReward: Int = 0,
    val completed: Boolean = false
)

@Serializable
data class LearningCompleteRequestDto(
    val moduleId: String
)

@Serializable
data class LearningQuizQuestionResponseDto(
    val id: String = "",
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val orderNo: Int? = null
)

@Serializable
data class LearningQuizResponseDto(
    val moduleId: String = "",
    val passScorePercent: Int? = null,
    val questions: List<LearningQuizQuestionResponseDto> = emptyList(),
)

@Serializable
data class LearningAnswerRequestDto(
    val questionId: String,
    val selectedOptionIndex: Int
)

@Serializable
data class LearningQuizSubmitRequestDto(
    val moduleId: String,
    val answers: List<LearningAnswerRequestDto>
)

@Serializable
data class LearningQuizSubmitResponseDto(
    val attemptId: String = "",
    val scorePercent: Int = 0,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val passed: Boolean = false,
    val awardedPoints: Int = 0,
    val moduleCompleted: Boolean = false
)

@Serializable
data class LearningAttemptResponseDto(
    val attemptId: String = "",
    val moduleId: String = "",
    val scorePercent: Int = 0,
    val passed: Boolean = false,
    val awardedPoints: Int = 0,
    val attemptedAt: String = ""
)

@Serializable
data class SupportTicketRequestDto(
    val subject: String,
    val message: String,
    val category: String? = null,
    val priority: String? = null
)

@Serializable
data class SupportTicketResponseDto(
    val id: String = "",
    val subject: String = "",
    val message: String = "",
    val status: String = "",
    val createdAt: String = "",
    val updatedAt: String? = null,
    val category: String? = null,
    val priority: String? = null,
    val resolution: String? = null
)

@Serializable
data class AssistantAskRequestDto(
    val question: String
)

@Serializable
data class AssistantAskResponseDto(
    val answer: String = "",
    val createdAt: String = "",
    val suggestions: List<String> = emptyList(),
    val sources: List<String> = emptyList()
)

@Serializable
data class AssistantHistoryItemDto(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val createdAt: String = ""
)

@Serializable
data class NewsResponseDto(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val publishedAt: String = "",
    val targetLevel: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val tags: List<String> = emptyList()
)
