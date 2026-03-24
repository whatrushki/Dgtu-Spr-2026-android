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
            CardTitle(title = "\u0414\u0435\u0442\u0430\u043B\u0438\u0437\u0430\u0446\u0438\u044F \u0440\u0435\u0439\u0442\u0438\u043D\u0433\u0430")
            ValueText(
                text = ratingDetail.totalPoints?.let { "${formatInt(it)} \u0431\u0430\u043B\u043B\u043E\u0432" }
                    ?: "\u041D\u0435\u0442 \u0434\u0430\u043D\u043D\u044B\u0445",
                accent = true
            )
            HintText(
                "\u041F\u043E\u043A\u0430\u0437\u0430\u0442\u0435\u043B\u0438 \u043D\u0438\u0436\u0435 \u043E\u0442\u043A\u0440\u044B\u0432\u0430\u044E\u0442\u0441\u044F " +
                    "\u043F\u043E \u043E\u0442\u0434\u0435\u043B\u044C\u043D\u043E\u0441\u0442\u0438 \u0447\u0435\u0440\u0435\u0437 \u0438\u043A\u043E\u043D\u043A\u0443 \u0438\u043D\u0444\u043E\u0440\u043C\u0430\u0446\u0438\u0438."
            )
            GlassPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelectTab(MainTab.Calculator) }
            ) {
                Text("\u0421\u043C\u043E\u0434\u0435\u043B\u0438\u0440\u043E\u0432\u0430\u0442\u044C \u0440\u043E\u0441\u0442")
            }
            HintText("\u0418\u0441\u0442\u043E\u0447\u043D\u0438\u043A: ${sourceLabel(sources.ratingDetail)}")
        }

        if (currentDetail.metrics.isEmpty()) {
            DashboardCard {
                EmptyState("\u041F\u043E\u043A\u0430\u0437\u0430\u0442\u0435\u043B\u0438 \u043F\u043E \u0440\u0435\u0439\u0442\u0438\u043D\u0433\u0443 \u043F\u043E\u043A\u0430 \u043D\u0435 \u043F\u0440\u0438\u0448\u043B\u0438")
            }
        } else {
            DashboardCard {
                CardTitle(title = "\u041F\u043E\u043A\u0430\u0437\u0430\u0442\u0435\u043B\u0438", trailing = currentDetail.metrics.size.toString())
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
            HintText("\u041E\u0442\u043A\u0440\u044B\u0442\u044C \u043E\u043F\u0438\u0441\u0430\u043D\u0438\u0435 \u043F\u043E\u043A\u0430\u0437\u0430\u0442\u0435\u043B\u044F")
        }
        AccentText(metric.points?.let(::formatInt) ?: "\u041D\u0435\u0442 \u0434\u0430\u043D\u043D\u044B\u0445")
        IconButton(onClick = onOpenDetails) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "\u041E\u043F\u0438\u0441\u0430\u043D\u0438\u0435 \u043F\u043E\u043A\u0430\u0437\u0430\u0442\u0435\u043B\u044F"
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
                title = "\u041A\u0430\u043A \u0440\u0430\u0441\u0441\u0447\u0438\u0442\u044B\u0432\u0430\u0435\u0442\u0441\u044F",
                body = metric.howCalculated
            )
            InfoBlock(
                title = "\u041A\u0430\u043A \u0443\u0432\u0435\u043B\u0438\u0447\u0438\u0442\u044C",
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
