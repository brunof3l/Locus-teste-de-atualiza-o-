package com.brunof3l.locus

import android.app.Application
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint
import com.facebook.react.defaults.DefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.flipper.ReactNativeFlipper
import com.facebook.soloader.SoLoader // <-- ESTA LINHA ESTAVA COM O ERRO "comimport"

// Adicione estas importações do Expo
import expo.modules.ApplicationLifecycleDispatcher
import expo.modules.ReactNativeHostWrapper

class MainApplication : Application(), ReactApplication {

  override val reactNativeHost: ReactNativeHost = ReactNativeHostWrapper(
    this,
    object : DefaultReactNativeHost(this) {
      override fun getPackages(): List<ReactPackage> {
        // Packages auto-linked by RN CLI
        val packages = PackageList(this).packages
        // Adicione pacotes manuais aqui, se necessário
        return packages
      }

      override fun getJSMainModuleName(): String = "index" // Geralmente é "index"

      override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

      override val isNewArchEnabled: Boolean = DefaultNewArchitectureEntryPoint.fabricEnabled
      override val isHermesEnabled: Boolean = DefaultNewArchitectureEntryPoint.hermesEnabled
    }
  )

  override val reactHost: ReactHost
    get() = ReactNativeHostWrapper.createReactHost(
      this.applicationContext,
      reactNativeHost
    )

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this, /* native exopackage */ false)
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      // Carrega bibliotecas da Nova Arquitetura
      DefaultNewArchitectureEntryPoint.load()
    }
    ReactNativeFlipper.initializeFlipper(this, reactNativeHost.reactInstanceManager)
    
    // Adicionado pelo Expo
    ApplicationLifecycleDispatcher.onApplicationCreate(this)
  }
}