package app.what.schedule.features.main.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.schedule.features.main.domain.models.ProfileUi
import app.what.schedule.features.main.presentation.components.AccentText
import app.what.schedule.features.main.presentation.components.CardTitle
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.GlassSecondaryButton
import app.what.schedule.features.main.presentation.components.HintText
import app.what.schedule.features.main.presentation.components.LabelText
import app.what.schedule.features.main.presentation.components.ScreenColumn
import app.what.schedule.features.main.presentation.components.SectionDivider

@Composable
fun ProfileScreen(
    contentPadding: PaddingValues,
    profile: ProfileUi,
    onLogout: () -> Unit
) {
    ScreenColumn(contentPadding = contentPadding) {
        DashboardCard {
            CardTitle(title = "Профиль сотрудника")
            Text(
                text = profile.fullName ?: "Нет данных",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            AccentText(profile.level ?: "Уровень не указан")
            HintText(profile.position ?: "Должность не указана")
        }

        DashboardCard {
            CardTitle(title = "Основная информация")
            ProfileField("Sber ID", profile.sberId)
            ProfileField("ID сотрудника", profile.employeeId)
            ProfileField("Роль", profile.role)
            ProfileField("Код ДЦ", profile.dealerCenterCode)
            ProfileField("Дилерский центр", profile.dealerCenterName)
        }

        DashboardCard {
            CardTitle(title = "Контакты")
            ProfileField("Телефон", profile.phone)
            ProfileField("Email", profile.email)
            ProfileField("Дата регистрации", profile.registeredAt)
        }

        DashboardCard {
            CardTitle(title = "Действия")
            HintText("Выход завершит текущую сессию и вернёт на экран авторизации.")
            SectionDivider()
            GlassSecondaryButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выйти")
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelText(label)
            Text(
                text = value?.takeIf { it.isNotBlank() } ?: "Нет данных",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        SectionDivider()
    }
}
