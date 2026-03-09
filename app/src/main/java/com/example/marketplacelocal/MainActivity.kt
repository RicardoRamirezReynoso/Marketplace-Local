package com.example.marketplacelocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.marketplacelocal.ui.navigation.MarketPlaceAppNavigation
import com.example.marketplacelocal.ui.theme.MarketPlaceLocalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * `onCreate` inicializa la interfaz de usuario de la aplicación.
     * Ahora la navegación está delegada a `MarketPlaceAppNavigation`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarketPlaceLocalTheme {
                MarketPlaceAppNavigation()
            }
        }
    }
}
