package app.what.schedule.features.news.domain.models


sealed interface NewsEvent {
    object Init : NewsEvent
}