package com.brunof3l.locus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.brunof3l.locus.ui.AppNav
import com.brunof3l.locus.ui.theme.LocusTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    installSplashScreen()
    setContent {
      LocusTheme {
        AppNav()
      }
    }
  }
}
