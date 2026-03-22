package app.what.schedule.features.main.domain.models

sealed interface MainEvent {
    data object Init : MainEvent
    data object RetryLoad : MainEvent
    data class SelectTab(val tab: MainTab) : MainEvent
    data class SelectLeaderboardScope(val scope: LeaderboardScope) : MainEvent

    data class UpdateCalculatorVolumeFactMln(val value: String) : MainEvent
    data class UpdateCalculatorDealsFact(val value: String) : MainEvent
    data class UpdateCalculatorShareFact(val value: String) : MainEvent
    data class UpdateCalculatorApproved(val value: String) : MainEvent
    data class UpdateCalculatorSubmitted(val value: String) : MainEvent

    data class UpdateDailyDate(val value: String) : MainEvent
    data class UpdateDailyDeals(val value: String) : MainEvent
    data class UpdateDailyVolume(val value: String) : MainEvent
    data class UpdateDailyShare(val value: String) : MainEvent
    data class UpdateDailyProducts(val value: String) : MainEvent
    data object SubmitDailyResults : MainEvent

    data class UpdateSupportSubject(val value: String) : MainEvent
    data class UpdateSupportMessage(val value: String) : MainEvent
    data class UpdateSupportCategory(val value: String) : MainEvent
    data class UpdateSupportPriority(val value: String) : MainEvent
    data class UpdateAssistantQuestion(val value: String) : MainEvent
    data object AskSupportAssistant : MainEvent
    data class ApplyAssistantSuggestion(val value: String) : MainEvent
    data object SubmitSupportTicket : MainEvent

    data class OpenLearningQuiz(val moduleId: String) : MainEvent
    data class UpdateLearningQuizAnswer(val questionId: String, val answer: String) : MainEvent
    data object SubmitLearningQuiz : MainEvent
    data object CloseLearningQuiz : MainEvent
    data class CompleteLearningModule(val moduleId: String) : MainEvent

    data object Logout : MainEvent
}
