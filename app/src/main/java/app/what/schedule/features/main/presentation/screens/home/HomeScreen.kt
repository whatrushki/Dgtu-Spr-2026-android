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

private const val NO_DATA = "\u041D\u0435\u0442 \u0434\u0430\u043D\u043D\u044B\u0445"

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
                CardTitle(
                    title = if (isLoading) {
                        "\u0417\u0430\u0433\u0440\u0443\u0437\u043A\u0430 \u0434\u0430\u043D\u043D\u044B\u0445"
                    } else {
                        "\u041D\u0435 \u0443\u0434\u0430\u043B\u043E\u0441\u044C \u043E\u0431\u043D\u043E\u0432\u0438\u0442\u044C \u044D\u043A\u0440\u0430\u043D"
                    }
                )
                HintText(
                    errorMessage
                        ?: "\u041F\u043E\u043B\u0443\u0447\u0430\u0435\u043C \u0441\u0442\u0430\u0442\u0443\u0441, \u0440\u0435\u0439\u0442\u0438\u043D\u0433 " +
                            "\u0438 \u0444\u0438\u043D\u0430\u043D\u0441\u043E\u0432\u044B\u0439 \u044D\u0444\u0444\u0435\u043A\u0442 \u0438\u0437 backend."
                )
                GlassPrimaryButton(onClick = onRetry) {
                    Text("\u041E\u0431\u043D\u043E\u0432\u0438\u0442\u044C")
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
        AccentText("\u041D\u0435\u0439\u0440\u043E\u0430\u0441\u0441\u0438\u0441\u0442\u0435\u043D\u0442 \u0443\u0436\u0435 \u0434\u043E\u0441\u0442\u0443\u043F\u0435\u043D")
        HintText(
            "\u0417\u0430\u0434\u0430\u0432\u0430\u0439\u0442\u0435 \u0432\u043E\u043F\u0440\u043E\u0441\u044B \u043F\u043E KPI, \u0442\u0438\u043A\u0435\u0442\u0430\u043C, " +
                "\u043E\u0431\u0443\u0447\u0430\u044E\u0449\u0438\u043C \u043C\u0430\u0442\u0435\u0440\u0438\u0430\u043B\u0430\u043C \u0438 " +
                "\u0440\u0430\u0431\u043E\u0447\u0438\u043C \u0441\u0446\u0435\u043D\u0430\u0440\u0438\u044F\u043C \u043F\u0440\u044F\u043C\u043E \u0432 " +
                "\u0441\u0435\u0440\u0432\u0438\u0441\u043D\u043E\u043C \u0440\u0430\u0437\u0434\u0435\u043B\u0435."
        )
        GlassSecondaryButton(
            onClick = onOpenSupport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("\u041E\u0442\u043A\u0440\u044B\u0442\u044C \u0430\u0441\u0441\u0438\u0441\u0442\u0435\u043D\u0442\u0430")
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
            title = dashboard.currentStatus ?: "\u0422\u0435\u043A\u0443\u0449\u0438\u0439 \u0441\u0442\u0430\u0442\u0443\u0441",
            trailing = dashboard.nextStatus?.let { "\u0421\u043B\u0435\u0434\u0443\u044E\u0449\u0438\u0439: $it" }
        )
        ValueText(
            text = dashboard.currentPoints?.let { "${formatInt(it)} \u0431\u0430\u043B\u043B\u043E\u0432" } ?: NO_DATA,
            accent = true
        )
        LabelText(
            dashboard.pointsToNextLevel?.let {
                "\u0414\u043E ${dashboard.nextStatus ?: "\u0441\u043B\u0435\u0434\u0443\u044E\u0449\u0435\u0433\u043E \u0443\u0440\u043E\u0432\u043D\u044F"} " +
                    "\u043E\u0441\u0442\u0430\u043B\u043E\u0441\u044C $it \u0431\u0430\u043B\u043B\u043E\u0432"
            } ?: "\u041D\u0435\u0442 \u0434\u0430\u043D\u043D\u044B\u0445 \u043F\u043E \u0441\u043B\u0435\u0434\u0443\u044E\u0449\u0435\u043C\u0443 \u0443\u0440\u043E\u0432\u043D\u044E"
        )
        ProgressTrack((dashboard.progressPercent ?: 0) / 100f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GridGap * 2)
        ) {
            MetricColumn(
                modifier = Modifier.weight(1f),
                label = "\u041F\u0440\u043E\u0433\u0440\u0435\u0441\u0441",
                value = dashboard.progressPercent?.let { "$it%" } ?: NO_DATA
            )
            MetricColumn(
                modifier = Modifier.weight(1f),
                label = "\u0414\u043E \u043A\u043E\u043D\u0446\u0430 \u043C\u0435\u0441\u044F\u0446\u0430",
                value = dashboard.daysToMonthEnd?.let { "$it \u0434\u043D\u0435\u0439" } ?: NO_DATA
            )
        }

        SectionDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LabelText("\u0424\u0438\u043D\u0430\u043D\u0441\u043E\u0432\u044B\u0439 \u043F\u0440\u043E\u0433\u043D\u043E\u0437")
            AccentText(
                dashboard.yearlyIncomeGrowthRub?.let { "\u0414\u043E\u0445\u043E\u0434 +${formatMoney(it)}" } ?: NO_DATA
            )
            Text(
                text = dashboard.mortgageSavingsRub?.let {
                    "\u042D\u043A\u043E\u043D\u043E\u043C\u0438\u044F \u043D\u0430 \u0438\u043F\u043E\u0442\u0435\u043A\u0435: ${formatMoney(it)}"
                } ?: "\u042D\u043A\u043E\u043D\u043E\u043C\u0438\u044F \u043D\u0430 \u0438\u043F\u043E\u0442\u0435\u043A\u0435: $NO_DATA",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        GlassPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenCalculator
        ) {
            Text("\u041A\u0430\u043A \u0443\u0441\u043A\u043E\u0440\u0438\u0442\u044C \u043F\u0435\u0440\u0435\u0445\u043E\u0434")
        }

        HintText(dashboard.levelTransitionRule)
        HintText("\u0418\u0441\u0442\u043E\u0447\u043D\u0438\u043A\u0438: ${sourceLabel(sources.status)}, ${sourceLabel(sources.financialEffect)}")
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
        CardTitle(title = "\u0420\u0435\u0437\u0443\u043B\u044C\u0442\u0430\u0442\u044B \u0434\u043D\u044F")
        ResultLine("\u0421\u0434\u0435\u043B\u043A\u0438", dashboard.dailyDeals?.toString())
        ResultLine(
            "\u041E\u0431\u044A\u0435\u043C",
            dashboard.dailyCreditVolumeMln?.let { "${it.toString().replace('.', ',')} \u043C\u043B\u043D \u20BD" }
        )
        ResultLine("\u0414\u043E\u043F. \u043F\u0440\u043E\u0434\u0443\u043A\u0442\u044B", dashboard.dailyExtraProducts?.toString())
        HintText("\u0418\u0441\u0442\u043E\u0447\u043D\u0438\u043A: ${sourceLabel(sources.dailyResults)}")
    }
}

@Composable
private fun RatingCard(
    modifier: Modifier,
    dashboard: DashboardUi,
    sources: DashboardSources
) {
    DashboardCard(modifier = modifier) {
        CardTitle(title = "\u0420\u0435\u0439\u0442\u0438\u043D\u0433")
        ValueText(dashboard.rank?.let { "#$it" } ?: NO_DATA)
        LabelText("\u041C\u043E\u044F \u043F\u043E\u0437\u0438\u0446\u0438\u044F \u0432 \u0440\u0435\u0439\u0442\u0438\u043D\u0433\u0435 \u0434\u0438\u043B\u0435\u0440\u0430")
        HintText(
            dashboard.rankDeltaWeek?.let {
                "\u0418\u0437\u043C\u0435\u043D\u0435\u043D\u0438\u0435 \u0437\u0430 \u043D\u0435\u0434\u0435\u043B\u044E: +$it"
            } ?: "\u041D\u0435\u0442 \u0434\u0430\u043D\u043D\u044B\u0445 \u043F\u043E \u0434\u0438\u043D\u0430\u043C\u0438\u043A\u0435"
        )
        HintText("\u0418\u0441\u0442\u043E\u0447\u043D\u0438\u043A: ${sourceLabel(sources.leaderboard)}")
    }
}

@Composable
private fun BenefitCard(
    modifier: Modifier,
    dashboard: DashboardUi
) {
    DashboardCard(modifier = modifier) {
        CardTitle(title = "\u041E\u0431\u0449\u0430\u044F \u0432\u044B\u0433\u043E\u0434\u0430 \u0437\u0430 \u0433\u043E\u0434")
        AccentText(formatMoney(dashboard.totalBenefitRub))
        LabelText("\u0411\u043E\u043D\u0443\u0441\u044B, \u0438\u043F\u043E\u0442\u0435\u043A\u0430, \u043A\u0435\u0448\u0431\u044D\u043A, \u0414\u041C\u0421")
    }
}

@Composable
private fun StatusSummaryCard(
    modifier: Modifier,
    dashboard: DashboardUi
) {
    DashboardCard(modifier = modifier) {
        CardTitle(title = "\u0421\u0442\u0430\u0442\u0443\u0441 \u0438 \u0446\u0435\u043B\u044C")
        LabelText("\u0422\u0435\u043A\u0443\u0449\u0438\u0439 \u0443\u0440\u043E\u0432\u0435\u043D\u044C")
        Text(
            text = dashboard.currentStatus ?: NO_DATA,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        LabelText("\u0411\u0430\u043B\u043B\u043E\u0432 \u0434\u043E \u0441\u043B\u0435\u0434\u0443\u044E\u0449\u0435\u0433\u043E \u0443\u0440\u043E\u0432\u043D\u044F")
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
        CardTitle(title = "\u0417\u0430\u0434\u0430\u0447\u0438 \u043C\u0435\u0441\u044F\u0446\u0430", trailing = dashboard.monthlyTasks.size.toString())
        if (dashboard.monthlyTasks.isEmpty()) {
            EmptyState("\u0417\u0430\u0434\u0430\u0447\u0438 \u043F\u043E\u043A\u0430 \u043D\u0435 \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043D\u044B")
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
            Text("\u041F\u043E\u0434\u0440\u043E\u0431\u043D\u0435\u0435 \u043F\u043E \u0437\u0430\u0434\u0430\u0447\u0430\u043C")
        }
        HintText("\u0418\u0441\u0442\u043E\u0447\u043D\u0438\u043A: ${sourceLabel(sources.tasks)}")
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
