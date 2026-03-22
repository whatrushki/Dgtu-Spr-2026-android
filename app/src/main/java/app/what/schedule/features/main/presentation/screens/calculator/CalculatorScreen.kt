package app.what.schedule.features.main.presentation.screens.calculator

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
import app.what.foundation.ui.controllers.rememberDialogController
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.domain.models.ScenarioFormUi
import app.what.schedule.features.main.domain.models.ScenarioResultUi
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.GlassPrimaryButton
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.LabelText
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.formatMoney
import app.what.schedule.features.main.presentation.screens.statistics.GrowthQuickActions
import kotlin.math.roundToInt

private const val DEFAULT_VOLUME_PLAN = 10.0
private const val DEFAULT_DEALS_PLAN = 10.0
private const val DEFAULT_SHARE_TARGET = 50.0

@Composable
fun CalculatorScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    form: ScenarioFormUi,
    onVolumeFactChange: (String) -> Unit,
    onDealsFactChange: (String) -> Unit,
    onShareFactChange: (String) -> Unit,
    onApprovedChange: (String) -> Unit,
    onSubmittedChange: (String) -> Unit
) {
    val dialog = rememberDialogController()
    val preview = calculateScenarioPreview(form)

    ScreenColumn(contentPadding = contentPadding) {
        GrowthQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)

        DashboardCard {
            CardTitle(title = "Сценарный калькулятор")
            HintText("Показываем только фактические показатели. Целевые пороги зашиты по правилам рейтинга.")

            ScenarioSlider(
                label = "Объём",
                suffix = "млн ₽",
                value = form.volumeFactMln.toFloatOrDefault(),
                range = 0f..20f,
                onValueChange = { onVolumeFactChange(it.roundToInt().toString()) }
            )
            ScenarioSlider(
                label = "Сделки",
                suffix = "шт",
                value = form.dealsFact.toFloatOrDefault(),
                range = 0f..30f,
                onValueChange = { onDealsFactChange(it.roundToInt().toString()) }
            )
            ScenarioSlider(
                label = "Доля банка",
                suffix = "%",
                value = form.bankShareFactPercent.toFloatOrDefault(),
                range = 0f..100f,
                onValueChange = { onShareFactChange(it.roundToInt().toString()) }
            )
            ScenarioSlider(
                label = "Одобрено заявок",
                suffix = "шт",
                value = form.approvedApplications.toFloatOrDefault(),
                range = 0f..40f,
                onValueChange = { onApprovedChange(it.roundToInt().toString()) }
            )
            ScenarioSlider(
                label = "Подано заявок",
                suffix = "шт",
                value = form.submittedApplications.toFloatOrDefault().coerceAtLeast(1f),
                range = 1f..40f,
                onValueChange = { onSubmittedChange(it.roundToInt().toString()) }
            )

            GlassPrimaryButton(
                onClick = {
                    dialog.open(full = true) {
                        ScenarioResultDialog(result = preview)
                    }
                }
            ) {
                Text("Открыть расчёт")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScenarioSlider(
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
private fun ScenarioResultDialog(result: ScenarioResultUi) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CardTitle(title = "Результат сценария")
        MetricBlock("Новый уровень", result.level)
        MetricBlock("Итоговый рейтинг", String.format("%.1f балла", result.totalPoints))
        MetricBlock("Прогресс", "${result.progressPercent}%")
        MetricBlock("Индекс объёма", String.format("%.1f", result.volumeIndex))
        MetricBlock("Индекс сделок", String.format("%.1f", result.dealsIndex))
        MetricBlock("Индекс доли", String.format("%.1f", result.shareIndex))
        MetricBlock("Индекс конверсии", String.format("%.1f", result.conversionIndex))
        MetricBlock(
            "Переход уровня",
            if (result.monthlyTransitionEligible) {
                "Порог достигнут, переход проверяется раз в месяц"
            } else {
                "Порог уровня пока не достигнут"
            }
        )
        MetricBlock("Новый доход", formatMoney(result.simulatedIncomeRub.toInt()))
        MetricBlock("Новая экономия", formatMoney(result.simulatedMortgageSavingRub.toInt()))
    }
}

@Composable
private fun MetricBlock(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LabelText(label)
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun calculateScenarioPreview(form: ScenarioFormUi): ScenarioResultUi {
    val volumeFact = form.volumeFactMln.trim().toDoubleOrNull() ?: 0.0
    val dealsFact = form.dealsFact.trim().toDoubleOrNull() ?: 0.0
    val shareFact = form.bankShareFactPercent.trim().toDoubleOrNull() ?: 0.0
    val approved = form.approvedApplications.trim().toDoubleOrNull() ?: 0.0
    val submitted = (form.submittedApplications.trim().toDoubleOrNull() ?: 1.0).coerceAtLeast(1.0)

    val volumeIndex = ((volumeFact / DEFAULT_VOLUME_PLAN) * 100.0).coerceAtMost(120.0)
    val dealsIndex = (dealsFact / DEFAULT_DEALS_PLAN) * 100.0
    val shareIndex = (shareFact / DEFAULT_SHARE_TARGET) * 100.0
    val conversionIndex = (approved / submitted) * 100.0
    val totalScore = 0.35 * volumeIndex +
        0.25 * dealsIndex +
        0.25 * shareIndex +
        0.15 * conversionIndex
    val normalizedScore = totalScore.coerceAtLeast(0.0)
    val level = when {
        normalizedScore >= 90.0 -> "Black"
        normalizedScore >= 70.0 -> "Gold"
        else -> "Silver"
    }

    return ScenarioResultUi(
        level = level,
        totalPoints = normalizedScore,
        progressPercent = ((normalizedScore / 90.0) * 100.0).coerceIn(0.0, 100.0).roundToInt(),
        volumeIndex = volumeIndex,
        dealsIndex = dealsIndex,
        shareIndex = shareIndex,
        conversionIndex = conversionIndex,
        monthlyTransitionEligible = normalizedScore >= 70.0,
        simulatedIncomeRub = ((normalizedScore / 100.0) * 540_000).toLong(),
        simulatedMortgageSavingRub = ((normalizedScore / 100.0) * 740_000).toLong()
    )
}

private fun String.toFloatOrDefault(): Float = this.trim().toFloatOrNull() ?: 0f
