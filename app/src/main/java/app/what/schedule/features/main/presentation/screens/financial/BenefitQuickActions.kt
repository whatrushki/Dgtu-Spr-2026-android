package app.what.schedule.features.main.presentation.screens.financial

import androidx.compose.runtime.Composable
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.MultiChoiceRow

private val benefitTabs = listOf(
    MainTab.Privileges to "Привилегии",
    MainTab.FinancialEffect to "Выгода",
    MainTab.Tasks to "Задачи",
    MainTab.Leaderboard to "Топ-10"
)

@Composable
fun BenefitQuickActions(
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    MultiChoiceRow(
        actions = benefitTabs,
        selectedTab = selectedTab,
        onSelectTab = onSelectTab
    )
}
