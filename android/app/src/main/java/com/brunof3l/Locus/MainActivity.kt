package com.brunof3l.locus

import android.os.Bundle
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint
import com.facebook.react.defaults.DefaultReactActivityDelegate

// Adicione esta importação
import expo.modules.ReactActivityDelegateWrapper

class MainActivity : ReactActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    // Adicionado para o react-native-screens
    super.onCreate(null)
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "Locus" // Certifique-se que "Locus" é o nome no app.json

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture (Fabric) with a single boolean flag.
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate {
    return ReactActivityDelegateWrapper(
      this,
      BuildConfig.DEBUG,
      object : DefaultReactActivityDelegate(
        this,
        mainComponentName,
        // Habilita a Nova Arquitetura (Fabric) se estiver usando
        DefaultNewArchitectureEntryPoint.fabricEnabled,
        // Habilita o Modo Bridge se a Nova Arquitetura estiver desabilitada
        DefaultNewArchitectureEntryPoint.jsBundleEntryFile
      ) {}
    )
  }
}