package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * StudioTheme provides distinct acoustic profile accents across the app.
 * In all themes, the continuous background animation seamlessly blends all 5 signature palettes:
 * Oceanic Blue, Roasted Brown, Crimson Rose, Sunset Orange, and Dreamy Pink.
 */
enum class StudioTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val highlightColor: Color,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val accentSecondary: Color
) {
    SUNSET_ORANGE(
        id = "sunset_orange",
        title = "Sunset Radiant",
        subtitle = "Amber glow with dynamic acoustic energy",
        primaryColor = SunsetOrangePrimary,
        highlightColor = SunsetOrangeHighlight,
        backgroundColor = StudioBackground,
        surfaceColor = Color(0xFF221A14),
        accentSecondary = CrimsonRosePrimary
    ),
    OCEANIC_BLUE(
        id = "oceanic_blue",
        title = "Oceanic Studio",
        subtitle = "Deep marine clarity & calm frequency response",
        primaryColor = OceanicBluePrimary,
        highlightColor = OceanicBlueHighlight,
        backgroundColor = StudioBackground,
        surfaceColor = Color(0xFF14201B),
        accentSecondary = SunsetOrangePrimary
    ),
    CRIMSON_ROSE(
        id = "crimson_rose",
        title = "Crimson Rose",
        subtitle = "High-voltage acoustic power & punchy bass",
        primaryColor = CrimsonRosePrimary,
        highlightColor = CrimsonRoseHighlight,
        backgroundColor = StudioBackground,
        surfaceColor = Color(0xFF221417),
        accentSecondary = DreamyPinkHighlight
    ),
    ROASTED_BROWN(
        id = "roasted_brown",
        title = "Roasted Analog",
        subtitle = "Warm analog tube resonance & rich low-end",
        primaryColor = RoastedBrownPrimary,
        highlightColor = RoastedBrownHighlight,
        backgroundColor = StudioBackground,
        surfaceColor = Color(0xFF1F1A18),
        accentSecondary = SunsetOrangeHighlight
    ),
    DREAMY_PINK(
        id = "dreamy_pink",
        title = "Dreamy Pink",
        subtitle = "Ethereal shimmer & spatial acoustic air",
        primaryColor = DreamyPinkDim,
        highlightColor = DreamyPinkHighlight,
        backgroundColor = StudioBackground,
        surfaceColor = Color(0xFF211922),
        accentSecondary = OceanicBlueHighlight
    ),
    DEEP_SLATE_BLUE(
        id = "deep_slate_blue",
        title = "Deep Slate",
        subtitle = "Nocturnal studio monitoring & balanced fidelity",
        primaryColor = DeepSlateBluePrimary,
        highlightColor = DeepSlateBlueHighlight,
        backgroundColor = StudioBackground,
        surfaceColor = Color(0xFF161E28),
        accentSecondary = SunsetOrangePrimary
    ),
    // Backward compatibility alias for saved "sunset_glow"
    SUNSET_GLOW(
        id = "sunset_glow",
        title = "Sunset Radiant",
        subtitle = "Amber glow with dynamic acoustic energy",
        primaryColor = SunsetOrangePrimary,
        highlightColor = SunsetOrangeHighlight,
        backgroundColor = StudioBackground,
        surfaceColor = Color(0xFF221A14),
        accentSecondary = CrimsonRosePrimary
    ),
    // Backward compatibility alias for saved "deep_plum"
    DEEP_PLUM(
        id = "deep_plum",
        title = "Dreamy Pink",
        subtitle = "Ethereal shimmer & spatial acoustic air",
        primaryColor = DreamyPinkDim,
        highlightColor = DreamyPinkHighlight,
        backgroundColor = StudioBackground,
        surfaceColor = Color(0xFF211922),
        accentSecondary = OceanicBlueHighlight
    );

    companion object {
        fun fromId(id: String): StudioTheme {
            return entries.firstOrNull { it.id == id } ?: SUNSET_ORANGE
        }

        /**
         * The 5 canonical primary color families + 1 supporting slate for selection in Settings
         */
        val selectableThemes: List<StudioTheme> = listOf(
            SUNSET_ORANGE,
            OCEANIC_BLUE,
            CRIMSON_ROSE,
            ROASTED_BROWN,
            DREAMY_PINK,
            DEEP_SLATE_BLUE
        )
    }
}

enum class MotionPreference(val title: String, val description: String, val speedMultiplier: Float) {
    FULL("Full Ambient Motion", "Continuous 5-color fluid mesh animation (~22s cycle)", 1.0f),
    GENTLE("Gentle Calm Motion", "Subtle breathing movements with low amplitude (~40s cycle)", 0.5f),
    REDUCED("Reduced Motion", "Static elegant multi-tone gradient, accessible & battery safe", 0.0f)
}
