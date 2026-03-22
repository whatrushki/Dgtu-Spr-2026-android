package app.what.schedule.features.main.presentation.screens.statistics

import androidx.compose.runtime.Composable
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.MultiChoiceRow

private val growthTabs = listOf(
    MainTab.Rating to "Рейтинг",
    MainTab.DailyResults to "Результаты",
    MainTab.Calculator to "Калькулятор"
)

@Composable
fun GrowthQuickActions(
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    MultiChoiceRow(
        actions = growthTabs,
        selectedTab = selectedTab,
        onSelectTab = onSelectTab
    )
}
