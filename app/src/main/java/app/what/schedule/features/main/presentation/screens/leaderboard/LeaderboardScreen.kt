package app.what.schedule.features.main.presentation.screens.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.TextPrimary
import app.what.schedule.features.main.presentation.components.ValueText
import app.what.schedule.features.main.presentation.components.sourceLabel
import app.what.schedule.features.main.presentation.screens.financial.BenefitQuickActions

private const val MAX_LEADERBOARD_ITEMS = 15

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
    val ratingItems = remember(regionItems) {
        regionItems
            .sortedWith(compareBy<LeaderboardUi> { normalizedPosition(it.position) }.thenByDescending { it.totalPoints })
    }
    val dealerCenterItems = remember(dealerItems) {
        dealerItems
            .sortedWith(compareBy<LeaderboardUi> { normalizedPosition(it.position) }.thenByDescending { it.totalPoints })
    }

    val isRatingScope = selectedScope == LeaderboardScope.Region
    val items = if (isRatingScope) ratingItems else dealerCenterItems
    val myRank = if (isRatingScope) regionRank else dealerRank
    val scopeTabs = listOf(
        LeaderboardScope.Region to "Топ рейтинга",
        LeaderboardScope.Dealer to "Топ дилеров"
    )
    val listTitle = if (isRatingScope) {
        "Топ-$MAX_LEADERBOARD_ITEMS по региону"
    } else {
        "Топ-$MAX_LEADERBOARD_ITEMS по вашему дилерскому центру"
    }

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
                CardTitle(title = listTitle)
                EmptyState("Backend пока не вернул данные для этого режима рейтинга.")
            }
        } else {
            DashboardCard {
                CardTitle(title = listTitle, trailing = "${items.size}")
                HintText(
                    if (isRatingScope) {
                        "Показаны лучшие участники общего рейтинга."
                    } else {
                        "Показаны лучшие участники вашего дилерского центра."
                    }
                )
            }

            items.forEach { item ->
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
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(actions) { (scope, label) ->
            FilterChip(
                selected = scope == selectedScope,
                onClick = { onSelectScope(scope) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Transparent,
                    labelColor = TextPrimary,
                    selectedContainerColor = Color(0x1E34C759),
                    selectedLabelColor = Color(0xFFF2FFF5)
                )
            )
        }
    }
}

private fun normalizedPosition(position: Int): Int = if (position > 0) position else Int.MAX_VALUE
