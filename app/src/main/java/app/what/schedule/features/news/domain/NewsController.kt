package app.what.schedule.features.news.domain

import app.what.foundation.core.UIController
import app.what.schedule.data.local.settings.AppValues
import app.what.schedule.features.news.domain.models.NewsAction
import app.what.schedule.features.news.domain.models.NewsEvent
import app.what.schedule.features.news.domain.models.NewsState


class NewsController(
    private val settings: AppValues
) : UIController<NewsState, NewsAction, NewsEvent>(
    NewsState()
) {
    override fun obtainEvent(viewEvent: NewsEvent) = when (viewEvent) {
        else -> Unit
    }

    val debugMode: Boolean
        get() = settings.debugMode.get() == true
}