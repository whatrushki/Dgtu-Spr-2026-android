package app.what.schedule.features.main.presentation.screens.statistics

import androidx.compose.runtime.Composable
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.MultiChoiceRow

private val growthTabs = listOf(
    MainTab.Rating to "\u0420\u0435\u0439\u0442\u0438\u043D\u0433",
    MainTab.DailyResults to "\u0420\u0435\u0437\u0443\u043B\u044C\u0442\u0430\u0442\u044B",
    MainTab.Calculator to "\u041A\u0430\u043B\u044C\u043A\u0443\u043B\u044F\u0442\u043E\u0440"
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
