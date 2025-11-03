package com.brunof3l.locus

import android.app.Application
import com.google.firebase.FirebaseApp

class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // Inicializa Firebase usando google-services.json
    FirebaseApp.initializeApp(this)
  }
}
