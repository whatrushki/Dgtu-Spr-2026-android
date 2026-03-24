package app.what.schedule.features.main.domain

import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.foundation.services.AppLogger.Companion.Auditor
import app.what.schedule.data.remote.dealer.DealerAuthRepository
import app.what.schedule.data.remote.dealer.DealerBackendRepository
import app.what.schedule.data.remote.dealer.DealerUnauthorizedException
import app.what.schedule.features.main.domain.models.DailyFormUi
import app.what.schedule.features.main.domain.models.MainAction
import app.what.schedule.features.main.domain.models.MainEvent
import app.what.schedule.features.main.domain.models.MainState
import app.what.schedule.features.main.domain.models.SupportComposerUi
import kotlinx.coroutines.launch

class MainController(
    private val backendRepository: DealerBackendRepository,
    private val authRepository: DealerAuthRepository
) : UIController<MainState, MainAction, MainEvent>(
    MainState()
) {
    override fun obtainEvent(viewEvent: MainEvent) = when (viewEvent) {
        MainEvent.Init, MainEvent.RetryLoad -> loadData()
        is MainEvent.SelectTab -> updateState { copy(selectedTab = viewEvent.tab) }
        is MainEvent.SelectLeaderboardScope -> updateState { copy(selectedLeaderboardScope = viewEvent.scope) }

        is MainEvent.UpdateCalculatorVolumeFactMln -> updateState { copy(calculatorForm = calculatorForm.copy(volumeFactMln = viewEvent.value, errorMessage = null)) }
        is MainEvent.UpdateCalculatorDealsFact -> updateState { copy(calculatorForm = calculatorForm.copy(dealsFact = viewEvent.value, errorMessage = null)) }
        is MainEvent.UpdateCalculatorShareFact -> updateState { copy(calculatorForm = calculatorForm.copy(bankShareFactPercent = viewEvent.value, errorMessage = null)) }
        is MainEvent.UpdateCalculatorApproved -> updateState { copy(calculatorForm = calculatorForm.copy(approvedApplications = viewEvent.value, errorMessage = null)) }
        is MainEvent.UpdateCalculatorSubmitted -> updateState { copy(calculatorForm = calculatorForm.copy(submittedApplications = viewEvent.value, errorMessage = null)) }

        is MainEvent.UpdateDailyDate -> updateState { copy(dailyForm = dailyForm.copy(date = viewEvent.value, errorMessage = null, successMessage = null)) }
        is MainEvent.UpdateDailyDeals -> updateState { copy(dailyForm = dailyForm.copy(dealCount = viewEvent.value, errorMessage = null, successMessage = null)) }
        is MainEvent.UpdateDailyVolume -> updateState { copy(dailyForm = dailyForm.copy(volumeRub = viewEvent.value, errorMessage = null, successMessage = null)) }
        is MainEvent.UpdateDailyShare -> updateState { copy(dailyForm = dailyForm.copy(bankSharePercent = viewEvent.value, errorMessage = null, successMessage = null)) }
        is MainEvent.UpdateDailyProducts -> updateState { copy(dailyForm = dailyForm.copy(additionalProductsCount = viewEvent.value, errorMessage = null, successMessage = null)) }
        MainEvent.SubmitDailyResults -> submitDailyResults()

        is MainEvent.UpdateSupportSubject -> updateState { copy(supportComposer = supportComposer.copy(subject = viewEvent.value, errorMessage = null, successMessage = null)) }
        is MainEvent.UpdateSupportMessage -> updateState { copy(supportComposer = supportComposer.copy(message = viewEvent.value, errorMessage = null, successMessage = null)) }
        is MainEvent.UpdateSupportCategory -> updateState { copy(supportComposer = supportComposer.copy(category = viewEvent.value, errorMessage = null, successMessage = null)) }
        is MainEvent.UpdateSupportPriority -> updateState { copy(supportComposer = supportComposer.copy(priority = viewEvent.value, errorMessage = null, successMessage = null)) }
        is MainEvent.UpdateAssistantQuestion -> updateState { copy(supportComposer = supportComposer.copy(assistantQuestion = viewEvent.value, assistantErrorMessage = null, successMessage = null)) }
        is MainEvent.ApplyAssistantSuggestion -> updateState {
            copy(
                supportComposer = supportComposer.copy(
                    assistantQuestion = viewEvent.value,
                    message = if (supportComposer.message.isBlank()) viewEvent.value else supportComposer.message,
                    assistantErrorMessage = null
                )
            )
        }
        MainEvent.AskSupportAssistant -> askSupportAssistant()
        MainEvent.SubmitSupportTicket -> submitSupportTicket()

        is MainEvent.OpenLearningQuiz -> openLearningQuiz(viewEvent.moduleId)
        is MainEvent.UpdateLearningQuizAnswer -> updateLearningQuizAnswer(viewEvent.questionId, viewEvent.answer)
        MainEvent.SubmitLearningQuiz -> submitLearningQuiz()
        MainEvent.CloseLearningQuiz -> updateState { copy(learningQuiz = null) }
        is MainEvent.CompleteLearningModule -> completeLearningModule(viewEvent.moduleId)

        MainEvent.Logout -> logout()
    }

    private fun loadData() {
        if (viewState.isLoading) return

        viewModelScope.launch {
            Auditor.info("main", "loading backend data")
            updateState { copy(isLoading = true, errorMessage = null) }

            runCatching { backendRepository.loadMainData() }
                .onSuccess { data ->
                    updateState {
                        copy(
                            dashboard = data.dashboard,
                            ratingDetail = data.ratingDetail,
                            dailyResults = data.dailyResults,
                            profile = data.profile,
                            news = data.news,
                            learningModules = data.learningModules,
                            learningAttempts = data.learningAttempts,
                            supportTickets = data.supportTickets,
                            assistantHistory = data.assistantHistory,
                            dealerLeaderboardItems = data.dealerLeaderboardItems,
                            regionLeaderboardItems = data.regionLeaderboardItems,
                            dealerRank = data.dealerRank,
                            regionRank = data.regionRank,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    handleMainFailure(error) {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Не удалось загрузить данные"
                        )
                    }
                }
        }
    }

    private fun submitDailyResults() {
        if (viewState.dailyForm.isSubmitting) return
        if (viewState.dailyForm.date.isBlank()) {
            updateState { copy(dailyForm = dailyForm.copy(errorMessage = "Укажите дату")) }
            return
        }

        viewModelScope.launch {
            updateState {
                copy(
                    dailyForm = dailyForm.copy(
                        isSubmitting = true,
                        errorMessage = null,
                        successMessage = null
                    )
                )
            }

            runCatching { backendRepository.submitDailyResults(viewState.dailyForm) }
                .onSuccess { dailyResults ->
                    updateState {
                        copy(
                            dailyResults = dailyResults,
                            dashboard = dashboard.copy(
                                dailyDeals = dailyResults.totalDeals,
                                dailyCreditVolumeMln = dailyResults.totalVolumeRub?.div(1_000_000.0),
                                dailyExtraProducts = dailyResults.additionalProductsCount
                            ),
                            dailyForm = DailyFormUi(
                                date = dailyForm.date,
                                successMessage = "Результаты дня сохранены"
                            )
                        )
                    }
                    loadData()
                }
                .onFailure { error ->
                    handleMainFailure(error) {
                        copy(
                            dailyForm = dailyForm.copy(
                                isSubmitting = false,
                                errorMessage = error.message ?: "Не удалось сохранить результаты дня"
                            )
                        )
                    }
                }
        }
    }

    private fun askSupportAssistant() {
        if (viewState.supportComposer.isAssistantLoading) return
        val question = viewState.supportComposer.assistantQuestion.trim()
        if (question.isBlank()) {
            updateState {
                copy(
                    supportComposer = supportComposer.copy(
                        assistantErrorMessage = "Введите вопрос для нейроассистента"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            updateState {
                copy(
                    supportComposer = supportComposer.copy(
                        isAssistantLoading = true,
                        assistantErrorMessage = null,
                        successMessage = null
                    )
                )
            }

            runCatching { backendRepository.askSupportAssistant(question) }
                .onSuccess { message ->
                    updateState {
                        copy(
                            assistantHistory = assistantHistory + message,
                            supportComposer = supportComposer.copy(
                                isAssistantLoading = false,
                                assistantQuestion = "",
                                assistantErrorMessage = null
                            )
                        )
                    }
                }
                .onFailure { error ->
                    handleMainFailure(error) {
                        copy(
                            supportComposer = supportComposer.copy(
                                isAssistantLoading = false,
                                assistantErrorMessage = error.message ?: "Не удалось получить ответ нейроассистента"
                            )
                        )
                    }
                }
        }
    }

    private fun submitSupportTicket() {
        if (viewState.supportComposer.isSubmitting) return
        if (viewState.supportComposer.subject.isBlank() || viewState.supportComposer.message.isBlank()) {
            updateState {
                copy(
                    supportComposer = supportComposer.copy(
                        errorMessage = "Заполните тему и сообщение"
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            updateState {
                copy(
                    supportComposer = supportComposer.copy(
                        isSubmitting = true,
                        errorMessage = null,
                        successMessage = null
                    )
                )
            }

            runCatching { backendRepository.createSupportTicket(viewState.supportComposer) }
                .onSuccess { ticket ->
                    updateState {
                        copy(
                            supportTickets = listOf(ticket) + supportTickets,
                            supportComposer = SupportComposerUi(
                                category = supportComposer.category,
                                priority = supportComposer.priority,
                                successMessage = "Обращение отправлено"
                            )
                        )
                    }
                }
                .onFailure { error ->
                    handleMainFailure(error) {
                        copy(
                            supportComposer = supportComposer.copy(
                                isSubmitting = false,
                                errorMessage = error.message ?: "Не удалось отправить обращение"
                            )
                        )
                    }
                }
        }
    }

    private fun openLearningQuiz(moduleId: String) {
        if (viewState.learningQuiz?.isLoading == true) return

        viewModelScope.launch {
            updateState {
                copy(
                    learningQuiz = learningQuiz?.copy(isLoading = true, errorMessage = null)
                        ?: app.what.schedule.features.main.domain.models.LearningQuizUi(
                            moduleId = moduleId,
                            moduleTitle = "",
                            isLoading = true
                        )
                )
            }

            runCatching { backendRepository.getLearningQuiz(moduleId) }
                .onSuccess { quiz ->
                    updateState { copy(learningQuiz = quiz) }
                }
                .onFailure { error ->
                    handleMainFailure(error) {
                        copy(
                            learningQuiz = learningQuiz?.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Не удалось загрузить квиз"
                            )
                        )
                    }
                }
        }
    }

    private fun updateLearningQuizAnswer(questionId: String, answer: String) {
        val quiz = viewState.learningQuiz ?: return
        updateState {
            copy(
                learningQuiz = quiz.copy(
                    questions = quiz.questions.map { question ->
                        if (question.id == questionId) {
                            val nextAnswers = if (question.multiple) {
                                if (answer in question.selectedAnswers) {
                                    question.selectedAnswers - answer
                                } else {
                                    question.selectedAnswers + answer
                                }
                            } else {
                                listOf(answer)
                            }
                            question.copy(selectedAnswers = nextAnswers)
                        } else {
                            question
                        }
                    },
                    errorMessage = null
                )
            )
        }
    }

    private fun submitLearningQuiz() {
        val quiz = viewState.learningQuiz ?: return
        if (quiz.isSubmitting || quiz.questions.any { it.selectedAnswers.isEmpty() }) {
            if (quiz.questions.any { it.selectedAnswers.isEmpty() }) {
                updateState {
                    copy(
                        learningQuiz = quiz.copy(
                            errorMessage = "Ответьте на все вопросы перед отправкой"
                        )
                    )
                }
            }
            return
        }

        viewModelScope.launch {
            updateState {
                copy(
                    learningQuiz = quiz.copy(
                        isSubmitting = true,
                        errorMessage = null
                    )
                )
            }

            runCatching { backendRepository.submitLearningQuiz(viewState.learningQuiz!!) }
                .onSuccess { result ->
                    updateState {
                        copy(
                            learningQuiz = learningQuiz?.copy(
                                isSubmitting = false,
                                result = result
                            )
                        )
                    }
                    loadData()
                }
                .onFailure { error ->
                    handleMainFailure(error) {
                        copy(
                            learningQuiz = learningQuiz?.copy(
                                isSubmitting = false,
                                errorMessage = error.message ?: "Не удалось отправить квиз"
                            )
                        )
                    }
                }
        }
    }

    private fun completeLearningModule(moduleId: String) {
        if (viewState.pendingLearningIds.contains(moduleId)) return

        viewModelScope.launch {
            updateState { copy(pendingLearningIds = pendingLearningIds + moduleId) }

            runCatching { backendRepository.completeLearningModule(moduleId) }
                .onSuccess { completed ->
                    updateState {
                        copy(
                            pendingLearningIds = pendingLearningIds - moduleId,
                            learningModules = learningModules.map { module ->
                                if (module.id == moduleId) completed else module
                            }
                        )
                    }
                }
                .onFailure { error ->
                    handleMainFailure(error) {
                        copy(
                            pendingLearningIds = pendingLearningIds - moduleId,
                            errorMessage = error.message ?: "Не удалось обновить модуль"
                        )
                    }
                }
        }
    }

    private fun logout() {
        authRepository.logout()
        setAction(MainAction.OpenAuth)
    }

    private inline fun handleMainFailure(
        error: Throwable,
        crossinline stateUpdate: MainState.() -> MainState
    ) {
        Auditor.err("main", "main flow error: ${error.message}", error)
        if (error is DealerUnauthorizedException) {
            authRepository.logout()
            updateState { stateUpdate() }
            setAction(MainAction.OpenAuth)
            return
        }
        updateState { stateUpdate() }
    }
}
