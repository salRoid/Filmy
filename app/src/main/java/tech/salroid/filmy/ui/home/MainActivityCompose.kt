package tech.salroid.filmy.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dagger.hilt.android.AndroidEntryPoint
import tech.salroid.filmy.theme.FilmyTheme
import tech.salroid.filmy.ui.screens.HomeScreen

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FilmyTheme {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(
                        Color.White.toArgb(),
                    ),
                    navigationBarStyle = SystemBarStyle.dark(
                        Color.Transparent.toArgb(),
                    ),
                )
                HomeScreen()
            }
        }
    }
}