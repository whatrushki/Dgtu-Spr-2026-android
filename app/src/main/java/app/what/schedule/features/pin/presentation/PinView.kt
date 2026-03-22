package app.what.schedule.features.pin.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.what.foundation.core.Listener
import app.what.schedule.R
import app.what.schedule.features.main.presentation.components.GlassSecondaryButton
import app.what.schedule.features.pin.domain.models.PinEvent
import app.what.schedule.features.pin.domain.models.PinStage
import app.what.schedule.features.pin.domain.models.PinState

private val KeySize = 72.dp
private val KeySpacing = 18.dp
private val IndicatorSize = 48.dp
private val IndicatorShape = RoundedCornerShape(16.dp)

private val PinGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2C8F74),
        Color(0xFF1B6662),
        Color(0xFF103A3D),
        Color(0xFF0A151A),
        Color(0xFF07090B)
    )
)

private val WelcomeGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF3E9632),
        Color(0xFF2D7425),
        Color(0xFF18461B),
        Color(0xFF0A120C)
    )
)

private val WelcomeHighlight = Brush.radialGradient(
    colors = listOf(
        Color(0x55E1BE63),
        Color(0x00E1BE63)
    )
)

@Composable
fun PinView(
    state: PinState,
    isOpeningMain: Boolean,
    listener: Listener<PinEvent>
) {
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isOpeningMain) 1f else 0f,
        animationSpec = tween(durationMillis = 380),
        label = "pin_overlay_alpha"
    )
    val overlayScale by animateFloatAsState(
        targetValue = if (isOpeningMain) 1f else 1.05f,
        animationSpec = tween(durationMillis = 820),
        label = "pin_overlay_scale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PinGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.weight(0.2f))
                SberWordmark()

                Spacer(modifier = Modifier.weight(0.7f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = titleFor(state.stage),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = subtitleFor(state.stage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xCCDCF1E2),
                        textAlign = TextAlign.Start
                    )
                    PinIndicators(
                        entered = state.enteredPin.length,
                        length = state.pinLength
                    )
                    state.errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Keypad(listener = listener)
            }

            if (overlayAlpha > 0.01f) {
                WelcomeTransitionOverlay(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(overlayAlpha)
                        .scale(overlayScale)
                )
            }
        }
    }
}

@Composable
private fun WelcomeTransitionOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(WelcomeGradient)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x22000000),
                            Color(0x66050A06)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 110.dp, top = 170.dp, end = 20.dp, bottom = 180.dp)
                .background(WelcomeHighlight)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 32.dp, top = 120.dp)
                .size(width = 205.dp, height = 330.dp)
                .clip(RoundedCornerShape(56.dp))
                .background(Color(0x44D9B75C))
                .border(1.dp, Color(0x42F7DD8A), RoundedCornerShape(56.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 26.dp, vertical = 42.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sber_logo),
                    contentDescription = "Сбер",
                    modifier = Modifier.fillMaxSize(0.62f),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Добро пожаловать",
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "в приложение",
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Сбер Бизнес",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Быстрый вход и персональное рабочее пространство",
                color = Color.White.copy(alpha = 0.74f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SberWordmark() {
    Image(
        painter = painterResource(id = R.drawable.sber_logo),
        contentDescription = "Сбер",
        modifier = Modifier
            .fillMaxWidth(0.72f)
            .height(64.dp),
        contentScale = ContentScale.Fit,
        alignment = Alignment.CenterStart
    )
}

@Composable
private fun PinIndicators(
    entered: Int,
    length: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(length) { index ->
            Surface(
                modifier = Modifier.size(IndicatorSize),
                shape = IndicatorShape,
                color = if (index < entered) {
                    Color(0x3342D66E)
                } else {
                    Color(0x22101818)
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = if (index < entered) {
                        Color(0x6642D66E)
                    } else {
                        Color.White.copy(alpha = 0.14f)
                    }
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (index < entered) "•" else "",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun Keypad(
    listener: Listener<PinEvent>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KeySpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        ).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { digit ->
                    PinDigitButton(
                        label = digit.toString(),
                        onClick = { listener(PinEvent.DigitPressed(digit)) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(KeySize))
            PinDigitButton(
                label = "0",
                onClick = { listener(PinEvent.DigitPressed(0)) }
            )
            GlassSecondaryButton(
                onClick = { listener(PinEvent.DeletePressed) },
                modifier = Modifier.size(KeySize),
                height = KeySize
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить",
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun PinDigitButton(
    label: String,
    onClick: () -> Unit
) {
    GlassSecondaryButton(
        onClick = onClick,
        modifier = Modifier.size(KeySize),
        height = KeySize
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.94f),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun titleFor(stage: PinStage): String = when (stage) {
    PinStage.CREATE -> "Создайте PIN"
    PinStage.CONFIRM -> "Повторите PIN"
    PinStage.ENTER -> "Введите PIN"
}

private fun subtitleFor(stage: PinStage): String = when (stage) {
    PinStage.CREATE -> "Задайте 5-значный код для входа в приложение."
    PinStage.CONFIRM -> "Введите тот же PIN ещё раз, чтобы сохранить его."
    PinStage.ENTER -> "Используйте ваш 5-значный PIN для входа."
}
