package com.example.alphabettracer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alphabettracer.ui.screens.AlphabetTracingApp
import com.example.alphabettracer.ui.theme.AlphabetTracerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlphabetTracerTheme {
                AlphabetTracingApp()
            }
        }
    }
}