package app.what.schedule.features.schedule.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import app.what.schedule.features.schedule.domain.models.ScheduleEvent
import app.what.schedule.features.schedule.domain.models.ScheduleState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleView(
    state: State<ScheduleState>,
    listener: (ScheduleEvent) -> Unit
) {

}