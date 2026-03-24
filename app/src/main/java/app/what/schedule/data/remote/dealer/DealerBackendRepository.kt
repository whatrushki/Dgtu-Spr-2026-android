package app.what.schedule.data.remote.dealer

import app.what.foundation.services.AppLogger.Companion.Auditor
import app.what.schedule.data.local.settings.AppValues
import app.what.schedule.features.main.domain.models.AssistantMessageUi
import app.what.schedule.features.main.domain.models.DailyEntryUi
import app.what.schedule.features.main.domain.models.DailyFormUi
import app.what.schedule.features.main.domain.models.DailyResultsUi
import app.what.schedule.features.main.domain.models.DashboardUi
import app.what.schedule.features.main.domain.models.LeaderboardUi
import app.what.schedule.features.main.domain.models.LearningAttemptUi
import app.what.schedule.features.main.domain.models.LearningModuleUi
import app.what.schedule.features.main.domain.models.LearningQuizQuestionUi
import app.what.schedule.features.main.domain.models.LearningQuizResultUi
import app.what.schedule.features.main.domain.models.LearningQuizUi
import app.what.schedule.features.main.domain.models.MonthTaskUi
import app.what.schedule.features.main.domain.models.NewsUi
import app.what.schedule.features.main.domain.models.PrivilegeUi
import app.what.schedule.features.main.domain.models.ProfileUi
import app.what.schedule.features.main.domain.models.RatingDetailUi
import app.what.schedule.features.main.domain.models.RatingMetricUi
import app.what.schedule.features.main.domain.models.ScenarioFormUi
import app.what.schedule.features.main.domain.models.ScenarioResultUi
import app.what.schedule.features.main.domain.models.SupportComposerUi
import app.what.schedule.features.main.domain.models.SupportTicketUi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt

class DealerBackendRepository(
    private val apiClient: DealerApiClient,
    private val settings: AppValues
) {
    suspend fun loadMainData(): BackendMainData = coroutineScope {
        Auditor.info("backend", "loadMainData started with persisted token")

        try {
            val profile = async { apiClient.getProfile() }
            val status = async { apiClient.getStatus() }
            val rating = async { safeRead("rating/detail") { apiClient.getRatingDetail() } ?: RatingDetailResponseDto() }
            val financial = async { safeRead("financial-effect") { apiClient.getFinancialEffect() } ?: FinancialEffectResponseDto() }
            val daily = async { safeRead("daily-results") { apiClient.getDailyResults() } ?: DailyResultResponseDto() }
            val tasks = async { safeRead("tasks") { apiClient.getTasks() } ?: emptyList() }
            val privileges = async { safeRead("privileges") { apiClient.getPrivileges() } ?: emptyList() }
            val dealerLeaderboard = async { safeRead("leaderboard/dealer") { apiClient.getLeaderboard(type = "dealer") } ?: emptyList() }
            val regionLeaderboard = async { safeRead("leaderboard/region") { apiClient.getLeaderboard(type = "region") } ?: emptyList() }
            val dealerPosition = async { safeRead("leaderboard/my-position/dealer") { apiClient.getMyPosition(type = "dealer") } ?: MyPositionResponseDto(type = "dealer") }
            val regionPosition = async { safeRead("leaderboard/my-position/region") { apiClient.getMyPosition(type = "region") } ?: MyPositionResponseDto(type = "region") }
            val learning = async { safeRead("learning/modules") { apiClient.getLearningModules() } ?: emptyList() }
            val learningAttempts = async { safeRead("learning/attempts") { apiClient.getLearningAttempts() } ?: emptyList() }
            val support = async { safeRead("support/tickets") { apiClient.getSupportTickets() } ?: emptyList() }
            val assistantHistory = async { safeRead("support/assistant/history") { apiClient.getSupportAssistantHistory() } ?: emptyList() }
            val news = async { safeRead("news") { apiClient.getNews() } ?: emptyList() }

            val profileValue = profile.await()
            val statusValue = status.await()
            val ratingValue = rating.await()
            val financialValue = financial.await()
            val dailyValue = daily.await()
            val tasksValue = tasks.await()
            val privilegesValue = privileges.await()
            val dealerLeaderboardValue = dealerLeaderboard.await()
            val regionLeaderboardValue = regionLeaderboard.await()
            val dealerPositionValue = dealerPosition.await()
            val regionPositionValue = regionPosition.await()
            val learningValue = learning.await()
            val learningAttemptsValue = learningAttempts.await()
            val supportValue = support.await()
            val assistantHistoryValue = assistantHistory.await()
            val newsValue = news.await()

            val learningModulesUi = learningValue.map { it.toUi() }

            BackendMainData(
                dashboard = statusValue.toDashboard(
                    profile = profileValue,
                    financial = financialValue,
                    daily = dailyValue,
                    tasks = tasksValue,
                    privileges = privilegesValue,
                    myPosition = dealerPositionValue
                ),
                ratingDetail = ratingValue.toUi(),
                dailyResults = dailyValue.toUi(),
                profile = profileValue.toUi(),
                news = newsValue.map { it.toUi() },
                learningModules = learningModulesUi,
                learningAttempts = learningAttemptsValue.map { it.toUi(learningModulesUi) },
                supportTickets = supportValue.map { it.toUi() },
                assistantHistory = assistantHistoryValue.map { it.toUi() },
                dealerLeaderboardItems = dealerLeaderboardValue
                    .map { it.toUi() }
                    .sortedWith(compareBy<LeaderboardUi> { it.position }.thenByDescending { it.totalPoints }),
                regionLeaderboardItems = regionLeaderboardValue
                    .map { it.toUi() }
                    .sortedWith(compareBy<LeaderboardUi> { it.position }.thenByDescending { it.totalPoints }),
                dealerRank = dealerPositionValue.position.takeIf { it > 0 },
                regionRank = regionPositionValue.position.takeIf { it > 0 }
            ).also {
                Auditor.info(
                    "backend",
                    "mapped main data: dailyEntries=${it.dailyResults.items.size}, news=${it.news.size}, modules=${it.learningModules.size}, attempts=${it.learningAttempts.size}, tickets=${it.supportTickets.size}, assistantHistory=${it.assistantHistory.size}, dealerTop=${it.dealerLeaderboardItems.size}, regionTop=${it.regionLeaderboardItems.size}"
                )
            }
        } catch (error: DealerUnauthorizedException) {
            clearSession()
            throw error
        }
    }

    private suspend fun <T> safeRead(
        label: String,
        block: suspend () -> T
    ): T? = try {
        block()
    } catch (error: DealerUnauthorizedException) {
        throw error
    } catch (error: Throwable) {
        Auditor.err("backend", "optional endpoint $label failed: ${error.message}", error)
        null
    }

    suspend fun calculateScenario(form: ScenarioFormUi): ScenarioResultUi {
        val volumeFact = form.volumeFactMln.trim().toDoubleOrZero()
        val dealsFact = form.dealsFact.trim().toDoubleOrZero()
        val shareFact = form.bankShareFactPercent.trim().toDoubleOrZero()
        val approved = form.approvedApplications.trim().toDoubleOrZero()
        val submitted = form.submittedApplications.trim().toDoubleOrZero().coerceAtLeast(1.0)

        val volumeIndex = ((volumeFact / 10.0) * 100.0).coerceAtMost(120.0)
        val dealsIndex = (dealsFact / 10.0) * 100.0
        val shareIndex = (shareFact / 50.0) * 100.0
        val conversionIndex = (approved / submitted) * 100.0

        val totalScore = 0.35 * volumeIndex +
            0.25 * dealsIndex +
            0.25 * shareIndex +
            0.15 * conversionIndex

        val normalizedScore = totalScore.coerceAtLeast(0.0)
        val level = when {
            normalizedScore >= 90.0 -> "Black"
            normalizedScore >= 70.0 -> "Gold"
            else -> "Silver"
        }
        val progressPercent = ((normalizedScore / 90.0) * 100.0).coerceIn(0.0, 100.0).roundToInt()

        return ScenarioResultUi(
            level = level,
            totalPoints = normalizedScore,
            progressPercent = progressPercent,
            volumeIndex = volumeIndex,
            dealsIndex = dealsIndex,
            shareIndex = shareIndex,
            conversionIndex = conversionIndex,
            monthlyTransitionEligible = normalizedScore >= 70.0,
            simulatedIncomeRub = ((normalizedScore / 100.0) * 540_000).toLong(),
            simulatedMortgageSavingRub = ((normalizedScore / 100.0) * 740_000).toLong()
        )
    }

    suspend fun submitDailyResults(form: DailyFormUi): DailyResultsUi {
        val response = apiClient.postDailyResults(
            DailyResultRequestDto(
                date = form.date,
                dealCount = form.dealCount.trim().toIntOrZero(),
                volumeRub = form.volumeRub.trim().toLongOrZero(),
                bankSharePercent = form.bankSharePercent.trim().toDoubleOrZero(),
                additionalProductsCount = form.additionalProductsCount.trim().toIntOrZero()
            )
        )
        return response.toUi()
    }

    suspend fun createSupportTicket(form: SupportComposerUi): SupportTicketUi =
        apiClient.createSupportTicket(
            SupportTicketRequestDto(
                subject = form.subject.trim(),
                message = form.message.trim(),
                category = form.category.takeIf { it.isNotBlank() },
                priority = form.priority.takeIf { it.isNotBlank() }
            )
        ).toUi()

    suspend fun askSupportAssistant(question: String): AssistantMessageUi =
        apiClient.askSupportAssistant(
            AssistantAskRequestDto(question = question.trim())
        ).toUi(question.trim())

    suspend fun getLearningQuiz(moduleId: String): LearningQuizUi =
        apiClient.getLearningQuiz(moduleId).toUi(
            moduleTitle = safeRead("learning/modules") { apiClient.getLearningModules() }
                ?.firstOrNull { it.id == moduleId }
                ?.title
                .orEmpty()
        )

    suspend fun submitLearningQuiz(quiz: LearningQuizUi): LearningQuizResultUi {
        val answers = quiz.questions.mapNotNull { question ->
            question.selectedAnswers.firstOrNull()?.let { selected ->
                val selectedIndex = question.options.indexOf(selected)
                if (selectedIndex < 0) return@mapNotNull null
                LearningAnswerRequestDto(
                    questionId = question.id,
                    selectedOptionIndex = selectedIndex
                )
            }
        }

        return apiClient.submitLearningQuiz(
            LearningQuizSubmitRequestDto(
                moduleId = quiz.moduleId,
                answers = answers
            )
        ).toUi()
    }

    suspend fun completeLearningModule(moduleId: String): LearningModuleUi =
        apiClient.completeLearningModule(moduleId).toUi()

    fun clearSession() {
        Auditor.warn("backend-auth", "clearing persisted backend session after unauthorized response")
        settings.dealerBackendToken.set(null)
        settings.dealerBackendResolvedBaseUrl.set(null)
        settings.dealerBackendEmployeeId.set(null)
        settings.dealerBackendSberId.set(null)
        settings.dealerBackendEmployeeName.set(null)
    }
}

data class BackendMainData(
    val dashboard: DashboardUi,
    val ratingDetail: RatingDetailUi,
    val dailyResults: DailyResultsUi,
    val profile: ProfileUi,
    val news: List<NewsUi>,
    val learningModules: List<LearningModuleUi>,
    val learningAttempts: List<LearningAttemptUi>,
    val supportTickets: List<SupportTicketUi>,
    val assistantHistory: List<AssistantMessageUi>,
    val dealerLeaderboardItems: List<LeaderboardUi>,
    val regionLeaderboardItems: List<LeaderboardUi>,
    val dealerRank: Int?,
    val regionRank: Int?
)

private fun ProfileResponseDto.toUi(): ProfileUi = ProfileUi(
    fullName = fullName,
    sberId = sberId,
    dealerCenterCode = dealerCenterCode,
    dealerCenterName = dealerCenterName,
    position = position,
    level = level.toLevelLabel(),
    phone = phone,
    email = email,
    registeredAt = registeredAt.take(10),
    role = null,
    employeeId = null
)

private fun RatingDetailResponseDto.toUi(): RatingDetailUi = RatingDetailUi(
    totalPoints = totalPoints,
    metrics = listOf(
        RatingMetricUi(
            title = "Объём",
            points = volumePoints,
            howCalculated = "Индекс = (факт объёма / план объёма) x 100, максимум 120.",
            howIncrease = "Увеличивайте профинансированный объём относительно плана."
        ),
        RatingMetricUi(
            title = "Сделки",
            points = dealsPoints,
            howCalculated = "Индекс = (факт сделок / план сделок) x 100.",
            howIncrease = "Увеличивайте количество сделок относительно плана."
        ),
        RatingMetricUi(
            title = "Доля банка",
            points = sharePoints,
            howCalculated = "Индекс = (факт доли / целевая доля) x 100.",
            howIncrease = "Повышайте долю сделок через банк."
        ),
        RatingMetricUi(
            title = "Конверсия",
            points = conversionPoints.takeIf { it > 0 } ?: additionalProductsPoints,
            howCalculated = "Индекс = (одобрено заявок / подано заявок) x 100.",
            howIncrease = "Повышайте качество заявок для роста одобрений."
        )
    )
)

private fun DailyResultResponseDto.toUi(): DailyResultsUi = DailyResultsUi(
    date = date,
    totalDeals = totalDeals,
    totalVolumeRub = totalVolumeRub,
    averageSharePercent = averageSharePercent,
    additionalProductsCount = additionalProductsCount,
    items = items.map { entry ->
        DailyEntryUi(
            id = entry.id,
            createdAt = entry.createdAt.take(16).replace('T', ' '),
            dealCount = entry.dealCount,
            volumeRub = entry.volumeRub,
            bankSharePercent = entry.bankSharePercent,
            additionalProductsCount = entry.additionalProductsCount
        )
    }
)

private fun StatusResponseDto.normalizedLevel(): String = when (level.uppercase(Locale.getDefault())) {
    "PLATINUM" -> "BLACK"
    else -> level.uppercase(Locale.getDefault())
}

private fun StatusResponseDto.resolveNextLevelThreshold(): Int? = when (normalizedLevel()) {
    "SILVER" -> 70
    "GOLD" -> 90
    else -> null
}

private fun StatusResponseDto.resolveProgressPercent(): Int {
    val threshold = resolveNextLevelThreshold()
    return if (threshold == null) 100 else ((totalPoints.toDouble() / threshold) * 100.0).coerceIn(0.0, 100.0).roundToInt()
}

private fun StatusResponseDto.toDashboard(
    profile: ProfileResponseDto,
    financial: FinancialEffectResponseDto,
    daily: DailyResultResponseDto,
    tasks: List<TaskResponseDto>,
    privileges: List<PrivilegeItemResponseDto>,
    myPosition: MyPositionResponseDto
): DashboardUi {
    val nextLevelThreshold = resolveNextLevelThreshold()
    val totalPointsToNext = nextLevelThreshold?.let { (it - totalPoints).coerceAtLeast(0) }

    return DashboardUi(
        employeeInitials = profile.fullName.toInitials(),
        currentPoints = totalPoints,
        monthPoints = null,
        daysToMonthEnd = LocalDate.now().lengthOfMonth() - LocalDate.now().dayOfMonth + 1,
        currentStatus = normalizedLevel().toLevelLabel(),
        nextStatus = normalizedLevel().toNextLevelLabel(),
        progressCurrent = totalPoints,
        progressTarget = nextLevelThreshold,
        pointsToNextLevel = totalPointsToNext,
        progressPercent = resolveProgressPercent(),
        yearlyIncomeGrowthRub = financial.breakdown.annualExtraIncomeRub.toSafeInt(),
        mortgageSavingsRub = financial.breakdown.mortgageSavingRub.toSafeInt(),
        cashbackRub = (financial.breakdown.cashbackDmsPremierRub / 2).toSafeInt(),
        dmsValueRub = (financial.breakdown.cashbackDmsPremierRub / 2).toSafeInt(),
        totalBenefitRub = financial.headlineRub.toSafeInt(),
        dailyDeals = daily.totalDeals,
        dailyCreditVolumeMln = daily.totalVolumeRub / 1_000_000.0,
        dailyExtraProducts = daily.additionalProductsCount,
        rank = myPosition.position.takeIf { it > 0 },
        rankDeltaWeek = null,
        monthlyTasks = tasks.map { it.toUi() },
        activePrivileges = privileges.filter { it.status == "ACTIVE" }.map { it.toUi() },
        lockedPrivileges = privileges.filter { it.status != "ACTIVE" }.map { it.toUi() }
    )
}

private fun TaskResponseDto.toUi(): MonthTaskUi = MonthTaskUi(
    id = id,
    title = title,
    metric = metricType.toTaskMetricLabel(),
    reward = "+$pointsReward баллов",
    progress = "$currentValue / $targetValue",
    progressPercent = progressPercent.roundToInt(),
    deadline = deadline,
    description = description,
    completed = completed
)

private fun PrivilegeItemResponseDto.toUi(): PrivilegeUi = PrivilegeUi(
    id = id,
    title = name,
    description = description,
    financialEffectRub = financialEffectRub.toSafeInt(),
    status = status.toPrivilegeLabel(),
    requiredLevel = levelRequired.toLevelLabel()
)

private fun LeaderboardItemDto.toUi(): LeaderboardUi = LeaderboardUi(
    position = position,
    fullName = fullName,
    dealerCenterCode = dealerCenterCode.ifBlank { "Не указан" },
    totalPoints = totalPoints,
    level = level.toLevelLabel()
)

private fun LearningModuleResponseDto.toUi(): LearningModuleUi = LearningModuleUi(
    id = id,
    title = title,
    description = description,
    pointsReward = pointsReward,
    completed = completed,
    durationMinutes = durationMin,
    format = if (videoUrl.isNullOrBlank()) null else "Видео",
    progressPercent = lastScorePercent,
    quizAvailable = true,
    category = quizPassPercent?.let { "Порог прохождения: $it%" }
)

private fun LearningAttemptResponseDto.toUi(modules: List<LearningModuleUi>): LearningAttemptUi = LearningAttemptUi(
    id = attemptId,
    moduleId = moduleId,
    moduleTitle = modules.firstOrNull { it.id == moduleId }?.title ?: "Модуль",
    score = scorePercent,
    totalQuestions = 0,
    correctAnswers = 0,
    passed = passed,
    pointsAwarded = awardedPoints,
    completedAt = attemptedAt.take(16).replace('T', ' ')
)

private fun LearningQuizResponseDto.toUi(moduleTitle: String): LearningQuizUi = LearningQuizUi(
    moduleId = moduleId,
    moduleTitle = moduleTitle.ifBlank { "Квиз" },
    description = passScorePercent?.let { "Для прохождения нужно $it%" },
    questions = questions.sortedBy { it.orderNo ?: Int.MAX_VALUE }.map { it.toUi() }
)

private fun LearningQuizQuestionResponseDto.toUi(): LearningQuizQuestionUi = LearningQuizQuestionUi(
    id = id,
    question = questionText,
    options = options
)

private fun LearningQuizSubmitResponseDto.toUi(): LearningQuizResultUi = LearningQuizResultUi(
    score = scorePercent,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers,
    passed = passed,
    pointsAwarded = awardedPoints,
    summary = if (moduleCompleted) "Модуль завершён" else null,
    completedAt = LocalDate.now().toString()
)

private fun SupportTicketResponseDto.toUi(): SupportTicketUi = SupportTicketUi(
    id = id,
    subject = subject,
    message = message,
    status = status.formatLabel(),
    createdAt = createdAt.take(16).replace('T', ' '),
    updatedAt = updatedAt?.take(16)?.replace('T', ' '),
    category = category?.formatLabel(),
    priority = priority?.formatLabel(),
    resolution = resolution
)

private fun AssistantHistoryItemDto.toUi(): AssistantMessageUi = AssistantMessageUi(
    id = id,
    question = question,
    answer = answer,
    createdAt = createdAt.take(16).replace('T', ' ')
)

private fun AssistantAskResponseDto.toUi(question: String): AssistantMessageUi = AssistantMessageUi(
    id = createdAt.ifBlank { question },
    question = question,
    answer = answer,
    createdAt = createdAt.take(16).replace('T', ' '),
    suggestions = suggestions,
    sources = sources
)

private fun NewsResponseDto.toUi(): NewsUi = NewsUi(
    id = id,
    title = title,
    content = content,
    publishedAt = publishedAt.take(10),
    targetLevel = targetLevel?.toLevelLabel(),
    summary = summary,
    category = category?.formatLabel(),
    tags = tags
)

private fun String.toInitials(): String = split(" ")
    .filter { it.isNotBlank() }
    .take(2)
    .joinToString("") { it.first().uppercase(Locale.getDefault()) }

private fun String.toLevelLabel(): String = when (uppercase(Locale.getDefault())) {
    "SILVER" -> "Silver"
    "GOLD" -> "Gold"
    "BLACK", "PLATINUM" -> "Black"
    else -> replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

private fun String.toNextLevelLabel(): String? = when (uppercase(Locale.getDefault())) {
    "SILVER" -> "Gold"
    "GOLD" -> "Black"
    else -> null
}

private fun String.toPrivilegeLabel(): String = when (uppercase(Locale.getDefault())) {
    "ACTIVE" -> "Активна"
    "LOCKED" -> "Заблокирована"
    "COMING_SOON" -> "Скоро"
    else -> formatLabel()
}

private fun String.toTaskMetricLabel(): String = when (uppercase(Locale.getDefault())) {
    "VOLUME", "VOLUME_RUB" -> "Объём"
    "DEALS", "DEAL_COUNT" -> "Сделки"
    "SHARE", "BANK_SHARE", "BANK_SHARE_PERCENT" -> "Доля банка"
    "ADDITIONAL_PRODUCTS", "ADDITIONAL_PRODUCTS_COUNT" -> "Доп. продукты"
    "CONVERSION" -> "Конверсия"
    else -> formatLabel()
}

private fun String.formatLabel(): String = lowercase(Locale.getDefault())
    .split('_', '-', ' ')
    .filter { it.isNotBlank() }
    .joinToString(" ") { token ->
        token.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        }
    }

private fun Long.toSafeInt(): Int? = takeIf { it <= Int.MAX_VALUE }?.toInt()

private fun String.toIntOrZero(): Int = toIntOrNull() ?: 0

private fun String.toLongOrZero(): Long = toLongOrNull() ?: 0L

private fun String.toDoubleOrZero(): Double = replace(',', '.').toDoubleOrNull() ?: 0.0
