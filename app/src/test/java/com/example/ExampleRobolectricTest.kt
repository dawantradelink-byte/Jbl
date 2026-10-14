package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ConnectionStatus
import com.example.model.EqualizerPreset
import com.example.ui.theme.CrimsonRoseHighlight
import com.example.ui.theme.CrimsonRosePrimary
import com.example.ui.theme.DeepSlateBlueHighlight
import com.example.ui.theme.DeepSlateBluePrimary
import com.example.ui.theme.DreamyPinkHighlight
import com.example.ui.theme.OceanicBlueHighlight
import com.example.ui.theme.OceanicBluePrimary
import com.example.ui.theme.RoastedBrownHighlight
import com.example.ui.theme.RoastedBrownPrimary
import com.example.ui.theme.StudioTheme
import com.example.ui.theme.SunsetOrangeHighlight
import com.example.ui.theme.SunsetOrangePrimary
import com.example.viewmodel.BoomboxViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name from context matches BOOMBOX Control Studio`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("BOOMBOX Control Studio", appName)
    }

    @Test
    fun `viewModel does not invent a connected speaker when none is verified`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = BoomboxViewModel(app)
        assertEquals("No Bluetooth audio connected", vm.speakerState.value.name)
        assertEquals(ConnectionStatus.DISCONNECTED, vm.speakerState.value.connectionStatus)
    }

    @Test
    fun `all studio color families exist with correct palette colors`() {
        val sunset = StudioTheme.SUNSET_ORANGE
        assertEquals("sunset_orange", sunset.id)
        assertEquals(SunsetOrangePrimary, sunset.primaryColor)
        assertEquals(SunsetOrangeHighlight, sunset.highlightColor)

        val oceanic = StudioTheme.OCEANIC_BLUE
        assertEquals("oceanic_blue", oceanic.id)
        assertEquals(OceanicBluePrimary, oceanic.primaryColor)
        assertEquals(OceanicBlueHighlight, oceanic.highlightColor)

        val rose = StudioTheme.CRIMSON_ROSE
        assertEquals("crimson_rose", rose.id)
        assertEquals(CrimsonRosePrimary, rose.primaryColor)
        assertEquals(CrimsonRoseHighlight, rose.highlightColor)

        val brown = StudioTheme.ROASTED_BROWN
        assertEquals("roasted_brown", brown.id)
        assertEquals(RoastedBrownPrimary, brown.primaryColor)
        assertEquals(RoastedBrownHighlight, brown.highlightColor)

        val pink = StudioTheme.DREAMY_PINK
        assertEquals("dreamy_pink", pink.id)
        assertEquals(DreamyPinkHighlight, pink.highlightColor)

        val slate = StudioTheme.DEEP_SLATE_BLUE
        assertEquals("deep_slate_blue", slate.id)
        assertEquals(DeepSlateBluePrimary, slate.primaryColor)
        assertEquals(DeepSlateBlueHighlight, slate.highlightColor)
    }

    @Test
    fun `viewModel preset selection updates frequency bands and preset`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = BoomboxViewModel(app)
        vm.selectPreset(EqualizerPreset.BASS_BOOST)
        assertEquals(EqualizerPreset.BASS_BOOST, vm.equalizerState.value.preset)
        val band60 = vm.equalizerState.value.bands.first { it.frequencyLabel == "60 Hz" }
        assertTrue(band60.gainDb > 0f)
    }

    @Test
    fun `viewModel master bass and treble controls update state`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = BoomboxViewModel(app)
        vm.updateBassLevel(0.85f)
        assertEquals(0.85f, vm.equalizerState.value.bassLevel, 0.01f)
        assertEquals(EqualizerPreset.CUSTOM, vm.equalizerState.value.preset)

        vm.updateTrebleLevel(0.70f)
        assertEquals(0.70f, vm.equalizerState.value.trebleLevel, 0.01f)
    }

    @Test
    fun `viewModel volume limiter caps volume at safe threshold`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = BoomboxViewModel(app)
        vm.toggleVolumeLimiter(true)
        vm.setVolume(100)
        assertEquals(85, vm.mediaState.value.volumePercent)
    }

    @Test
    fun `viewModel mute toggle updates isMuted state`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = BoomboxViewModel(app)
        val initialMute = vm.mediaState.value.isMuted
        vm.toggleMute()
        assertEquals(!initialMute, vm.mediaState.value.isMuted)
    }
}
