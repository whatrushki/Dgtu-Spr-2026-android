package app.what.schedule.features.main.presentation

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import app.what.schedule.features.main.domain.models.MainEvent
import app.what.schedule.features.main.domain.models.MainState
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.MainChrome
import app.what.schedule.features.main.presentation.screens.calculator.CalculatorScreen
import app.what.schedule.features.main.presentation.screens.daily.DailyResultsScreen
import app.what.schedule.features.main.presentation.screens.financial.FinancialEffectScreen
import app.what.schedule.features.main.presentation.screens.home.HomeScreen
import app.what.schedule.features.main.presentation.screens.leaderboard.LeaderboardScreen
import app.what.schedule.features.main.presentation.screens.learning.LearningScreen
import app.what.schedule.features.main.presentation.screens.news.NewsScreen
import app.what.schedule.features.main.presentation.screens.profile.ProfileScreen
import app.what.schedule.features.main.presentation.screens.privileges.PrivilegesScreen
import app.what.schedule.features.main.presentation.screens.statistics.StatisticsScreen
import app.what.schedule.features.main.presentation.screens.support.SupportScreen
import app.what.schedule.features.main.presentation.screens.tasks.TasksScreen

@Composable
fun MainView(
    state: MainState,
    listener: (MainEvent) -> Unit
) {
    val swipeTabs = remember {
        listOf(
            MainTab.Status,
            MainTab.Rating,
            MainTab.Privileges,
            MainTab.Learning
        )
    }
    val selectedIndex = swipeTabs.indexOf(state.selectedTab)
    val isSwipeTab = selectedIndex >= 0
    val pagerState = rememberPagerState(
        initialPage = selectedIndex.coerceAtLeast(0),
        pageCount = { swipeTabs.size }
    )

    LaunchedEffect(isSwipeTab, selectedIndex) {
        if (isSwipeTab && pagerState.currentPage != selectedIndex) {
            pagerState.scrollToPage(selectedIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage, isSwipeTab, state.selectedTab) {
        if (!isSwipeTab) return@LaunchedEffect
        val pagedTab = swipeTabs[pagerState.currentPage]
        if (pagedTab != state.selectedTab) {
            listener(MainEvent.SelectTab(pagedTab))
        }
    }

    MainChrome(
        selectedTab = state.selectedTab,
        title = tabTitle(state.selectedTab),
        onSearchClick = { listener(MainEvent.SelectTab(MainTab.Support)) },
        onOpenNews = { listener(MainEvent.SelectTab(MainTab.News)) },
        onOpenProfile = { listener(MainEvent.SelectTab(MainTab.Profile)) },
        onSelectRootTab = { listener(MainEvent.SelectTab(it)) }
    ) { contentPadding ->
        if (isSwipeTab) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                MainTabContent(
                    tab = swipeTabs[page],
                    state = state,
                    contentPadding = contentPadding,
                    listener = listener
                )
            }
        } else {
            MainTabContent(
                tab = state.selectedTab,
                state = state,
                contentPadding = contentPadding,
                listener = listener
            )
        }
    }
}

@Composable
private fun MainTabContent(
    tab: MainTab,
    state: MainState,
    contentPadding: androidx.compose.foundation.layout.PaddingValues,
    listener: (MainEvent) -> Unit
) {
    when (tab) {
        MainTab.Status -> HomeScreen(
            contentPadding = contentPadding,
            dashboard = state.dashboard,
            sources = state.sources,
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            onRetry = { listener(MainEvent.RetryLoad) },
            onOpenCalculator = { listener(MainEvent.SelectTab(MainTab.Calculator)) },
            onOpenTasks = { listener(MainEvent.SelectTab(MainTab.Tasks)) },
            onOpenSupport = { listener(MainEvent.SelectTab(MainTab.Support)) }
        )

        MainTab.Rating -> StatisticsScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            ratingDetail = state.ratingDetail,
            sources = state.sources
        )

        MainTab.Calculator -> CalculatorScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            form = state.calculatorForm,
            onVolumeFactChange = { listener(MainEvent.UpdateCalculatorVolumeFactMln(it)) },
            onDealsFactChange = { listener(MainEvent.UpdateCalculatorDealsFact(it)) },
            onShareFactChange = { listener(MainEvent.UpdateCalculatorShareFact(it)) },
            onApprovedChange = { listener(MainEvent.UpdateCalculatorApproved(it)) },
            onSubmittedChange = { listener(MainEvent.UpdateCalculatorSubmitted(it)) }
        )

        MainTab.Tasks -> TasksScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            dashboard = state.dashboard,
            sources = state.sources
        )

        MainTab.DailyResults -> DailyResultsScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            dashboard = state.dashboard,
            sources = state.sources,
            dailyResults = state.dailyResults,
            form = state.dailyForm,
            onDateChange = { listener(MainEvent.UpdateDailyDate(it)) },
            onDealsChange = { listener(MainEvent.UpdateDailyDeals(it)) },
            onVolumeChange = { listener(MainEvent.UpdateDailyVolume(it)) },
            onShareChange = { listener(MainEvent.UpdateDailyShare(it)) },
            onProductsChange = { listener(MainEvent.UpdateDailyProducts(it)) },
            onSubmit = { listener(MainEvent.SubmitDailyResults) }
        )

        MainTab.Privileges -> PrivilegesScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            dashboard = state.dashboard
        )

        MainTab.FinancialEffect -> FinancialEffectScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            dashboard = state.dashboard,
            sources = state.sources
        )

        MainTab.Leaderboard -> LeaderboardScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            dashboard = state.dashboard,
            sources = state.sources,
            selectedScope = state.selectedLeaderboardScope,
            onSelectScope = { listener(MainEvent.SelectLeaderboardScope(it)) },
            dealerItems = state.dealerLeaderboardItems,
            regionItems = state.regionLeaderboardItems,
            dealerRank = state.dealerRank,
            regionRank = state.regionRank
        )

        MainTab.Learning -> LearningScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            modules = state.learningModules,
            attempts = state.learningAttempts,
            activeQuiz = state.learningQuiz,
            pendingModuleIds = state.pendingLearningIds,
            onOpenQuiz = { listener(MainEvent.OpenLearningQuiz(it)) },
            onUpdateQuizAnswer = { questionId, answer -> listener(MainEvent.UpdateLearningQuizAnswer(questionId, answer)) },
            onSubmitQuiz = { listener(MainEvent.SubmitLearningQuiz) },
            onCloseQuiz = { listener(MainEvent.CloseLearningQuiz) },
            onCompleteModule = { listener(MainEvent.CompleteLearningModule(it)) }
        )

        MainTab.Support -> SupportScreen(
            contentPadding = contentPadding,
            selectedTab = tab,
            onSelectTab = { listener(MainEvent.SelectTab(it)) },
            tickets = state.supportTickets,
            assistantHistory = state.assistantHistory,
            composer = state.supportComposer,
            onSubjectChange = { listener(MainEvent.UpdateSupportSubject(it)) },
            onMessageChange = { listener(MainEvent.UpdateSupportMessage(it)) },
            onCategoryChange = { listener(MainEvent.UpdateSupportCategory(it)) },
            onPriorityChange = { listener(MainEvent.UpdateSupportPriority(it)) },
            onAssistantQuestionChange = { listener(MainEvent.UpdateAssistantQuestion(it)) },
            onAskAssistant = { listener(MainEvent.AskSupportAssistant) },
            onApplySuggestion = { listener(MainEvent.ApplyAssistantSuggestion(it)) },
            onSubmit = { listener(MainEvent.SubmitSupportTicket) }
        )

        MainTab.News -> NewsScreen(
            contentPadding = contentPadding,
            news = state.news
        )

        MainTab.Profile -> ProfileScreen(
            contentPadding = contentPadding,
            profile = state.profile,
            onLogout = { listener(MainEvent.Logout) }
        )
    }
}

private fun tabTitle(tab: MainTab): String = when (tab) {
    MainTab.Status -> "Текущий статус"
    MainTab.Rating -> "Детализация рейтинга"
    MainTab.Calculator -> "Сценарный калькулятор"
    MainTab.Privileges -> "Привилегии уровня"
    MainTab.FinancialEffect -> "Личный эффект"
    MainTab.Tasks -> "Задачи месяца"
    MainTab.DailyResults -> "Результаты дня"
    MainTab.Leaderboard -> "Рейтинг"
    MainTab.Learning -> "Обучение"
    MainTab.Support -> "Поддержка"
    MainTab.News -> "Новости"
    MainTab.Profile -> "Профиль"
}
