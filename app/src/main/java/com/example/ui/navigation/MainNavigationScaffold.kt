package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SettingsInputComponent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AnimatedBackground
import com.example.ui.screens.AudioStudioScreen
import com.example.ui.screens.DeviceManagerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.PrimaryContainerOceanic
import com.example.ui.theme.PrimaryMint
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.TextOnSurface
import com.example.ui.theme.TextOnSurfaceVariant
import com.example.ui.util.LocalStudioHaptics
import com.example.ui.util.rememberStudioHaptics
import com.example.viewmodel.BoomboxViewModel

enum class NavigationDestination(
    val route: String,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val testTag: String
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    STUDIO("studio", "Studio EQ", Icons.Filled.Equalizer, Icons.Outlined.Equalizer, "nav_studio"),
    DEVICES("devices", "Devices", Icons.Filled.SettingsInputComponent, Icons.Outlined.SettingsInputComponent, "nav_devices"),
    SETTINGS("settings", "Skin", Icons.Filled.Palette, Icons.Outlined.Palette, "nav_settings")
}

@Composable
fun MainNavigationScaffold(
    viewModel: BoomboxViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(NavigationDestination.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }
    val userMessage by viewModel.userFeedbackMessage.collectAsStateWithLifecycle()
    val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
    val motionPref by viewModel.motionPreference.collectAsStateWithLifecycle()
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()

    val studioHaptics = rememberStudioHaptics(enabled = settingsState.hapticFeedbackEnabled)

    // Back button handling: returns to HOME if on secondary screen
    BackHandler(enabled = currentDestination != NavigationDestination.HOME) {
        studioHaptics.click()
        currentDestination = NavigationDestination.HOME
    }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedbackMessage()
        }
    }

    // Wrap everything in the full-screen dynamic animated background & haptic provider
    CompositionLocalProvider(LocalStudioHaptics provides studioHaptics) {
        AnimatedBackground(
            activeTheme = activeTheme,
            motionPreference = motionPref
        ) {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.padding(bottom = 88.dp)
                    ) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = SurfaceContainerHigh,
                            contentColor = TextOnSurface,
                            actionColor = PrimaryMint
                        )
                    }
                },
                bottomBar = {
                    // Floating Rounded-Full Glassmorphic Bottom Navigation Bar
                    val navBarInsets = WindowInsets.navigationBars.asPaddingValues()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = navBarInsets.calculateBottomPadding() + 8.dp)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 420.dp)
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(SurfaceContainerLow.copy(alpha = 0.88f))
                                .border(1.dp, Color(0xFF333536).copy(alpha = 0.60f), RoundedCornerShape(32.dp))
                                .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Color.Black)
                                .padding(horizontal = 6.dp)
                                .testTag("main_navigation_bar")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NavigationDestination.entries.forEach { destination ->
                                    val isSelected = currentDestination == destination

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(
                                                if (isSelected) PrimaryContainerOceanic.copy(alpha = 0.35f) else Color.Transparent
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) PrimaryMint.copy(alpha = 0.40f) else Color.Transparent,
                                                RoundedCornerShape(24.dp)
                                            )
                                            .clickable {
                                                if (currentDestination != destination) {
                                                    studioHaptics.click()
                                                }
                                                currentDestination = destination
                                            }
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .testTag(destination.testTag),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) destination.activeIcon else destination.inactiveIcon,
                                                contentDescription = destination.title,
                                                tint = if (isSelected) PrimaryMint else TextOnSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = destination.title,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isSelected) PrimaryMint else TextOnSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Smooth animated screen transitions (250ms)
                    AnimatedContent(
                        targetState = currentDestination,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                        },
                        label = "screenTransition"
                    ) { dest ->
                        when (dest) {
                            NavigationDestination.HOME -> HomeScreen(viewModel = viewModel)
                            NavigationDestination.STUDIO -> AudioStudioScreen(viewModel = viewModel)
                            NavigationDestination.DEVICES -> DeviceManagerScreen(viewModel = viewModel)
                            NavigationDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
