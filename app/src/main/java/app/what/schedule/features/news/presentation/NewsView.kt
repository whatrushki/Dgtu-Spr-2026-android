package app.what.schedule.features.news.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import app.what.schedule.features.news.domain.models.NewsEvent
import app.what.schedule.features.news.domain.models.NewsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsView(
    state: NewsState,
    listener: (NewsEvent) -> Unit
) {

}