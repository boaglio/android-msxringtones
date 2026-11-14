package com.olimposystems.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.olimposystems.android.ui.MsxApp
import com.olimposystems.android.ui.theme.MsxTheme

/** Única activity do app; as telas são composables (ver [MsxApp]). */
class MSXringtonesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MsxTheme {
                MsxApp()
            }
        }
    }
}
