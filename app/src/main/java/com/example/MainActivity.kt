package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.MainNavigationScaffold
import com.example.ui.theme.BoomboxTheme
import com.example.viewmodel.BoomboxViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BoomboxViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()

            BoomboxTheme(activeTheme = activeTheme) {
                MainNavigationScaffold(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
