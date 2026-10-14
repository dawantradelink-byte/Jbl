package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * BOOMBOX4MINI Master Multi-Chromatic Color System
 *
 * Mix and match across the 5 reference palettes:
 * - Oceanic Blue / Mint: Primary #97D3B7, Container #215C46, OnPrimary #003827
 * - Sunset Orange / Peach: Secondary #FFB86C, Container #D38000, Soft #F8BD8F, Fixed #FFDCBC
 * - Crimson Rose / Coral: Tertiary #FFB3B2, Container #A80025, Vivid #EF384A, Soft #FD95B2
 * - Dreamy Pink: Primary #F4D2FF, Highlight #FDACF7, Accent #BA7EB5
 * - Roasted Brown / Mocha: Primary #45342E, Soft #DDB7A9, Deep #2E221E
 * - Deep Slate: Surface #1A202C, Highlight #96B1D2
 *
 * Obsidian Dark Base Surfaces:
 * - surface-container-lowest: #0C0E0F
 * - surface: #121415
 * - surface-container-low: #1A1C1D
 * - surface-container: #1E2021
 * - surface-container-high: #282A2B
 * - surface-container-highest: #333536
 * - on-surface: #E2E2E3
 * - on-surface-variant: #BFC9C2
 * - outline: #8A938D
 * - outline-variant: #404944
 */

// 1. Oceanic Blue / Mint Palette
val PrimaryMint = Color(0xFF97D3B7)
val PrimaryMintDark = Color(0xFF003827)
val PrimaryContainerOceanic = Color(0xFF215C46)
val OceanicBluePrimary = Color(0xFF215C46)
val OceanicBlueHighlight = Color(0xFF97D3B7)
val OceanicBlueDim = Color(0xFF163E2F)
val OceanicTealSoft = Color(0xFFA9BDBC)

// 2. Sunset Orange / Peach Palette
val SecondarySunsetOrange = Color(0xFFFFB86C)
val SecondarySunsetContainer = Color(0xFFD38000)
val SecondaryFixedPeach = Color(0xFFFFDCBC)
val SunsetOrangePrimary = Color(0xFFEE9521)
val SunsetOrangeHighlight = Color(0xFFFFB86C)
val SunsetOrangeSoft = Color(0xFFF8BD8F)
val SunsetOrangeDim = Color(0xFFC4730E)

// 3. Crimson Rose / Coral Palette
val TertiaryCrimsonRose = Color(0xFFFFB3B2)
val TertiaryCrimsonContainer = Color(0xFFA80025)
val CrimsonRosePrimary = Color(0xFFEF384A)
val CrimsonRoseHighlight = Color(0xFFFFB3B2)
val CrimsonRoseSoft = Color(0xFFFD95B2)
val CrimsonRoseDim = Color(0xFFC72435)

// 4. Dreamy Pink Palette
val DreamyPinkPrimary = Color(0xFFF4D2FF)
val DreamyPinkHighlight = Color(0xFFFDACF7)
val DreamyPinkDim = Color(0xFFBA7EB5)

// 5. Roasted Brown / Mocha Palette
val RoastedBrownPrimary = Color(0xFF45342E)
val RoastedBrownHighlight = Color(0xFFDDB7A9)
val RoastedBrownDim = Color(0xFF2E221E)
val RoastedBrownFixed = Color(0xFFFFDCBC)

// 6. Deep Slate Blue Supporting Palette
val DeepSlateBluePrimary = Color(0xFF1B2B3F)
val DeepSlateBlueHighlight = Color(0xFF96B1D2)
val DeepSlateBlueDim = Color(0xFF121E2C)

// Dark Obsidian Base Surfaces (Tailwind config exact tokens)
val SurfaceDark = Color(0xFF121415)
val SurfaceContainerLowest = Color(0xFF0C0E0F)
val SurfaceContainerLow = Color(0xFF1A1C1D)
val SurfaceContainer = Color(0xFF1E2021)
val SurfaceContainerHigh = Color(0xFF282A2B)
val SurfaceContainerHighest = Color(0xFF333536)
val SurfaceBright = Color(0xFF38393A)

// High-Contrast WCAG 2.2 Typography Tokens
val TextOnSurface = Color(0xFFE2E2E3)
val TextOnSurfaceVariant = Color(0xFFBFC9C2)
val OutlineColor = Color(0xFF8A938D)
val OutlineVariantColor = Color(0xFF404944)

// Studio aliases for backward compatibility
val StudioBackground = SurfaceDark
val StudioSurface = SurfaceContainer
val StudioSurfaceElevated = SurfaceContainerHigh
val StudioSurfaceCard = SurfaceContainerHighest
val StudioCardBorder = OutlineVariantColor
val StudioCardBorderHover = OutlineColor

val TextPrimary = TextOnSurface
val TextSecondary = TextOnSurfaceVariant
val TextTertiary = OutlineColor
val TextDisabled = Color(0xFF676A69)

// Brand Primary & Accent Aliases
val PrimaryAccent = PrimaryMint
val HighlightAccent = SecondarySunsetOrange
val PrimaryDim = PrimaryContainerOceanic
val PrimaryGlow = Color(0x3397D3B7)

// Functional State Semantics
val StatusConnected = PrimaryMint
val StatusConnecting = SecondarySunsetOrange
val StatusDisconnected = CrimsonRosePrimary
val HardwareUnsupportedBadge = SunsetOrangeSoft

val SunsetPrimary = SunsetOrangePrimary
val SunsetHighlight = SunsetOrangeHighlight
val SunsetBackground = StudioBackground
val ElectricBlue = PrimaryAccent
val ElectricBlueDim = PrimaryDim
val ElectricBlueGlow = PrimaryGlow
val DeepCyan = PrimaryMint
