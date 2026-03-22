package app.what.schedule.features.main.presentation.screens.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.schedule.features.main.domain.models.DashboardSources
import app.what.schedule.features.main.domain.models.DashboardUi
import app.what.schedule.features.main.domain.models.LeaderboardScope
import app.what.schedule.features.main.domain.models.LeaderboardUi
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.AccentText
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.MultiChoiceRow
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.ValueText
import app.what.schedule.features.main.presentation.components.sourceLabel
import app.what.schedule.features.main.presentation.screens.financial.BenefitQuickActions

@Composable
fun LeaderboardScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    dashboard: DashboardUi,
    sources: DashboardSources,
    selectedScope: LeaderboardScope,
    onSelectScope: (LeaderboardScope) -> Unit,
    dealerItems: List<LeaderboardUi>,
    regionItems: List<LeaderboardUi>,
    dealerRank: Int?,
    regionRank: Int?
) {
    val items = if (selectedScope == LeaderboardScope.Dealer) dealerItems else regionItems
    val myRank = if (selectedScope == LeaderboardScope.Dealer) dealerRank else regionRank
    val scopeTabs = listOf(
        LeaderboardScope.Dealer to "Топ дилера",
        LeaderboardScope.Region to "Топ региона"
    )

    ScreenColumn(contentPadding = contentPadding) {
        BenefitQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)

        DashboardCard {
            CardTitle(title = "Рейтинг")
            MultiChoiceScopeRow(
                actions = scopeTabs,
                selectedScope = selectedScope,
                onSelectScope = onSelectScope
            )
        }

        DashboardCard {
            CardTitle(title = "Моя позиция")
            ValueText(myRank?.let { "#$it" } ?: "Нет данных", accent = true)
            HintText("Источник: ${sourceLabel(sources.leaderboard)}")
        }

        if (items.isEmpty()) {
            DashboardCard {
                CardTitle(title = "Топ-10")
                EmptyState("Данные рейтинга пришли пустыми или ещё не загрузились")
            }
        } else {
            items.take(10).forEach { item ->
                DashboardCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "#${item.position} ${item.fullName}",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            HintText("ДЦ: ${item.dealerCenterCode}")
                            HintText("Уровень: ${item.level}")
                        }
                        AccentText("${item.totalPoints}")
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiChoiceScopeRow(
    actions: List<Pair<LeaderboardScope, String>>,
    selectedScope: LeaderboardScope,
    onSelectScope: (LeaderboardScope) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        actions.forEach { (scope, label) ->
            androidx.compose.material3.FilterChip(
                selected = scope == selectedScope,
                onClick = { onSelectScope(scope) },
                label = { Text(label) },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    labelColor = app.what.schedule.features.main.presentation.components.TextPrimary,
                    selectedContainerColor = androidx.compose.ui.graphics.Color(0x1E34C759),
                    selectedLabelColor = androidx.compose.ui.graphics.Color(0xFFF2FFF5)
                )
            )
        }
    }
}
