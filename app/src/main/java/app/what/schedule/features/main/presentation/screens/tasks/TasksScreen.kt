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
                CardTitle(title = "\u0417\u0430\u0434\u0430\u0447\u0438 \u043C\u0435\u0441\u044F\u0446\u0430", trailing = "0")
                EmptyState("\u0417\u0430\u0434\u0430\u0447\u0438 \u043C\u0435\u0441\u044F\u0446\u0430 \u043F\u043E\u043A\u0430 \u043D\u0435 \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043D\u044B")
                HintText("\u0418\u0441\u0442\u043E\u0447\u043D\u0438\u043A: ${sourceLabel(sources.tasks)}")
            }
        } else {
            dashboard.monthlyTasks.forEachIndexed { index, task ->
                DashboardCard {
                    CardTitle(title = task.title, trailing = "${index + 1}")
                    task.description?.let { HintText(it) }
                    task.metric?.let { HintText("Показатель: $it") }
                    task.progress?.let { HintText("\u041F\u0440\u043E\u0433\u0440\u0435\u0441\u0441: $it") }
                    task.reward?.let { HintText("\u041D\u0430\u0433\u0440\u0430\u0434\u0430: $it") }
                    task.deadline?.let { HintText("\u0414\u0435\u0434\u043B\u0430\u0439\u043D: $it") }
                    ProgressTrack((task.progressPercent ?: 0) / 100f)
                    Text(
                        text = if (task.completed) "\u0412\u044B\u043F\u043E\u043B\u043D\u0435\u043D\u043E" else "\u0412 \u0440\u0430\u0431\u043E\u0442\u0435",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            DashboardCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CardTitle(title = "\u0418\u0441\u0442\u043E\u0447\u043D\u0438\u043A")
                    HintText(sourceLabel(sources.tasks))
                }
            }
        }
    }
}
