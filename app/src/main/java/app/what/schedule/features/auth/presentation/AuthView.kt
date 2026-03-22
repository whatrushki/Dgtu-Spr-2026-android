package app.what.schedule.features.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.what.foundation.core.Listener
import app.what.schedule.R
import app.what.schedule.features.auth.domain.models.AuthEvent
import app.what.schedule.features.auth.domain.models.AuthMode
import app.what.schedule.features.auth.domain.models.AuthState
import app.what.schedule.features.main.presentation.components.DashboardCard
import app.what.schedule.features.main.presentation.components.EmptyState
import app.what.schedule.features.main.presentation.components.GlassPrimaryButton
import app.what.schedule.features.main.presentation.components.GlassSecondaryButton
import app.what.schedule.features.main.presentation.components.OuterPadding
import app.what.schedule.features.main.presentation.components.SectionGap

private val Roles = listOf(
    "SALES_MANAGER" to "Менеджер по продажам",
    "KSO" to "КСО",
    "ROP" to "РОП",
    "DIRECTOR" to "Директор",
    "ADMIN" to "Администратор"
)

private val AuthGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2C8F74),
        Color(0xFF1B6662),
        Color(0xFF103A3D),
        Color(0xFF0A151A),
        Color(0xFF07090B)
    )
)

@Composable
fun AuthView(
    state: AuthState,
    listener: Listener<AuthEvent>
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AuthGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = OuterPadding, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(SectionGap)
            ) {
                SberLogoHeader()

                DashboardCard {
                    Text(
                        text = "Авторизация",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Вход и регистрация в темном стеклянном стиле с единым Sber ID.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xDDE6F3EC)
                    )
                }

                ModeSwitcher(
                    mode = state.mode,
                    onSwitch = { listener(AuthEvent.SwitchMode(it)) }
                )

                state.errorMessage?.let { message ->
                    DashboardCard {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        if (state.dealerCenters.isEmpty() && !state.isLoadingCenters) {
                            GlassSecondaryButton(onClick = { listener(AuthEvent.RetryCenters) }) {
                                Text("Повторить")
                            }
                        }
                    }
                }

                if (state.mode == AuthMode.Login) {
                    LoginCard(state = state, listener = listener)
                } else {
                    RegisterCard(state = state, listener = listener)
                }
            }
        }
    }
}

@Composable
private fun SberLogoHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sber_logo),
            contentDescription = "Сбер",
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .height(72.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun ModeSwitcher(
    mode: AuthMode,
    onSwitch: (AuthMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(AuthMode.Login to "Вход", AuthMode.Register to "Регистрация").forEach { (item, title) ->
            FilterChip(
                selected = item == mode,
                onClick = { onSwitch(item) },
                label = { Text(title) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0x2A0A1611),
                    labelColor = Color(0xE5EEF5F0),
                    selectedContainerColor = Color(0x2A1E6F30),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun LoginCard(
    state: AuthState,
    listener: Listener<AuthEvent>
) {
    DashboardCard {
        Text(
            text = "Вход по Sber ID",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        DealerTextField(
            value = state.loginSberId,
            onValueChange = { listener(AuthEvent.UpdateLoginSberId(it)) },
            label = "Sber ID"
        )
        DealerTextField(
            value = state.loginPassword,
            onValueChange = { listener(AuthEvent.UpdateLoginPassword(it)) },
            label = "Пароль",
            password = true
        )
        GlassPrimaryButton(
            onClick = { listener(AuthEvent.SubmitLogin) },
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            } else {
                Text("Авторизоваться")
            }
        }
    }
}

@Composable
private fun RegisterCard(
    state: AuthState,
    listener: Listener<AuthEvent>
) {
    DashboardCard {
        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        if (state.isLoadingCenters) {
            CircularProgressIndicator()
        } else if (state.dealerCenters.isEmpty()) {
            EmptyState("Не удалось загрузить дилерские центры")
        } else {
            DealerTextField(
                value = state.registerFullName,
                onValueChange = { listener(AuthEvent.UpdateRegisterFullName(it)) },
                label = "ФИО"
            )
            DealerTextField(
                value = state.registerSberId,
                onValueChange = { listener(AuthEvent.UpdateRegisterSberId(it)) },
                label = "Sber ID"
            )
            DealerDropdown(
                label = "Дилерский центр",
                value = state.selectedDealerCenter?.let { "${it.title} (${it.subtitle})" }.orEmpty(),
                items = state.dealerCenters.map { it.id to "${it.title} (${it.subtitle})" },
                onSelect = { listener(AuthEvent.SelectDealerCenter(it)) }
            )
            DealerDropdown(
                label = "Роль",
                value = Roles.firstOrNull { it.first == state.selectedRole }?.second.orEmpty(),
                items = Roles,
                onSelect = { listener(AuthEvent.SelectRole(it)) }
            )
            DealerTextField(
                value = state.registerPosition,
                onValueChange = { listener(AuthEvent.UpdateRegisterPosition(it)) },
                label = "Должность"
            )
            DealerTextField(
                value = state.registerPhone,
                onValueChange = { listener(AuthEvent.UpdateRegisterPhone(it)) },
                label = "Телефон",
                keyboardType = KeyboardType.Phone
            )
            DealerTextField(
                value = state.registerEmail,
                onValueChange = { listener(AuthEvent.UpdateRegisterEmail(it)) },
                label = "Email",
                keyboardType = KeyboardType.Email
            )
            DealerTextField(
                value = state.registerPassword,
                onValueChange = { listener(AuthEvent.UpdateRegisterPassword(it)) },
                label = "Пароль",
                password = true
            )
            GlassPrimaryButton(
                onClick = { listener(AuthEvent.SubmitRegister) },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Text("Зарегистрироваться")
                }
            }
        }
    }
}

@Composable
private fun DealerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    password: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (password) {
            PasswordVisualTransformation()
        } else {
            androidx.compose.ui.text.input.VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = authFieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DealerDropdown(
    label: String,
    value: String,
    items: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = authFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { (id, title) ->
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        expanded = false
                        onSelect(id)
                    }
                )
            }
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0x220A1611),
    unfocusedContainerColor = Color(0x1A0A1611),
    focusedBorderColor = Color(0x8834C759),
    unfocusedBorderColor = Color(0x33E5F1EA),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color(0xEAF5EE),
    focusedLabelColor = Color(0xCCDCF1E2),
    unfocusedLabelColor = Color(0xB3DCF1E2),
    cursorColor = Color(0xFF34C759)
)
