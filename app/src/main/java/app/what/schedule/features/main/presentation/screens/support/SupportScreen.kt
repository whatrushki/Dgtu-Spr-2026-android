package app.what.schedule.features.main.presentation.screens.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.foundation.ui.controllers.rememberDialogController
import app.what.schedule.features.main.domain.models.AssistantMessageUi
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.features.main.domain.models.SupportComposerUi
import app.what.schedule.features.main.domain.models.SupportTicketUi
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.GlassPrimaryButton
import app.what.schedule.features.main.presentation.components.GlassSecondaryButton
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.TextPrimary
import app.what.schedule.utils.StringUtils.parseMarkdown

private val TicketCategories = listOf("GENERAL", "LEARNING", "TICKETS", "TECH")
private val TicketPriorities = listOf("LOW", "NORMAL", "HIGH")

@Composable
fun SupportScreen(
    contentPadding: PaddingValues,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    tickets: List<SupportTicketUi>,
    assistantHistory: List<AssistantMessageUi>,
    composer: SupportComposerUi,
    onSubjectChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onAssistantQuestionChange: (String) -> Unit,
    onAskAssistant: () -> Unit,
    onApplySuggestion: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val dialog = rememberDialogController()
    val currentHistory by rememberUpdatedState(assistantHistory)
    val currentComposer by rememberUpdatedState(composer)
    val currentTickets by rememberUpdatedState(tickets)

    ScreenColumn(contentPadding = contentPadding) {
        ServiceQuickActions(selectedTab = selectedTab, onSelectTab = onSelectTab)

        DashboardCard {
            CardTitle(title = "GigaChat")
            HintText("Откройте нейроассистента в отдельном окне и продолжайте переписку как в современном чате.")
            HintText("Сообщений в истории: ${assistantHistory.size}")
            GlassPrimaryButton(
                onClick = {
                    dialog.open(full = true) {
                        AssistantChatDialog(
                            history = currentHistory,
                            composer = currentComposer,
                            onQuestionChange = onAssistantQuestionChange,
                            onAskAssistant = onAskAssistant,
                            onApplySuggestion = onApplySuggestion
                        )
                    }
                }
            ) {
                Text("Открыть GigaChat")
            }
        }

        DashboardCard {
            CardTitle(title = "Тикеты поддержки")
            HintText("Создавайте обращения отдельно от чата с ассистентом и ведите их историю в отдельном окне.")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassPrimaryButton(
                    onClick = {
                        dialog.open(full = true) {
                            TicketComposerDialog(
                                composer = currentComposer,
                                onSubjectChange = onSubjectChange,
                                onMessageChange = onMessageChange,
                                onCategoryChange = onCategoryChange,
                                onPriorityChange = onPriorityChange,
                                onSubmit = onSubmit
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Новый тикет")
                }
                GlassSecondaryButton(
                    onClick = {
                        dialog.open(full = true) {
                            TicketHistoryDialog(tickets = currentTickets)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("История")
                }
            }
        }
    }
}

@Composable
private fun AssistantChatDialog(
    history: List<AssistantMessageUi>,
    composer: SupportComposerUi,
    onQuestionChange: (String) -> Unit,
    onAskAssistant: () -> Unit,
    onApplySuggestion: (String) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 560.dp, max = 780.dp)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardTitle(title = "GigaChat", trailing = history.size.toString())
        HintText("Задайте вопрос про KPI, обучение, обращения клиентов или внутренние процессы.")

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 16.dp)
            ) {
                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState("История диалога пока пустая")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(history, key = { it.id }) { message ->
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                UserBubble(
                                    text = message.question,
                                    timestamp = message.createdAt
                                )
                                AssistantBubble(
                                    answer = message.answer,
                                    suggestions = message.suggestions,
                                    onApplySuggestion = onApplySuggestion
                                )
                            }
                        }
                    }
                }
            }
        }

        composer.assistantErrorMessage?.let { HintText(it) }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.16f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = composer.assistantQuestion,
                    onValueChange = onQuestionChange,
                    placeholder = { Text("Введите запрос") },
                    modifier = Modifier.weight(1f),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                    singleLine = false
                )
                GlassPrimaryButton(
                    onClick = onAskAssistant,
                    enabled = !composer.isAssistantLoading,
                    modifier = Modifier.width(52.dp),
                    height = 52.dp
                ) {
                    if (composer.isAssistantLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Отправить",
                            modifier = Modifier.size(18.dp),
                            tint = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserBubble(
    text: String,
    timestamp: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.82f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                HintText(timestamp)
            }
        }
    }
}

@Composable
private fun AssistantBubble(
    answer: String,
    suggestions: List<String>,
    onApplySuggestion: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val markdown = remember(answer, colors) {
        parseMarkdown(answer, colors)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.88f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = markdown,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (suggestions.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestions.take(2).forEach { suggestion ->
                            GlassSecondaryButton(
                                onClick = { onApplySuggestion(suggestion) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(suggestion, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketComposerDialog(
    composer: SupportComposerUi,
    onSubjectChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardTitle(title = "Новый тикет")
        OutlinedTextField(
            value = composer.subject,
            onValueChange = onSubjectChange,
            label = { Text("Тема") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        TicketChipRow(
            title = "Категория",
            values = TicketCategories,
            selected = composer.category,
            onSelected = onCategoryChange
        )
        TicketChipRow(
            title = "Приоритет",
            values = TicketPriorities,
            selected = composer.priority,
            onSelected = onPriorityChange
        )
        OutlinedTextField(
            value = composer.message,
            onValueChange = onMessageChange,
            label = { Text("Сообщение") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )
        GlassPrimaryButton(
            onClick = onSubmit,
            enabled = !composer.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (composer.isSubmitting) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            } else {
                Text("Отправить тикет")
            }
        }
        composer.errorMessage?.let { HintText(it) }
        composer.successMessage?.let { HintText(it) }
    }
}

@Composable
private fun TicketHistoryDialog(
    tickets: List<SupportTicketUi>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardTitle(title = "История тикетов", trailing = tickets.size.toString())
        if (tickets.isEmpty()) {
            EmptyState("Тикетов пока нет")
        } else {
            tickets.forEachIndexed { index, ticket ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = ticket.subject,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    ticket.category?.let { HintText("Категория: $it") }
                    ticket.priority?.let { HintText("Приоритет: $it") }
                    HintText("Статус: ${ticket.status}")
                    HintText(ticket.message)
                    ticket.resolution?.takeIf { it.isNotBlank() }?.let { HintText("Решение: $it") }
                    HintText(ticket.updatedAt ?: ticket.createdAt)
                }
            }
        }
    }
}

@Composable
private fun TicketChipRow(
    title: String,
    values: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HintText(title)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEach { value ->
                FilterChip(
                    selected = value == selected,
                    onClick = { onSelected(value) },
                    label = { Text(value) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
                    )
                )
            }
        }
    }
}
