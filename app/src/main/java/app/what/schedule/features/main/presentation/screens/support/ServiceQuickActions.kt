package app.what.schedule.features.main.presentation.screens.support

import androidx.compose.runtime.Composable
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.MultiChoiceRow

private val serviceTabs = listOf(
    MainTab.Learning to "Обучение",
    MainTab.Support to "Поддержка"
)

@Composable
fun ServiceQuickActions(
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    MultiChoiceRow(
        actions = serviceTabs,
        selectedTab = selectedTab,
        onSelectTab = onSelectTab
    )
}
