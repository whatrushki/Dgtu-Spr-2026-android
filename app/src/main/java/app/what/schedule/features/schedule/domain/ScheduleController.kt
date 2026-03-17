package app.what.schedule.features.schedule.domain

import app.what.foundation.core.UIController
import app.what.schedule.data.local.settings.AppValues
import app.what.schedule.features.schedule.domain.models.ScheduleAction
import app.what.schedule.features.schedule.domain.models.ScheduleEvent
import app.what.schedule.features.schedule.domain.models.ScheduleState


class ScheduleController(
    private val settings: AppValues
) : UIController<ScheduleState, ScheduleAction, ScheduleEvent>(
    ScheduleState()
) {
    override fun obtainEvent(viewEvent: ScheduleEvent) = when (viewEvent) {
        ScheduleEvent.Init -> {}

    }

    val debugMode: Boolean
        get() = settings.debugMode.get() == true
}