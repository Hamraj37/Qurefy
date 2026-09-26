package com.qurafy.hamraj37.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.SettingsBrightness
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qurafy.hamraj37.data.model.QuranMetaData
import com.qurafy.hamraj37.data.repository.NightModePreference
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.rounded.Home

@Composable
fun QuranReaderScreen(
    initialPage: Int = 1,
    viewModel: QuranReaderViewModel = viewModel(),
    onNavigateHome: (() -> Unit)? = null
) {
    if (onNavigateHome != null) {
        BackHandler {
            onNavigateHome()
        }
    }
    val lastReadPage by viewModel.lastReadPage.collectAsState()
    val nightMode by viewModel.nightMode.collectAsState()
    val bookmarkedPages by viewModel.bookmarkedPages.collectAsState()
    val totalPages by viewModel.pageCount.collectAsState()
    val isJumpDialogOpen by viewModel.isJumpToPageDialogOpen.collectAsState()

    // Drawing Mode States
    val isDrawingMode by viewModel.isDrawingMode.collectAsState()
    val activeBrushColor by viewModel.activeBrushColor.collectAsState()
    val isHighlighter by viewModel.isHighlighter.collectAsState()
    val pageAnnotations by viewModel.pageAnnotations.collectAsState()

    // System dark mode check
    val isSystemDark = isSystemInDarkTheme()
    val isNight = when (nightMode) {
        NightModePreference.DARK -> true
        NightModePreference.LIGHT -> false
        NightModePreference.SYSTEM -> isSystemDark
    }

    val scope = rememberCoroutineScope()

    // Pager state
    val targetIndex = (initialPage - 1).coerceIn(0, (totalPages - 1).coerceAtLeast(0))
    val pagerState = rememberPagerState(
        initialPage = targetIndex,
        pageCount = { totalPages }
    )

    LaunchedEffect(initialPage, totalPages) {
        val idx = (initialPage - 1).coerceIn(0, (totalPages - 1).coerceAtLeast(0))
        if (pagerState.currentPage != idx) {
            pagerState.scrollToPage(idx)
        }
    }

    // Current page 1-based
    val currentPage = pagerState.currentPage + 1
    val isCurrentBookmarked = remember(currentPage, bookmarkedPages) { bookmarkedPages.contains(currentPage) }
    val currentSurah = remember(currentPage) { QuranMetaData.getSurahForPage(currentPage) }
    val headerTitle = remember(currentSurah, currentPage) {
        if (currentSurah != null) {
            "${currentSurah.nameTransliteration} (${currentSurah.nameArabic})"
        } else {
            "Page $currentPage"
        }
    }

    // Controls visibility
    var showControls by remember { mutableStateOf(true) }
    var showThemeMenu by remember { mutableStateOf(false) }

    // Auto-save last read page position on scroll
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { pageIndex ->
                viewModel.saveLastReadPage(pageIndex + 1)
            }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(
                    if (isNight) Color(0xFF121212)
                    else MaterialTheme.colorScheme.background
                )
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = !isDrawingMode,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) { pageIndex ->
                QuranPdfPageView(
                    pageIndex = pageIndex + 1,
                    isNightMode = isNight,
                    renderPageBitmap = { page, w, h, night ->
                        viewModel.renderPdfPage(page, w, h, night)
                    },
                    isDrawingMode = isDrawingMode,
                    activeColor = activeBrushColor,
                    isHighlighter = isHighlighter,
                    savedStrokes = pageAnnotations[pageIndex + 1] ?: emptyList(),
                    onAddStroke = { stroke -> viewModel.addStroke(pageIndex + 1, stroke) },
                    onToggleControls = { showControls = !showControls },
                    modifier = Modifier.fillMaxSize()
                )
            }

            val glassBorderBrush = remember {
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.70f),
                        Color.White.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            }
            val glassBgColor = if (isNight) Color(0xCC1E1E1E) else Color(0xDDFFFFFF)

            // Drawing Toolbar Overlay
            AnimatedVisibility(
                visible = isDrawingMode,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                DrawingToolbar(
                    activeColor = activeBrushColor,
                    isHighlighter = isHighlighter,
                    onSelectBrush = { color, high -> viewModel.setBrush(color, high) },
                    onUndo = { viewModel.undoStroke(currentPage) },
                    onClearPage = { viewModel.clearPageStrokes(currentPage) },
                    onCloseDrawingMode = { viewModel.setDrawingMode(false) }
                )
            }

            // Separate Top Left Floating Home Button Overlay
            if (onNavigateHome != null) {
                AnimatedVisibility(
                    visible = showControls && !isDrawingMode,
                    enter = fadeIn() + slideInVertically { -it },
                    exit = fadeOut() + slideOutVertically { -it },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(top = 10.dp, start = 16.dp)
                ) {
                    Surface(
                        onClick = onNavigateHome,
                        shape = CircleShape,
                        tonalElevation = 12.dp,
                        shadowElevation = 12.dp,
                        color = glassBgColor,
                        modifier = Modifier.border(1.2.dp, glassBorderBrush, CircleShape)
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Home,
                                contentDescription = "Go to Home",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Floating Center Top Header Card overlay
            AnimatedVisibility(
                visible = showControls && !isDrawingMode,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                TopHeaderCard(title = headerTitle)
            }

            // Separate Top Right Floating Pencil Button Overlay
            AnimatedVisibility(
                visible = showControls && !isDrawingMode,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 10.dp, end = 16.dp)
            ) {
                Surface(
                    onClick = { viewModel.toggleDrawingMode() },
                    shape = CircleShape,
                    tonalElevation = 12.dp,
                    shadowElevation = 12.dp,
                    color = glassBgColor,
                    modifier = Modifier.border(1.2.dp, glassBorderBrush, CircleShape)
                ) {
                    Box(
                        modifier = Modifier.padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Mark/Annotate Text",
                            tint = if (isDrawingMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Floating Bottom Control Bar overlay
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface(
                    tonalElevation = 12.dp,
                    shadowElevation = 12.dp,
                    color = glassBgColor,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    modifier = Modifier.border(1.2.dp, glassBorderBrush, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Page Scrubber Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (pagerState.currentPage > 0) {
                                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                    }
                                },
                                enabled = pagerState.currentPage > 0
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Previous Page"
                                )
                            }

                            Slider(
                                value = currentPage.toFloat(),
                                onValueChange = { pageVal ->
                                    val targetPageIndex = (pageVal.toInt() - 1).coerceIn(0, (totalPages - 1).coerceAtLeast(0))
                                    scope.launch { pagerState.scrollToPage(targetPageIndex) }
                                },
                                valueRange = 1f..totalPages.toFloat().coerceAtLeast(1f),
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    if (pagerState.currentPage < totalPages - 1) {
                                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                    }
                                },
                                enabled = pagerState.currentPage < totalPages - 1
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = "Next Page"
                                )
                            }

                            // Page Pill
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 2.dp
                            ) {
                                Text(
                                    text = "Page $currentPage / $totalPages",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Bottom Navigation Bar Items
                        Box(modifier = Modifier.fillMaxWidth()) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp,
                                windowInsets = WindowInsets(0, 0, 0, 0)
                            ) {
                                // 1. Jump to Page
                                NavigationBarItem(
                                    selected = isJumpDialogOpen,
                                    onClick = {
                                        viewModel.setJumpToPageDialogOpen(true)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Rounded.FormatListNumbered,
                                            contentDescription = "Jump"
                                        )
                                    },
                                    label = { Text("Jump") }
                                )

                                // 2. Bookmark
                                NavigationBarItem(
                                    selected = isCurrentBookmarked,
                                    onClick = {
                                        viewModel.toggleBookmark(currentPage)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isCurrentBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (isCurrentBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    label = { Text("Bookmark") }
                                )

                                // 3. Theme
                                NavigationBarItem(
                                    selected = showThemeMenu,
                                    onClick = { showThemeMenu = true },
                                    icon = {
                                        Icon(
                                            imageVector = when (nightMode) {
                                                NightModePreference.DARK -> Icons.Rounded.DarkMode
                                                NightModePreference.LIGHT -> Icons.Rounded.LightMode
                                                NightModePreference.SYSTEM -> Icons.Rounded.SettingsBrightness
                                            },
                                            contentDescription = "Theme"
                                        )
                                    },
                                    label = { Text("Theme") }
                                )
                            }

                            DropdownMenu(
                                expanded = showThemeMenu,
                                onDismissRequest = { showThemeMenu = false },
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Light Theme") },
                                    leadingIcon = { Icon(Icons.Rounded.LightMode, contentDescription = null) },
                                    onClick = {
                                        viewModel.setNightMode(NightModePreference.LIGHT)
                                        showThemeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Night Theme") },
                                    leadingIcon = { Icon(Icons.Rounded.DarkMode, contentDescription = null) },
                                    onClick = {
                                        viewModel.setNightMode(NightModePreference.DARK)
                                        showThemeMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("System Theme") },
                                    leadingIcon = { Icon(Icons.Rounded.SettingsBrightness, contentDescription = null) },
                                    onClick = {
                                        viewModel.setNightMode(NightModePreference.SYSTEM)
                                        showThemeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Jump To Page Dialog

    // Jump To Page Dialog
    if (isJumpDialogOpen) {
        JumpToPageDialog(
            currentPage = currentPage,
            maxPage = totalPages,
            bookmarkedPages = bookmarkedPages,
            onDismiss = { viewModel.setJumpToPageDialogOpen(false) },
            onJumpToPage = { targetPage ->
                viewModel.setJumpToPageDialogOpen(false)
                scope.launch {
                    pagerState.scrollToPage((targetPage - 1).coerceIn(0, (totalPages - 1).coerceAtLeast(0)))
                }
            }
        )
    }
}

@Composable
fun TopHeaderCard(
    title: String,
    modifier: Modifier = Modifier
) {
    val glassBorderBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.70f),
                Color.White.copy(alpha = 0.20f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    }

    Surface(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 10.dp)
            .border(1.2.dp, glassBorderBrush, CircleShape),
        shape = CircleShape,
        tonalElevation = 12.dp,
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)
        )
    }
}
