package app.what.schedule.features.main.presentation.screens.daily

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.schedule.features.main.domain.models.DailyFormUi
import app.what.schedule.features.main.domain.models.DailyResultsUi
import app.what.schedule.features.main.domain.models.DashboardSources
import app.what.schedule.features.main.domain.models.DashboardUi
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.GlassPrimaryButton
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.LabelText
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.SectionDivider
import app.what.schedule.features.main.presentation.components.formatMoney
import app.what.schedule.features.main.presentation.components.sourceLabel
import app.what.schedule.features.main.presentation.screens.statistics.GrowthQuickActions
import kotlin.math.roundToInt

@Composable
fun DailyResultsScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    dashboard: DashboardUi,
    sources: DashboardSources,
    dailyResults: DailyResultsUi,
    form: DailyFormUi,
    onDateChange: (String) -> Unit,
    onDealsChange: (String) -> Unit,
    onVolumeChange: (String) -> Unit,
    onShareChange: (String) -> Unit,
    onProductsChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    ScreenColumn(contentPadding = contentPadding) {
        GrowthQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)

        DashboardCard {
            CardTitle(
                title = "Результаты дня",
                trailing = dailyResults.date ?: form.date
            )
            ResultRow("Оформлено сделок", dailyResults.totalDeals?.toString())
            ResultRow("Объём кредитов", dailyResults.totalVolumeRub?.let { formatMoney(it.toInt()) })
            ResultRow("Доп. продукты", dailyResults.additionalProductsCount?.toString())
            ResultRow(
                "Средняя доля банка",
                dailyResults.averageSharePercent?.let { "${it.toInt()}%" }
            )
            GlassPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelectTab(MainTab.Rating) }
            ) {
                Text("Детализация")
            }
            HintText("Источник: ${sourceLabel(sources.dailyResults)}")
        }

        DashboardCard {
            CardTitle(title = "Данные за день")
            HintText("Изменяйте показатели слайдерами и сохраняйте результат в backend.")
            DailySlider(
                label = "Сделки",
                suffix = "шт",
                value = form.dealCount.toFloatOrDefault(),
                range = 0f..30f,
                onValueChange = { onDealsChange(it.roundToInt().toString()) }
            )
            DailySlider(
                label = "Объём кредитов",
                suffix = "тыс ₽",
                value = (form.volumeRub.toFloatOrDefault() / 1000f).coerceAtLeast(0f),
                range = 0f..20000f,
                onValueChange = { onVolumeChange((it.roundToInt() * 1000).toString()) }
            )
            DailySlider(
                label = "Доля банка",
                suffix = "%",
                value = form.bankSharePercent.toFloatOrDefault(),
                range = 0f..100f,
                onValueChange = { onShareChange(it.roundToInt().toString()) }
            )
            DailySlider(
                label = "Доп. продукты",
                suffix = "шт",
                value = form.additionalProductsCount.toFloatOrDefault(),
                range = 0f..20f,
                onValueChange = { onProductsChange(it.roundToInt().toString()) }
            )
            GlassPrimaryButton(
                onClick = onSubmit,
                enabled = !form.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить результаты")
            }
            form.errorMessage?.let { HintText(it) }
            form.successMessage?.let { HintText(it) }
        }

        DashboardCard {
            CardTitle(title = "История записей", trailing = dailyResults.items.size.toString())
            if (dailyResults.items.isEmpty()) {
                EmptyState("Для выбранной даты записей пока нет")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    dailyResults.items.forEachIndexed { index, item ->
                        if (index > 0) {
                            SectionDivider()
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = item.createdAt,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            HintText("Сделки: ${item.dealCount}")
                            HintText("Объём: ${formatMoney(item.volumeRub.toInt())}")
                            HintText("Доля банка: ${item.bankSharePercent.toInt()}%")
                            HintText("Доп. продукты: ${item.additionalProductsCount}")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailySlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    suffix: String,
    onValueChange: (Float) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelText(label)
            Text(
                text = "${value.roundToInt()} $suffix",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onValueChange,
            valueRange = range,
            steps = 0,
            interactionSource = interactionSource,
            thumb = {
                Surface(
                    modifier = Modifier.size(if (isDragged) 22.dp else 18.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = if (isDragged) 6.dp else 2.dp
                ) {}
            }
        )
    }
}

@Composable
private fun ResultRow(
    title: String,
    value: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LabelText(title)
        Text(
            text = value ?: "Нет данных",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

private fun String.toFloatOrDefault(): Float = this.trim().toFloatOrNull() ?: 0f
