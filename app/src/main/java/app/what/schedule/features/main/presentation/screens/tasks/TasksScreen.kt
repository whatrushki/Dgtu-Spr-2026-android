package app.what.schedule.features.main.presentation.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.schedule.features.main.domain.models.DashboardSources
import app.what.schedule.features.main.domain.models.DashboardUi
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.ProgressTrack
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.sourceLabel
import app.what.schedule.features.main.presentation.screens.financial.BenefitQuickActions

@Composable
fun TasksScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    dashboard: DashboardUi,
    sources: DashboardSources
) {
    ScreenColumn(contentPadding = contentPadding) {
        BenefitQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)

        if (dashboard.monthlyTasks.isEmpty()) {
            DashboardCard {
                CardTitle(title = "Задачи месяца", trailing = "0")
                EmptyState("Задачи месяца пока не загружены")
                HintText("Источник: ${sourceLabel(sources.tasks)}")
            }
        } else {
            dashboard.monthlyTasks.forEachIndexed { index, task ->
                DashboardCard {
                    CardTitle(title = task.title, trailing = "${index + 1}")
                    task.description?.let { HintText(it) }
                    task.progress?.let { HintText("Прогресс: $it") }
                    task.reward?.let { HintText("Награда: $it") }
                    task.deadline?.let { HintText("Дедлайн: $it") }
                    ProgressTrack((task.progressPercent ?: 0) / 100f)
                    Text(
                        text = if (task.completed) "Выполнено" else "В работе",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            DashboardCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CardTitle(title = "Источник")
                    HintText(sourceLabel(sources.tasks))
                }
            }
        }
    }
}
