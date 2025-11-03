package com.brunof3l.locus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunof3l.locus.data.FirebaseRepository
import com.brunof3l.locus.ui.components.PrimaryButton
import com.brunof3l.locus.ui.components.LocusInput
import com.brunof3l.locus.ui.components.LocusCard
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun LoginScreen(onLoggedIn: () -> Unit, onGoSignup: () -> Unit = {}) {
  val repo = remember { FirebaseRepository() }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var loading by remember { mutableStateOf(false) }
  var error by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()
  var passwordVisible by remember { mutableStateOf(false) }

  Box(Modifier.fillMaxSize()) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(24.dp)
    ) {
      Spacer(Modifier.height(40.dp))
      Text(
        text = "LOCUS",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 36.sp),
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(6.dp))
      Text(
        text = "Gestão de Patrimônio",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(24.dp))

      LocusCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
          Text("Email", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(8.dp))
          LocusInput(value = email, onValueChange = { email = it }, label = "Digite seu e-mail...", modifier = Modifier.fillMaxWidth(), singleLine = true)
          Spacer(Modifier.height(16.dp))
          Text("Senha", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(8.dp))
          LocusInput(
            value = password,
            onValueChange = { password = it },
            label = "Digite sua senha...",
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Text(if (passwordVisible) "🙈" else "👁️")
              }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
          )
          Spacer(Modifier.height(16.dp))
          PrimaryButton(text = if (loading) "Entrando..." else "Entrar", leading = { Text("→", color = MaterialTheme.colorScheme.onPrimary) }, onClick = {
            scope.launch {
              loading = true; error = null
              try { repo.signIn(email.trim(), password) ; onLoggedIn() }
              catch (e: Exception) { error = e.message }
              finally { loading = false }
            }
          }, modifier = Modifier.fillMaxWidth())
        }
      }

      Spacer(Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
      ) {
        Text("Não tem conta? ", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        TextButton(onClick = onGoSignup) {
          Text("Cadastre-se", color = MaterialTheme.colorScheme.secondary)
        }
      }

      if (error != null) {
        Spacer(Modifier.height(8.dp))
        Text(error!!, color = MaterialTheme.colorScheme.error)
      }
    }
  }
}