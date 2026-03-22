package app.what.schedule.features.main.presentation.screens.learning

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.foundation.ui.controllers.rememberDialogController
import app.what.schedule.features.main.domain.models.LearningAttemptUi
import app.what.schedule.features.main.domain.models.LearningModuleUi
import app.what.schedule.features.main.domain.models.LearningQuizQuestionUi
import app.what.schedule.features.main.domain.models.LearningQuizUi
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.GlassPrimaryButton
import app.what.schedule.features.main.presentation.components.GlassSecondaryButton
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.LabelText
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.screens.support.ServiceQuickActions
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LearningScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    modules: List<LearningModuleUi>,
    attempts: List<LearningAttemptUi>,
    activeQuiz: LearningQuizUi?,
    pendingModuleIds: Set<String>,
    onOpenQuiz: (String) -> Unit,
    onUpdateQuizAnswer: (String, String) -> Unit,
    onSubmitQuiz: () -> Unit,
    onCloseQuiz: () -> Unit,
    onCompleteModule: (String) -> Unit
) {
    val dialog = rememberDialogController()
    val currentQuiz by rememberUpdatedState(activeQuiz)
    val currentAttempts by rememberUpdatedState(attempts)

    ScreenColumn(contentPadding = contentPadding) {
        ServiceQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)

        DashboardCard {
            CardTitle(title = "Обучающие материалы", trailing = modules.size.toString())
            HintText("Модули и квизы загружаются с backend и проходятся в одном сценарии обучения.")
        }

        if (modules.isEmpty()) {
            DashboardCard {
                EmptyState("Модули обучения пока не загружены")
            }
        } else {
            modules.forEach { module ->
                DashboardCard {
                    ModuleCard(
                        module = module,
                        isPending = pendingModuleIds.contains(module.id),
                        onStart = {
                            if (module.quizAvailable) {
                                onOpenQuiz(module.id)
                                dialog.open(full = true) {
                                    QuizSheet(
                                        quiz = currentQuiz,
                                        onUpdateQuizAnswer = onUpdateQuizAnswer,
                                        onSubmitQuiz = onSubmitQuiz,
                                        onCloseQuiz = onCloseQuiz
                                    )
                                }
                            } else {
                                onCompleteModule(module.id)
                            }
                        }
                    )
                }
            }
        }

        DashboardCard {
            CardTitle(title = "История попыток", trailing = attempts.size.toString())
            if (attempts.isEmpty()) {
                EmptyState("Попыток по обучению пока нет")
            } else {
                GlassSecondaryButton(
                    onClick = {
                        dialog.open(full = true) {
                            AttemptsDialog(attempts = currentAttempts)
                        }
                    }
                ) {
                    Text("Открыть историю")
                }
            }
        }
    }
}

@Composable
private fun ModuleCard(
    module: LearningModuleUi,
    isPending: Boolean,
    onStart: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CardTitle(title = module.title)
        HintText(module.description)
        module.category?.let { HintText("Категория: $it") }
        module.difficulty?.let { HintText("Сложность: $it") }
        module.format?.let { HintText("Формат: $it") }
        module.durationMinutes?.let { HintText("Длительность: $it мин") }
        module.progressPercent?.let { HintText("Прогресс: $it%") }
        HintText("Баллы за прохождение: +${module.pointsReward}")
        HintText(if (module.completed) "Статус: пройден" else "Статус: не пройден")

        GlassPrimaryButton(
            onClick = onStart,
            enabled = !isPending && !module.completed,
            modifier = Modifier.fillMaxWidth()
        ) {
            when {
                isPending -> CircularProgressIndicator(strokeWidth = 2.dp)
                module.completed -> Text("Модуль пройден")
                module.quizAvailable -> Text("Пройти квиз")
                else -> Text("Завершить материал")
            }
        }
    }
}

@Composable
private fun QuizSheet(
    quiz: LearningQuizUi?,
    onUpdateQuizAnswer: (String, String) -> Unit,
    onSubmitQuiz: () -> Unit,
    onCloseQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 560.dp, max = 820.dp)
            .padding(20.dp)
            .clipToBounds(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardTitle(title = quiz?.moduleTitle ?: "Квиз")

        if (quiz == null || quiz.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        quiz.description?.let { HintText(it) }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            quiz.attemptsLeft?.let { QuizMetaChip("Попыток: $it", Modifier.weight(1f)) }
                            quiz.timeLimitMinutes?.let { QuizMetaChip("Лимит: $it мин", Modifier.weight(1f)) }
                        }
                    }
                    item {
                        quiz.errorMessage?.let { HintText(it) }
                    }
                    itemsIndexed(quiz.questions, key = { _, item -> item.id }) { index, question ->
                        QuizQuestionCard(
                            index = index,
                            question = question,
                            onUpdateQuizAnswer = onUpdateQuizAnswer
                        )
                    }
                    quiz.result?.let { result ->
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    CardTitle(title = if (result.passed) "Победа" else "Результат")
                                    HintText("Верных ответов: ${result.correctAnswers}/${result.totalQuestions}")
                                    HintText("Баллы: ${result.score}")
                                    HintText(if (result.passed) "Статус: пройден" else "Статус: не пройден")
                                    HintText("Начислено: +${result.pointsAwarded}")
                                    result.summary?.let { HintText(it) }
                                    HintText(result.completedAt)
                                }
                            }
                        }
                    }
                }

                if (quiz.result?.passed == true) {
                    VictoryFireworks(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassPrimaryButton(
                onClick = onSubmitQuiz,
                enabled = quiz?.isSubmitting != true && quiz != null,
                modifier = Modifier.weight(1f)
            ) {
                if (quiz?.isSubmitting == true) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Text("Отправить")
                }
            }
            GlassSecondaryButton(
                onClick = onCloseQuiz,
                modifier = Modifier.weight(1f)
            ) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun QuizMetaChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun QuizQuestionCard(
    index: Int,
    question: LearningQuizQuestionUi,
    onUpdateQuizAnswer: (String, String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LabelText("Вопрос ${index + 1}")
            Text(
                text = question.question,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            HintText(if (question.multiple) "Можно выбрать несколько вариантов" else "Выберите один вариант")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEach { option ->
                    FilterChip(
                        selected = option in question.selectedAnswers,
                        onClick = { onUpdateQuizAnswer(question.id, option) },
                        label = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.24f),
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.26f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
            question.explanation?.takeIf { it.isNotBlank() }?.let {
                HintText(it)
            }
        }
    }
}

@Composable
private fun VictoryFireworks(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "victory")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "victory-progress"
    )
    val bursts = remember {
        listOf(
            FireworkBurst(0.16f, 0.2f, Color(0xFFFFD166), Color(0xFFFF7B00)),
            FireworkBurst(0.5f, 0.14f, Color(0xFF72DDF7), Color(0xFF219EBC)),
            FireworkBurst(0.84f, 0.22f, Color(0xFFB8F2A1), Color(0xFF2A9D8F))
        )
    }

    Canvas(modifier = modifier) {
        val overlayHeight = size.height * 0.42f
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                endY = overlayHeight
            ),
            size = Size(size.width, overlayHeight)
        )

        bursts.forEach { burst ->
            val center = Offset(size.width * burst.x, size.height * burst.y)
            val radius = 28.dp.toPx() + progress * 80.dp.toPx()
            val alpha = (1f - progress).coerceIn(0f, 1f)

            drawCircle(
                color = burst.inner.copy(alpha = alpha * 0.45f),
                radius = radius * 0.45f,
                center = center
            )

            repeat(12) { index ->
                val angle = index / 12f * (Math.PI * 2.0)
                val direction = Offset(cos(angle).toFloat(), sin(angle).toFloat())
                val end = center + Offset(direction.x * radius, direction.y * radius)
                drawLine(
                    color = burst.outer.copy(alpha = alpha),
                    start = center,
                    end = end,
                    strokeWidth = 3.dp.toPx()
                )
            }

            drawCircle(
                color = burst.outer.copy(alpha = alpha),
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

private data class FireworkBurst(
    val x: Float,
    val y: Float,
    val inner: Color,
    val outer: Color
)

@Composable
private fun AttemptsDialog(
    attempts: List<LearningAttemptUi>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardTitle(title = "История попыток", trailing = attempts.size.toString())
        attempts.forEachIndexed { index, attempt ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = attempt.moduleTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                HintText("Результат: ${attempt.correctAnswers}/${attempt.totalQuestions}")
                HintText("Баллы: ${attempt.score}")
                HintText(if (attempt.passed) "Статус: пройден" else "Статус: не пройден")
                HintText("Награда: +${attempt.pointsAwarded}")
                HintText(attempt.completedAt)
            }
        }
    }
}
