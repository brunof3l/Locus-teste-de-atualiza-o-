package com.brunof3l.locus.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brunof3l.locus.ui.screens.AddItemScreen
import com.brunof3l.locus.ui.screens.AdminScreen
import com.brunof3l.locus.ui.screens.HomeScreen
import com.brunof3l.locus.ui.screens.ItemDetailScreen
import com.brunof3l.locus.ui.screens.ItemsScreen
import com.brunof3l.locus.ui.screens.LoginScreen
import com.brunof3l.locus.ui.screens.ScannerScreen
import com.brunof3l.locus.ui.screens.SignupScreen

@Composable
fun AppNav() {
  val nav = rememberNavController()
  NavHost(navController = nav, startDestination = "login") {
    composable("login") { LoginScreen(onLoggedIn = { nav.navigate("home") }, onGoSignup = { nav.navigate("signup") }) }
    composable("signup") { SignupScreen(onSignupSuccess = { nav.popBackStack(); nav.navigate("login") }, onGoLogin = { nav.popBackStack(); nav.navigate("login") }) }
    composable("home") { HomeScreen(
      onGoScan = { nav.navigate("scanner") },
      onGoItems = { nav.navigate("items") },
      onGoAdd = { nav.navigate("addItem") },
      onGoAdmin = { nav.navigate("admin") }
    ) }
    composable("scanner") { ScannerScreen(
      onFound = { cod -> nav.navigate("addItem?cod=$cod") },
      onNotFound = { cod -> nav.navigate("addItem?cod=$cod") },
      onBack = { nav.popBackStack() }
    ) }
    composable("items") { ItemsScreen(
      onOpenDetail = { cod -> nav.navigate("detail/$cod") },
      onGoHome = { nav.navigate("home") },
      onGoAdmin = { nav.navigate("admin") }
    ) }
    composable(
      route = "addItem?cod={cod}",
      arguments = listOf(navArgument("cod") { type = NavType.StringType; defaultValue = ""; nullable = true })
    ) { backStackEntry ->
      val initialCod = backStackEntry.arguments?.getString("cod")
      AddItemScreen(initialCod = initialCod, onSaved = { cod -> nav.navigate("detail/$cod") }, onBack = { nav.navigate("home") })
    }
    composable(
      route = "detail/{cod}",
      arguments = listOf(navArgument("cod") { type = NavType.StringType })
    ) { backStackEntry ->
      val cod = backStackEntry.arguments?.getString("cod") ?: ""
      ItemDetailScreen(cod = cod, onBack = { nav.popBackStack() })
    }
    composable("admin") { AdminScreen(
      onGoHome = { nav.navigate("home") },
      onGoItems = { nav.navigate("items") },
      onBack = { nav.popBackStack() }
    ) }
  }
}