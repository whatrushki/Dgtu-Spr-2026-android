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
            title = "\u0410\u043A\u0442\u0438\u0432\u043D\u044B\u0435 \u043F\u0440\u0438\u0432\u0438\u043B\u0435\u0433\u0438\u0438",
            items = dashboard.activePrivileges,
            onOpenCalculator = { onSelectTab(MainTab.Calculator) },
            onOpenDetails = { onSelectTab(MainTab.FinancialEffect) }
        )
        PrivilegeBlock(
            title = "\u0417\u0430\u0431\u043B\u043E\u043A\u0438\u0440\u043E\u0432\u0430\u043D\u043D\u044B\u0435 \u043F\u0440\u0438\u0432\u0438\u043B\u0435\u0433\u0438\u0438",
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
                EmptyState("\u041F\u0440\u0438\u0432\u0438\u043B\u0435\u0433\u0438\u0438 \u043F\u043E\u043A\u0430 \u043D\u0435 \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043D\u044B")
            }
        } else {
            ScreenSectionTitle(text = title)
            items.forEach { item ->
                DashboardCard {
                    CardTitle(title = item.title, trailing = item.status)
                    item.description?.let { HintText(it) }
                    item.requiredLevel?.let { HintText("\u0423\u0440\u043E\u0432\u0435\u043D\u044C: $it") }
                    item.financialEffectRub?.let { HintText("\u0424\u0438\u043D\u0430\u043D\u0441\u043E\u0432\u044B\u0439 \u044D\u0444\u0444\u0435\u043A\u0442: ${formatMoney(it)}") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassSecondaryButton(
                            modifier = Modifier.weight(1f),
                            onClick = onOpenDetails,
                            height = 46.dp
                        ) {
                            Text("\u041F\u043E\u0434\u0440\u043E\u0431\u043D\u0435\u0435")
                        }
                        GlassPrimaryButton(
                            modifier = Modifier.weight(1f),
                            onClick = onOpenCalculator,
                            height = 46.dp
                        ) {
                            Text("\u0420\u0430\u0441\u0441\u0447\u0438\u0442\u0430\u0442\u044C")
                        }
                    }
                }
            }
        }
    }
}
