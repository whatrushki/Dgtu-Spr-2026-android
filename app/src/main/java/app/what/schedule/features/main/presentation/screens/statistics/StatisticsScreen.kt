package app.what.schedule.features.main.presentation.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.what.foundation.ui.controllers.rememberDialogController
import app.what.schedule.features.main.domain.models.DashboardSources
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.domain.models.RatingDetailUi
import app.what.schedule.features.main.domain.models.RatingMetricUi
import app.what.schedule.features.main.presentation.components.AccentText
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.GlassPrimaryButton
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.LabelText
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.SectionDivider
import app.what.schedule.features.main.presentation.components.ValueText
import app.what.schedule.features.main.presentation.components.formatInt
import app.what.schedule.features.main.presentation.components.sourceLabel

@Composable
fun StatisticsScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    ratingDetail: RatingDetailUi,
    sources: DashboardSources
) {
    val dialog = rememberDialogController()
    val currentDetail by rememberUpdatedState(ratingDetail)

    ScreenColumn(contentPadding = contentPadding) {
        GrowthQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)

        DashboardCard {
            CardTitle(title = "Детализация рейтинга")
            ValueText(
                text = ratingDetail.totalPoints?.let { "${formatInt(it)} баллов" } ?: "Нет данных",
                accent = true
            )
            HintText("Показатели ниже открываются по отдельности через иконку информации.")
            GlassPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelectTab(MainTab.Calculator) }
            ) {
                Text("Смоделировать рост")
            }
            HintText("Источник: ${sourceLabel(sources.ratingDetail)}")
        }

        if (currentDetail.metrics.isEmpty()) {
            DashboardCard {
                EmptyState("Показатели по рейтингу пока не пришли")
            }
        } else {
            DashboardCard {
                CardTitle(title = "Показатели", trailing = currentDetail.metrics.size.toString())
                currentDetail.metrics.forEachIndexed { index, metric ->
                    if (index > 0) {
                        SectionDivider()
                    }
                    MetricOverviewRow(
                        metric = metric,
                        onOpenDetails = {
                            dialog.open {
                                MetricDetailsDialog(metric = metric)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricOverviewRow(
    metric: RatingMetricUi,
    onOpenDetails: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = metric.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            HintText("Открыть описание показателя")
        }
        AccentText(metric.points?.let(::formatInt) ?: "Нет данных")
        IconButton(onClick = onOpenDetails) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Описание показателя"
            )
        }
    }
}

@Composable
private fun MetricDetailsDialog(metric: RatingMetricUi) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = metric.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            InfoBlock(
                title = "Как рассчитывается",
                body = metric.howCalculated
            )
            InfoBlock(
                title = "Как увеличить",
                body = metric.howIncrease
            )
        }
    }
}

@Composable
private fun InfoBlock(
    title: String,
    body: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.28f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LabelText(title)
            Text(
                text = body,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
