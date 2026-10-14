package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SpeakerGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.PrimaryContainerOceanic
import com.example.ui.theme.PrimaryMint
import com.example.ui.theme.SecondarySunsetContainer
import com.example.ui.theme.SecondarySunsetOrange
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryCrimsonContainer
import com.example.ui.theme.TertiaryCrimsonRose

@Composable
fun AcousticSpeakerVisualizer(
    speakerName: String,
    connectionStatus: ConnectionStatus,
    volumePercent: Int,
    audioEffectAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audioAcoustics")

    // Passive bass radiator physical excursion pulse
    val bassRadiatorPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = if (connectionStatus == ConnectionStatus.CONNECTED) 1.06f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 420, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bassPulse"
    )

    // Ambient floating glow intensity
    val ambientGlowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = if (isPlaying && connectionStatus == ConnectionStatus.CONNECTED) 0.85f else 0.20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientGlow"
    )

    // Waveform variation offsets for driver cones
    val waveOffset1 by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = if (isPlaying && connectionStatus == ConnectionStatus.CONNECTED) 0.95f else 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w1"
    )
    val waveOffset2 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = if (isPlaying && connectionStatus == ConnectionStatus.CONNECTED) 0.30f else 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(520, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w2"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("acoustic_speaker_visualizer")
            .semantics {
                contentDescription = "$speakerName, status: ${connectionStatus.name}"
            },
        surfaceAlpha = 0.70f
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status Header Badge: real Android A2DP state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Connected Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerHigh.copy(alpha = 0.80f))
                        .border(1.dp, PrimaryContainerOceanic.copy(alpha = 0.50f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(PrimaryMint)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (connectionStatus == ConnectionStatus.CONNECTED) "Connected • Android A2DP" else "No audio route",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = if (connectionStatus == ConnectionStatus.CONNECTED) PrimaryMint else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // System audio output badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "System audio output",
                        tint = PrimaryMint,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AUDIO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Speaker Silhouette Stage (Canvas matching SVG precision)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(144.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f + 8f

                    // 1. Ambient multi-color glow dispersion (Orange + Rose)
                    if (connectionStatus == ConnectionStatus.CONNECTED) {
                        // Left glow (Orange)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SecondarySunsetContainer.copy(alpha = 0.28f * ambientGlowIntensity),
                                    Color.Transparent
                                ),
                                center = Offset(cx - 70.dp.toPx(), cy - 20.dp.toPx()),
                                radius = 90.dp.toPx()
                            ),
                            radius = 90.dp.toPx(),
                            center = Offset(cx - 70.dp.toPx(), cy - 20.dp.toPx())
                        )
                        // Right glow (Rose)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    TertiaryCrimsonContainer.copy(alpha = 0.30f * ambientGlowIntensity),
                                    Color.Transparent
                                ),
                                center = Offset(cx + 70.dp.toPx(), cy + 20.dp.toPx()),
                                radius = 90.dp.toPx()
                            ),
                            radius = 90.dp.toPx(),
                            center = Offset(cx + 70.dp.toPx(), cy + 20.dp.toPx())
                        )
                    }

                    // 2. Rugged Top Grip Handle
                    val handleW = 130.dp.toPx().coerceAtMost(w * 0.60f)
                    val handleH = 32.dp.toPx()
                    val handlePath = Path().apply {
                        moveTo(cx - handleW / 2, cy - 20.dp.toPx())
                        cubicTo(
                            cx - handleW / 2, cy - 20.dp.toPx() - handleH,
                            cx + handleW / 2, cy - 20.dp.toPx() - handleH,
                            cx + handleW / 2, cy - 20.dp.toPx()
                        )
                    }
                    drawPath(
                        path = handlePath,
                        color = Color(0xFF333536),
                        style = Stroke(width = 6.dp.toPx())
                    )
                    drawPath(
                        path = handlePath,
                        color = Color(0xFF404944),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // 3. Outer Rugged Bumper Chassis
                    val bodyWidth = 236.dp.toPx().coerceAtMost(w - 24.dp.toPx())
                    val bodyHeight = 78.dp.toPx()
                    val cornerR = 28.dp.toPx()

                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF282A2B),
                                Color(0xFF1A1C1D),
                                Color(0xFF0C0E0F)
                            ),
                            start = Offset(cx - bodyWidth / 2, cy - bodyHeight / 2),
                            end = Offset(cx + bodyWidth / 2, cy + bodyHeight / 2)
                        ),
                        topLeft = Offset(cx - bodyWidth / 2, cy - bodyHeight / 2),
                        size = Size(bodyWidth, bodyHeight),
                        cornerRadius = CornerRadius(cornerR, cornerR)
                    )

                    // Grille Mesh Surface Pattern (dashed lines)
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 6.dp.toPx()), 0f)
                    val lineCount = 5
                    val lineSpacing = bodyHeight / (lineCount + 1)
                    for (i in 1..lineCount) {
                        val ly = cy - bodyHeight / 2 + i * lineSpacing
                        drawLine(
                            color = OutlineColor.copy(alpha = 0.35f),
                            start = Offset(cx - bodyWidth / 2 + 18.dp.toPx(), ly),
                            end = Offset(cx + bodyWidth / 2 - 18.dp.toPx(), ly),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = dashEffect
                        )
                    }

                    // 4. Left Woofer Driver (Oceanic Mint/Teal Accent)
                    val leftWooferX = cx - bodyWidth * 0.28f
                    val wooferRadius = 26.dp.toPx() * bassRadiatorPulse
                    // Cone glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1A1C1D),
                                Color(0xFF282A2B),
                                PrimaryContainerOceanic.copy(alpha = 0.40f)
                            ),
                            center = Offset(leftWooferX, cy),
                            radius = wooferRadius
                        ),
                        radius = wooferRadius,
                        center = Offset(leftWooferX, cy)
                    )
                    drawCircle(
                        color = Color(0xFF333536),
                        radius = wooferRadius,
                        center = Offset(leftWooferX, cy),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF121415),
                        radius = 12.dp.toPx(),
                        center = Offset(leftWooferX, cy)
                    )
                    drawCircle(
                        color = PrimaryContainerOceanic.copy(alpha = 0.65f),
                        radius = 7.dp.toPx(),
                        center = Offset(leftWooferX, cy)
                    )

                    // 5. Center Acoustic Logo Block
                    val logoW = 18.dp.toPx()
                    val logoH = 20.dp.toPx()
                    drawRoundRect(
                        color = SurfaceContainerLowest,
                        topLeft = Offset(cx - logoW / 2, cy - logoH / 2),
                        size = Size(logoW, logoH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    // Play triangle glyph
                    val triPath = Path().apply {
                        moveTo(cx - 3.dp.toPx(), cy - 5.dp.toPx())
                        lineTo(cx + 5.dp.toPx(), cy)
                        lineTo(cx - 3.dp.toPx(), cy + 5.dp.toPx())
                        close()
                    }
                    drawPath(
                        path = triPath,
                        color = PrimaryMint
                    )

                    // 6. Right Woofer Driver (Sunset Orange Accent)
                    val rightWooferX = cx + bodyWidth * 0.28f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1A1C1D),
                                Color(0xFF282A2B),
                                SecondarySunsetOrange.copy(alpha = 0.35f)
                            ),
                            center = Offset(rightWooferX, cy),
                            radius = wooferRadius
                        ),
                        radius = wooferRadius,
                        center = Offset(rightWooferX, cy)
                    )
                    drawCircle(
                        color = Color(0xFF333536),
                        radius = wooferRadius,
                        center = Offset(rightWooferX, cy),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFF121415),
                        radius = 12.dp.toPx(),
                        center = Offset(rightWooferX, cy)
                    )
                    drawCircle(
                        color = SecondarySunsetContainer.copy(alpha = 0.65f),
                        radius = 7.dp.toPx(),
                        center = Offset(rightWooferX, cy)
                    )

                    // 7. Passive End Radiator Anodized Gradient Rings (Left & Right)
                    val ringBrush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryContainerOceanic,
                            SecondarySunsetOrange,
                            TertiaryCrimsonContainer
                        )
                    )
                    val capH = (bodyHeight * 0.65f) * bassRadiatorPulse
                    drawOval(
                        brush = ringBrush,
                        topLeft = Offset(cx - bodyWidth / 2 - 2.dp.toPx(), cy - capH / 2),
                        size = Size(6.dp.toPx(), capH),
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                    drawOval(
                        brush = ringBrush,
                        topLeft = Offset(cx + bodyWidth / 2 - 4.dp.toPx(), cy - capH / 2),
                        size = Size(6.dp.toPx(), capH),
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }

            // Live Transducer Pulse Meter Rings (3 dots: Mint, Orange, Rose)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(PrimaryMint)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(SecondarySunsetOrange)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TertiaryCrimsonRose)
                )
            }

            // Quick Acoustic Telemetry Metrics Pills (Grid of 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerLowest.copy(alpha = 0.65f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Metric 1: Android system volume
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceContainerLow)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = SecondarySunsetOrange,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "OUTPUT",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$volumePercent%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = SecondarySunsetOrange
                    )
                }

                // Metric 2: Android-reported audio route
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceContainerLow)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SpeakerGroup,
                            contentDescription = null,
                            tint = PrimaryMint,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "ROUTE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (connectionStatus == ConnectionStatus.CONNECTED) "A2DP" else "OFFLINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (connectionStatus == ConnectionStatus.CONNECTED) PrimaryMint else TertiaryCrimsonRose
                    )
                }

                // Metric 3: phone-side EQ availability
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceContainerLow)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = TertiaryCrimsonRose,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "PHONE EQ",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (audioEffectAvailable) "AVAILABLE" else "UNAVAILABLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TertiaryCrimsonRose
                    )
                }
            }
        }
    }
}
