package app.what.schedule.features.main.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.what.schedule.features.main.domain.models.MainTab
import app.what.schedule.ui.theme.icons.WHATIcons
import app.what.schedule.ui.theme.icons.filled.Features
import app.what.schedule.ui.theme.icons.filled.News
import app.what.schedule.ui.theme.icons.filled.Person
import app.what.schedule.ui.theme.icons.filled.Question
import app.what.schedule.ui.theme.icons.filled.Run

val GridGap = 12.dp
val SectionGap = 24.dp
val OuterPadding = 20.dp
val CardInnerPadding = 20.dp
val TextPrimary = Color(0xFFF4F7F4)
val TextSecondary = Color(0xFFB4BDB6)

private val AccentGreen = Color(0xFF34C759)
private val GlassStroke = Color(0x1FD8ECE0)
private val GlassFill = Color(0x12F4FFF6)
private val GlassGlow = Color(0x26000000)
private val DarkGlassFill = Color(0x44070D0A)
private val DarkGlassStrong = Color(0x660A120D)
private val DarkGlassBorder = Color(0x22D8F2E1)
private val PanelGlassFill = Color(0x68040907)
private val PanelGlassBorder = Color(0x1EDCF2E4)

private val ShellGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2D8F74),
        Color(0xFF1D6C66),
        Color(0xFF114B4A),
        Color(0xFF0C1E1F),
        Color(0xFF07090B)
    )
)

private val TopBarShade = Brush.verticalGradient(
    colors = listOf(
        Color(0x52060808),
        Color(0x26080A09),
        Color(0x12090C0A),
        Color.Transparent
    )
)

private val CardTint = Brush.linearGradient(
    colors = listOf(
        Color(0x1834C759),
        Color(0x081F9444)
    )
)

private const val NO_DATA = "Нет данных"

@Composable
fun MainChrome(
    selectedTab: MainTab,
    title: String,
    onSearchClick: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenProfile: () -> Unit,
    onSelectRootTab: (MainTab) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val rootItems = listOf(
        RootNavItem(
            label = "Главный",
            icon = Icons.Filled.Home,
            tab = MainTab.Status,
            selected = selectedTab == MainTab.Status
        ),
        RootNavItem(
            label = "Рост",
            icon = WHATIcons.Features,
            tab = MainTab.Rating,
            selected = selectedTab in setOf(MainTab.Rating, MainTab.Calculator, MainTab.DailyResults)
        ),
        RootNavItem(
            label = "Привилегии",
            icon = WHATIcons.Run,
            tab = MainTab.Privileges,
            selected = selectedTab in setOf(MainTab.Privileges, MainTab.FinancialEffect, MainTab.Tasks, MainTab.Leaderboard)
        ),
        RootNavItem(
            label = "Сервис",
            icon = WHATIcons.Question,
            tab = MainTab.Learning,
            selected = selectedTab in setOf(MainTab.Learning, MainTab.Support)
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MainTopBar(
                onSearchClick = onSearchClick,
                onOpenNews = onOpenNews,
                onOpenProfile = onOpenProfile
            )
        },
        bottomBar = {
            BottomNavigationBar(
                rootItems = rootItems,
                onSelectRootTab = onSelectRootTab
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ShellGradient)
        ) {
            content(innerPadding)
        }
    }
}

private data class RootNavItem(
    val label: String,
    val icon: ImageVector,
    val tab: MainTab,
    val selected: Boolean
)

@Composable
private fun MainTopBar(
    onSearchClick: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarShade)
            .zIndex(1f)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(
                icon = WHATIcons.Person,
                onClick = onOpenProfile
            )
            SearchPill(
                modifier = Modifier.weight(1f),
                placeholder = "Поиск с GigaChat",
                onClick = onSearchClick
            )
            HeaderIconButton(
                icon = WHATIcons.News,
                onClick = onOpenNews
            )
        }
    }
}

@Composable
private fun SearchPill(
    modifier: Modifier = Modifier,
    placeholder: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        GlassLayer(
            shape = RoundedCornerShape(16.dp),
            blurRadius = 10.dp,
            fillColor = PanelGlassFill,
            borderColor = PanelGlassBorder
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color(0xD9E8F1EA),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = placeholder,
                    color = Color(0xD9E8F1EA),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    modifier = Modifier.offset(y = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(44.dp)
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        GlassLayer(
            shape = RoundedCornerShape(14.dp),
            blurRadius = 10.dp,
            fillColor = PanelGlassFill,
            borderColor = PanelGlassBorder
        ) {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    rootItems: List<RootNavItem>,
    onSelectRootTab: (MainTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(68.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        GlassLayer(
            shape = RoundedCornerShape(24.dp),
            blurRadius = 14.dp,
            fillColor = PanelGlassFill,
            borderColor = PanelGlassBorder
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rootItems.forEach { item ->
                    BottomNavItem(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        item = item,
                        onClick = {
                            if (!item.selected) {
                                onSelectRootTab(item.tab)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassLayer(
    shape: Shape,
    modifier: Modifier = Modifier,
    blurRadius: Dp = 20.dp,
    fillColor: Color = GlassFill,
    borderColor: Color = GlassStroke,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .shadow(12.dp, shape, ambientColor = GlassGlow, spotColor = GlassGlow)
            .clip(shape)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(blurRadius)
                .background(Color(0x080A100C))
        )
        Surface(
            modifier = Modifier.matchParentSize(),
            shape = shape,
            color = fillColor,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    modifier: Modifier,
    item: RootNavItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 2.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (item.selected) {
            GlassLayer(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxSize(),
                blurRadius = 10.dp,
                fillColor = DarkGlassStrong,
                borderColor = Color(0x3034C759)
            ) {}
        }
        NavItemContent(
            item = item,
            iconTint = if (item.selected) AccentGreen else Color(0xCCE6ECE7),
            textColor = if (item.selected) AccentGreen else Color(0xCCE6ECE7),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun NavItemContent(
    item: RootNavItem,
    iconTint: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(18.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            maxLines = 2,
            color = textColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.offset(y = 1.dp)
        )
    }
}

@Composable
fun ScreenColumn(
    contentPadding: PaddingValues,
    content: @Composable () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = OuterPadding,
            top = contentPadding.calculateTopPadding() + 6.dp,
            end = OuterPadding,
            bottom = contentPadding.calculateBottomPadding() + 18.dp
        ),
        verticalArrangement = Arrangement.spacedBy(SectionGap)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(SectionGap)) {
                content()
            }
        }
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0x1FD5EBDD))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(8.dp)
                    .background(Color(0x12060D09))
            )
            Surface(
                modifier = Modifier.matchParentSize(),
                color = Color(0x16080F0B)
            ) {}
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(CardTint)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(CardInnerPadding),
                verticalArrangement = Arrangement.spacedBy(GridGap)
            ) {
                content()
            }
        }
    }
}

@Composable
fun CardTitle(
    title: String,
    trailing: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        if (trailing != null) {
            Text(
                text = trailing,
                color = AccentGreen,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun ValueText(
    text: String,
    accent: Boolean = false
) {
    Text(
        text = text,
        color = if (accent) AccentGreen else TextPrimary,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        maxLines = 1
    )
}

@Composable
fun HintText(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun AccentText(text: String) {
    Text(
        text = text,
        color = AccentGreen,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1
    )
}

@Composable
fun EmptyState(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = Color(0x12FFFFFF)
    )
}

@Composable
fun ScreenSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier.padding(vertical = 4.dp),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
}

@Composable
fun ProgressTrack(progress: Float) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
        color = AccentGreen,
        trackColor = Color(0x1F177C2D)
    )
}

@Composable
fun GlassPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 54.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        color = Color.Transparent
    ) {
        GlassLayer(
            shape = RoundedCornerShape(22.dp),
            blurRadius = 10.dp,
            fillColor = Color(0x2234C759),
            borderColor = Color(0x4434C759)
        ) {
            CompositionLocalProvider(LocalContentColor provides Color(0xFFF4FFF6)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun GlassSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 54.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        color = Color.Transparent
    ) {
        GlassLayer(
            shape = RoundedCornerShape(22.dp),
            blurRadius = 9.dp,
            fillColor = Color(0x1434C759),
            borderColor = Color(0x2826C2A3)
        ) {
            CompositionLocalProvider(LocalContentColor provides Color(0xFFEAF7EE)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    content()
                }
            }
        }
    }
}

fun sourceLabel(source: String): String = when (source) {
    "/api/v1/status" -> "status"
    "/api/v1/rating/detail" -> "rating"
    "/api/v1/financial-effect" -> "effect"
    "/api/v1/daily-results" -> "results"
    "/api/v1/tasks" -> "tasks"
    "/api/v1/leaderboard" -> "leaderboard"
    "/api/v1/learning/modules" -> "learning"
    "/api/v1/learning/quiz" -> "quiz"
    "/api/v1/learning/attempts" -> "attempts"
    "/api/v1/support/tickets" -> "tickets"
    "/api/v1/support/assistant/ask" -> "assistant"
    "/api/v1/news" -> "news"
    else -> source
}

fun formatInt(value: Int?): String = value?.let { "%,d".format(it).replace(',', ' ') } ?: NO_DATA

fun formatMoney(value: Int?): String = value?.let { "%,d ₽".format(it).replace(',', ' ') } ?: NO_DATA

@Composable
fun QuickActionsSection(
    actions: List<Pair<MainTab, String>>,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    MultiChoiceRow(
        actions = actions,
        selectedTab = selectedTab,
        onSelectTab = onSelectTab
    )
}

@Composable
fun MultiChoiceRow(
    actions: List<Pair<MainTab, String>>,
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(actions) { (tab, label) ->
            FilterChip(
                selected = tab == selectedTab,
                onClick = { onSelectTab(tab) },
                label = {
                    Text(
                        text = label,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = tab == selectedTab,
                    borderColor = Color(0x22FFFFFF),
                    selectedBorderColor = Color(0x4434C759)
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Transparent,
                    labelColor = TextPrimary,
                    selectedContainerColor = Color(0x1E34C759),
                    selectedLabelColor = Color(0xFFF2FFF5)
                )
            )
        }
    }
}
