package app.what.schedule.features.main.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.schedule.features.main.domain.models.DashboardSources
import app.what.schedule.features.main.domain.models.DashboardUi
import app.what.schedule.features.main.presentation.components.AccentText
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.GlassPrimaryButton
import app.what.schedule.features.main.presentation.components.GlassSecondaryButton
import app.what.schedule.features.main.presentation.components.GridGap
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.LabelText
import app.what.schedule.features.main.presentation.components.ProgressTrack
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.SectionDivider
import app.what.schedule.features.main.presentation.components.SectionGap
import app.what.schedule.features.main.presentation.components.ValueText
import app.what.schedule.features.main.presentation.components.formatInt
import app.what.schedule.features.main.presentation.components.formatMoney
import app.what.schedule.features.main.presentation.components.sourceLabel

private const val NO_DATA = "Нет данных"

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    dashboard: DashboardUi,
    sources: DashboardSources,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenSupport: () -> Unit
) {
    ScreenColumn(contentPadding = contentPadding) {
        if (isLoading || !errorMessage.isNullOrBlank()) {
            DashboardCard {
                CardTitle(title = if (isLoading) "Загрузка данных" else "Не удалось обновить экран")
                HintText(errorMessage ?: "Получаем статус, рейтинг и финансовый эффект из backend.")
                GlassPrimaryButton(onClick = onRetry) {
                    Text("Обновить")
                }
            }
        }

        HeroCard(
            dashboard = dashboard,
            sources = sources,
            onOpenCalculator = onOpenCalculator
        )
        AssistantCard(onOpenSupport = onOpenSupport)
        OverviewGrid(dashboard = dashboard, sources = sources)
        TasksPreviewCard(
            dashboard = dashboard,
            sources = sources,
            onOpenTasks = onOpenTasks
        )
    }
}

@Composable
private fun AssistantCard(
    onOpenSupport: () -> Unit
) {
    DashboardCard {
        CardTitle(title = "GigaChat")
        AccentText("Нейроассистент уже доступен")
        HintText("Задавайте вопросы по KPI, тикетам, обучающим материалам и рабочим сценариям прямо в сервисном разделе.")
        GlassSecondaryButton(
            onClick = onOpenSupport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Открыть ассистента")
        }
    }
}

@Composable
private fun HeroCard(
    dashboard: DashboardUi,
    sources: DashboardSources,
    onOpenCalculator: () -> Unit
) {
    DashboardCard {
        CardTitle(
            title = dashboard.currentStatus ?: "Текущий статус",
            trailing = dashboard.nextStatus?.let { "Следующий: $it" }
        )
        ValueText(
            text = dashboard.currentPoints?.let { "${formatInt(it)} баллов" } ?: NO_DATA,
            accent = true
        )
        LabelText(
            dashboard.pointsToNextLevel?.let {
                "До ${dashboard.nextStatus ?: "следующего уровня"} осталось $it баллов"
            } ?: "Нет данных по следующему уровню"
        )
        ProgressTrack((dashboard.progressPercent ?: 0) / 100f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GridGap * 2)
        ) {
            MetricColumn(
                modifier = Modifier.weight(1f),
                label = "Прогресс",
                value = dashboard.progressPercent?.let { "$it%" } ?: NO_DATA
            )
            MetricColumn(
                modifier = Modifier.weight(1f),
                label = "До конца месяца",
                value = dashboard.daysToMonthEnd?.let { "$it дней" } ?: NO_DATA
            )
        }

        SectionDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LabelText("Финансовый прогноз")
            AccentText(
                dashboard.yearlyIncomeGrowthRub?.let { "Доход +${formatMoney(it)}" } ?: NO_DATA
            )
            Text(
                text = dashboard.mortgageSavingsRub?.let { "Экономия на ипотеке: ${formatMoney(it)}" }
                    ?: "Экономия на ипотеке: $NO_DATA",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        GlassPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenCalculator
        ) {
            Text("Как ускорить переход")
        }

        HintText(dashboard.levelTransitionRule)
        HintText("Источники: ${sourceLabel(sources.status)}, ${sourceLabel(sources.financialEffect)}")
    }
}

@Composable
private fun OverviewGrid(
    dashboard: DashboardUi,
    sources: DashboardSources
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val singleColumn = maxWidth < 440.dp
        if (singleColumn) {
            Column(verticalArrangement = Arrangement.spacedBy(SectionGap)) {
                DayResultCard(Modifier, dashboard, sources)
                RatingCard(Modifier, dashboard, sources)
                BenefitCard(Modifier, dashboard)
                StatusSummaryCard(Modifier, dashboard)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(SectionGap)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(GridGap * 2)
                ) {
                    DayResultCard(Modifier.weight(1f), dashboard, sources)
                    RatingCard(Modifier.weight(1f), dashboard, sources)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(GridGap * 2)
                ) {
                    BenefitCard(Modifier.weight(1f), dashboard)
                    StatusSummaryCard(Modifier.weight(1f), dashboard)
                }
            }
        }
    }
}

@Composable
private fun DayResultCard(
    modifier: Modifier,
    dashboard: DashboardUi,
    sources: DashboardSources
) {
    DashboardCard(modifier = modifier) {
        CardTitle(title = "Результаты дня")
        ResultLine("Сделки", dashboard.dailyDeals?.toString())
        ResultLine(
            "Объем",
            dashboard.dailyCreditVolumeMln?.let { "${it.toString().replace('.', ',')} млн ₽" }
        )
        ResultLine("Доп. продукты", dashboard.dailyExtraProducts?.toString())
        HintText("Источник: ${sourceLabel(sources.dailyResults)}")
    }
}

@Composable
private fun RatingCard(
    modifier: Modifier,
    dashboard: DashboardUi,
    sources: DashboardSources
) {
    DashboardCard(modifier = modifier) {
        CardTitle(title = "Рейтинг")
        ValueText(dashboard.rank?.let { "#$it" } ?: NO_DATA)
        LabelText("Моя позиция в рейтинге дилера")
        HintText(
            dashboard.rankDeltaWeek?.let { "Изменение за неделю: +$it" } ?: "Нет данных по динамике"
        )
        HintText("Источник: ${sourceLabel(sources.leaderboard)}")
    }
}

@Composable
private fun BenefitCard(
    modifier: Modifier,
    dashboard: DashboardUi
) {
    DashboardCard(modifier = modifier) {
        CardTitle(title = "Общая выгода за год")
        AccentText(formatMoney(dashboard.totalBenefitRub))
        LabelText("Бонусы, ипотека, кешбэк, ДМС")
    }
}

@Composable
private fun StatusSummaryCard(
    modifier: Modifier,
    dashboard: DashboardUi
) {
    DashboardCard(modifier = modifier) {
        CardTitle(title = "Статус и цель")
        LabelText("Текущий уровень")
        Text(
            text = dashboard.currentStatus ?: NO_DATA,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        LabelText("Баллов до следующего уровня")
        Text(
            text = dashboard.pointsToNextLevel?.let { formatInt(it) } ?: NO_DATA,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TasksPreviewCard(
    dashboard: DashboardUi,
    sources: DashboardSources,
    onOpenTasks: () -> Unit
) {
    DashboardCard {
        CardTitle(title = "Задачи месяца", trailing = dashboard.monthlyTasks.size.toString())
        if (dashboard.monthlyTasks.isEmpty()) {
            EmptyState("Задачи пока не загружены")
        } else {
            dashboard.monthlyTasks.take(3).forEachIndexed { index, task ->
                if (index > 0) {
                    SectionDivider()
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = task.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                    task.reward?.let { HintText(it) }
                    task.progress?.let { HintText(it) }
                    ProgressTrack((task.progressPercent ?: 0) / 100f)
                }
            }
        }
        GlassSecondaryButton(onClick = onOpenTasks) {
            Text("Подробнее по задачам")
        }
        HintText("Источник: ${sourceLabel(sources.tasks)}")
    }
}

@Composable
private fun MetricColumn(
    modifier: Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LabelText(label)
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun ResultLine(
    label: String,
    value: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LabelText(label)
        Text(
            text = value ?: NO_DATA,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
