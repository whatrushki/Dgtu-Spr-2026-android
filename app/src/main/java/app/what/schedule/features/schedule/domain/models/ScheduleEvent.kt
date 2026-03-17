package app.what.schedule.features.schedule.domain.models

sealed interface ScheduleEvent {
    object Init : ScheduleEvent
}