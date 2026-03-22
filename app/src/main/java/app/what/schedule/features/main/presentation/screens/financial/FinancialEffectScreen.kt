package app.what.schedule.features.main.presentation.screens.financial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import app.what.schedule.features.main.domain.models.DashboardSources
import app.what.schedule.features.main.domain.models.DashboardUi
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.AccentText
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.LabelText
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.ValueText
import app.what.schedule.features.main.presentation.components.formatMoney
import app.what.schedule.features.main.presentation.components.sourceLabel

@Composable
fun FinancialEffectScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    dashboard: DashboardUi,
    sources: DashboardSources
) {
    ScreenColumn(contentPadding = contentPadding) {
        BenefitQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)
        DashboardCard {
            LabelText("Ваша общая выгода в 2026 году")
            ValueText(formatMoney(dashboard.totalBenefitRub), accent = true)
            HintText("Источник: ${sourceLabel(sources.financialEffect)}")
        }
        DashboardCard {
            CardTitle(title = "Структура личного эффекта")
            EffectRow("Доп. доход от бонусов", dashboard.yearlyIncomeGrowthRub)
            EffectRow("Экономия по ипотеке", dashboard.mortgageSavingsRub)
            EffectRow("Кэшбэк", dashboard.cashbackRub)
            EffectRow("ДМС", dashboard.dmsValueRub)
        }
    }
}

@Composable
private fun EffectRow(
    title: String,
    value: Int?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LabelText(title)
        Text(
            text = formatMoney(value),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
