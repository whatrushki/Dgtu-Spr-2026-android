package app.what.schedule.features.main.domain.models

import java.time.LocalDate

data class MainState(
    val selectedTab: MainTab = MainTab.Status,
    val dashboard: DashboardUi = DashboardUi(),
    val ratingDetail: RatingDetailUi = RatingDetailUi(),
    val dailyResults: DailyResultsUi = DailyResultsUi(),
    val sources: DashboardSources = DashboardSources(),
    val profile: ProfileUi = ProfileUi(),
    val news: List<NewsUi> = emptyList(),
    val learningModules: List<LearningModuleUi> = emptyList(),
    val learningAttempts: List<LearningAttemptUi> = emptyList(),
    val learningQuiz: LearningQuizUi? = null,
    val supportTickets: List<SupportTicketUi> = emptyList(),
    val assistantHistory: List<AssistantMessageUi> = emptyList(),
    val dealerLeaderboardItems: List<LeaderboardUi> = emptyList(),
    val regionLeaderboardItems: List<LeaderboardUi> = emptyList(),
    val selectedLeaderboardScope: LeaderboardScope = LeaderboardScope.Dealer,
    val dealerRank: Int? = null,
    val regionRank: Int? = null,
    val calculatorForm: ScenarioFormUi = ScenarioFormUi(),
    val dailyForm: DailyFormUi = DailyFormUi(),
    val supportComposer: SupportComposerUi = SupportComposerUi(),
    val pendingLearningIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class LeaderboardScope {
    Dealer,
    Region
}

enum class MainTab {
    Status,
    Rating,
    Calculator,
    Tasks,
    DailyResults,
    Privileges,
    FinancialEffect,
    Leaderboard,
    Learning,
    Support,
    Profile,
    News
}

data class DashboardUi(
    val employeeInitials: String? = null,
    val currentPoints: Int? = null,
    val monthPoints: Int? = null,
    val daysToMonthEnd: Int? = null,
    val currentStatus: String? = null,
    val nextStatus: String? = null,
    val progressCurrent: Int? = null,
    val progressTarget: Int? = null,
    val pointsToNextLevel: Int? = null,
    val progressPercent: Int? = null,
    val yearlyIncomeGrowthRub: Int? = null,
    val mortgageSavingsRub: Int? = null,
    val cashbackRub: Int? = null,
    val dmsValueRub: Int? = null,
    val totalBenefitRub: Int? = null,
    val dailyDeals: Int? = null,
    val dailyCreditVolumeMln: Double? = null,
    val dailyExtraProducts: Int? = null,
    val rank: Int? = null,
    val rankDeltaWeek: Int? = null,
    val levelTransitionRule: String = "Переход уровня проверяется 1 раз в месяц при достижении порога.",
    val monthlyTasks: List<MonthTaskUi> = emptyList(),
    val activePrivileges: List<PrivilegeUi> = emptyList(),
    val lockedPrivileges: List<PrivilegeUi> = emptyList()
)

data class RatingDetailUi(
    val totalPoints: Int? = null,
    val metrics: List<RatingMetricUi> = listOf(
        RatingMetricUi(
            title = "Объем",
            points = null,
            howCalculated = "Индекс = (факт объема / план объема) x 100, максимум 120.",
            howIncrease = "Увеличивайте профинансированный объем относительно плана."
        ),
        RatingMetricUi(
            title = "Сделки",
            points = null,
            howCalculated = "Индекс = (факт сделок / план сделок) x 100.",
            howIncrease = "Увеличивайте количество сделок относительно плана."
        ),
        RatingMetricUi(
            title = "Доля банка",
            points = null,
            howCalculated = "Индекс = (факт доли / целевая доля) x 100.",
            howIncrease = "Повышайте долю сделок через банк."
        ),
        RatingMetricUi(
            title = "Конверсия",
            points = null,
            howCalculated = "Индекс = (одобрено заявок / подано заявок) x 100.",
            howIncrease = "Повышайте качество заявок для роста одобрений."
        )
    )
)

data class RatingMetricUi(
    val title: String,
    val points: Int?,
    val howCalculated: String,
    val howIncrease: String
)

data class DashboardSources(
    val status: String = "/api/v1/status",
    val ratingDetail: String = "/api/v1/rating/detail",
    val financialEffect: String = "/api/v1/financial-effect",
    val dailyResults: String = "/api/v1/daily-results",
    val tasks: String = "/api/v1/tasks",
    val leaderboard: String = "/api/v1/leaderboard",
    val learningModules: String = "/api/v1/learning/modules",
    val learningQuiz: String = "/api/v1/learning/quiz",
    val learningAttempts: String = "/api/v1/learning/attempts",
    val supportTickets: String = "/api/v1/support/tickets",
    val supportAssistant: String = "/api/v1/support/assistant/ask",
    val news: String = "/api/v1/news"
)

data class MonthTaskUi(
    val id: String? = null,
    val title: String,
    val reward: String? = null,
    val progress: String? = null,
    val progressPercent: Int? = null,
    val deadline: String? = null,
    val description: String? = null,
    val completed: Boolean = false
)

data class PrivilegeUi(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val financialEffectRub: Int? = null,
    val status: String? = null,
    val requiredLevel: String? = null
)

data class ProfileUi(
    val fullName: String? = null,
    val sberId: String? = null,
    val dealerCenterCode: String? = null,
    val dealerCenterName: String? = null,
    val position: String? = null,
    val level: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val registeredAt: String? = null,
    val employeeId: String? = null,
    val role: String? = null
)

data class NewsUi(
    val id: String,
    val title: String,
    val content: String,
    val publishedAt: String,
    val targetLevel: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val tags: List<String> = emptyList()
)

data class LearningModuleUi(
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val completed: Boolean,
    val difficulty: String? = null,
    val durationMinutes: Int? = null,
    val format: String? = null,
    val progressPercent: Int? = null,
    val quizAvailable: Boolean = false,
    val category: String? = null
)

data class LearningAttemptUi(
    val id: String,
    val moduleId: String,
    val moduleTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val passed: Boolean,
    val pointsAwarded: Int,
    val completedAt: String
)

data class LearningQuizUi(
    val moduleId: String,
    val moduleTitle: String,
    val description: String? = null,
    val questions: List<LearningQuizQuestionUi> = emptyList(),
    val timeLimitMinutes: Int? = null,
    val attemptsLeft: Int? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val result: LearningQuizResultUi? = null
)

data class LearningQuizQuestionUi(
    val id: String,
    val question: String,
    val options: List<String>,
    val multiple: Boolean = false,
    val selectedAnswers: List<String> = emptyList(),
    val explanation: String? = null
)

data class LearningQuizResultUi(
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val passed: Boolean,
    val pointsAwarded: Int,
    val summary: String? = null,
    val completedAt: String
)

data class SupportTicketUi(
    val id: String,
    val subject: String,
    val message: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String? = null,
    val category: String? = null,
    val priority: String? = null,
    val resolution: String? = null
)

data class AssistantMessageUi(
    val id: String,
    val question: String,
    val answer: String,
    val createdAt: String,
    val suggestions: List<String> = emptyList(),
    val sources: List<String> = emptyList()
)

data class LeaderboardUi(
    val position: Int,
    val fullName: String,
    val dealerCenterCode: String,
    val totalPoints: Int,
    val level: String
)

data class DailyResultsUi(
    val date: String? = null,
    val totalDeals: Int? = null,
    val totalVolumeRub: Long? = null,
    val averageSharePercent: Double? = null,
    val additionalProductsCount: Int? = null,
    val items: List<DailyEntryUi> = emptyList()
)

data class DailyEntryUi(
    val id: String,
    val createdAt: String,
    val dealCount: Int,
    val volumeRub: Long,
    val bankSharePercent: Double,
    val additionalProductsCount: Int
)

data class ScenarioFormUi(
    val volumeFactMln: String = "",
    val dealsFact: String = "",
    val bankShareFactPercent: String = "",
    val approvedApplications: String = "",
    val submittedApplications: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val result: ScenarioResultUi? = null
)

data class ScenarioResultUi(
    val level: String,
    val totalPoints: Double,
    val progressPercent: Int,
    val volumeIndex: Double,
    val dealsIndex: Double,
    val shareIndex: Double,
    val conversionIndex: Double,
    val monthlyTransitionEligible: Boolean,
    val simulatedIncomeRub: Long,
    val simulatedMortgageSavingRub: Long
)

data class DailyFormUi(
    val date: String = LocalDate.now().toString(),
    val dealCount: String = "",
    val volumeRub: String = "",
    val bankSharePercent: String = "",
    val additionalProductsCount: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class SupportComposerUi(
    val subject: String = "",
    val message: String = "",
    val category: String = "GENERAL",
    val priority: String = "NORMAL",
    val assistantQuestion: String = "",
    val isSubmitting: Boolean = false,
    val isAssistantLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val assistantErrorMessage: String? = null
)
