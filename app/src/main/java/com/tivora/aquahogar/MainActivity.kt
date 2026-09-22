package com.tivora.aquahogar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tivora.aquahogar.presentation.navigation.AquaHogarNavHost
import com.tivora.aquahogar.presentation.theme.AquaHogarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AquaHogarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AquaHogarNavHost()
                }
            }
        }
    }
}
