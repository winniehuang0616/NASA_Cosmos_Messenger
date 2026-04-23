package com.example.nasacosmosmessengerapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nasacosmosmessengerapp.presentation.theme.NASACosmosMessengerAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NASACosmosMessengerAPPTheme {
                MainScaffold()
            }
        }
    }
}
