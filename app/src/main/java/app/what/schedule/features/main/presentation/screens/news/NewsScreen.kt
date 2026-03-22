package app.what.schedule.features.main.presentation.screens.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.foundation.ui.controllers.rememberDialogController
import app.what.schedule.features.main.domain.models.NewsUi
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.GlassSecondaryButton
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.ScreenColumn

@Composable
fun NewsScreen(
    contentPadding: PaddingValues,
    news: List<NewsUi>
) {
    val dialog = rememberDialogController()

    ScreenColumn(contentPadding = contentPadding) {
        if (news.isEmpty()) {
            DashboardCard {
                CardTitle(title = "Новости и изменения")
                EmptyState("Новостей пока нет")
            }
        } else {
            news.forEach { item ->
                DashboardCard {
                    CardTitle(title = item.title)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HintText(item.publishedAt)
                        item.category?.let { HintText(it) }
                        item.targetLevel?.let { HintText(it) }
                    }
                    item.summary?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (item.tags.isNotEmpty()) {
                        HintText(item.tags.joinToString(prefix = "#", separator = " #"))
                    }
                    GlassSecondaryButton(
                        onClick = {
                            dialog.open(full = true) {
                                NewsDialog(item)
                            }
                        }
                    ) {
                        Text("Открыть новость")
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsDialog(item: NewsUi) {
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardTitle(title = item.title)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HintText(item.publishedAt)
            item.category?.let { HintText(it) }
            item.targetLevel?.let { HintText(it) }
        }
        item.summary?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = item.content,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
        if (item.tags.isNotEmpty()) {
            HintText(item.tags.joinToString(prefix = "#", separator = " #"))
        }
    }
}
