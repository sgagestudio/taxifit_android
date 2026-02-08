package com.mm.taxifit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mm.taxifit.data.repository.AppUserRole
import com.mm.taxifit.ui.navigation.RoleHomeNavHost
import com.mm.taxifit.ui.theme.TaxifitTheme

class HomeOwnerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaxifitTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RoleHomeNavHost(role = AppUserRole.OWNER)
                }
            }
        }
    }
}
