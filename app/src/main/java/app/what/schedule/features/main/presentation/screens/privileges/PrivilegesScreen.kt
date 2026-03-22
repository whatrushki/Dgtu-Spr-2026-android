package app.what.schedule.features.main.presentation.screens.privileges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.what.schedule.features.main.domain.models.DashboardUi
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.domain.models.PrivilegeUi
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.GlassPrimaryButton
import app.what.schedule.features.main.presentation.components.GlassSecondaryButton
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.ScreenSectionTitle
import app.what.schedule.features.main.presentation.components.SectionGap
import app.what.schedule.features.main.presentation.components.formatMoney
import app.what.schedule.features.main.presentation.screens.financial.BenefitQuickActions

@Composable
fun PrivilegesScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    dashboard: DashboardUi
) {
    ScreenColumn(contentPadding = contentPadding) {
        BenefitQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)
        PrivilegeBlock(
            title = "Активные привилегии",
            items = dashboard.activePrivileges,
            onOpenCalculator = { onSelectTab(MainTab.Calculator) },
            onOpenDetails = { onSelectTab(MainTab.FinancialEffect) }
        )
        PrivilegeBlock(
            title = "Заблокированные привилегии",
            items = dashboard.lockedPrivileges,
            onOpenCalculator = { onSelectTab(MainTab.Calculator) },
            onOpenDetails = { onSelectTab(MainTab.FinancialEffect) }
        )
    }
}

@Composable
private fun PrivilegeBlock(
    title: String,
    items: List<PrivilegeUi>,
    onOpenCalculator: () -> Unit,
    onOpenDetails: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (items.isEmpty()) {
            DashboardCard {
                CardTitle(title = title, trailing = "0")
                EmptyState("Привилегии пока не загружены")
            }
        } else {
            ScreenSectionTitle(text = title)
            items.forEach { item ->
                DashboardCard {
                    CardTitle(title = item.title, trailing = item.status)
                    item.description?.let { HintText(it) }
                    item.requiredLevel?.let { HintText("Уровень: $it") }
                    item.financialEffectRub?.let { HintText("Финансовый эффект: ${formatMoney(it)}") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassSecondaryButton(
                            modifier = Modifier.weight(1f),
                            onClick = onOpenDetails,
                            height = 46.dp
                        ) {
                            Text("Подробнее")
                        }
                        GlassPrimaryButton(
                            modifier = Modifier.weight(1f),
                            onClick = onOpenCalculator,
                            height = 46.dp
                        ) {
                            Text("Рассчитать")
                        }
                    }
                }
            }
        }
    }
}
