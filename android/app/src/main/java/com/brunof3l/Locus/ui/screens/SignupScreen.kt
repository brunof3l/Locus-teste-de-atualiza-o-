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
import com.brunof3l.locus.ui.components.LocusCard
import com.brunof3l.locus.ui.components.LocusInput
import com.brunof3l.locus.ui.components.PrimaryButton
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun SignupScreen(onSignupSuccess: () -> Unit, onGoLogin: () -> Unit = {}) {
  val repo = remember { FirebaseRepository() }
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirm by remember { mutableStateOf("") }
  var loading by remember { mutableStateOf(false) }
  var error by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  fun validate(): String? {
    if (name.trim().isEmpty()) return "Informe seu nome"
    if (email.trim().isEmpty()) return "Informe seu e-mail"
    if (!email.contains("@")) return "E-mail inválido"
    if (password.length < 6) return "A senha deve ter 6+ caracteres"
    if (password != confirm) return "As senhas não conferem"
    return null
  }

  Box(Modifier.fillMaxSize()) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(24.dp)
    ) {
      Spacer(Modifier.height(40.dp))
      Text(
        text = "Cadastro",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp),
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(6.dp))
      Text(
        text = "Crie sua conta para usar o Locus",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )
      Spacer(Modifier.height(24.dp))

      LocusCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
          Text("Nome", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(8.dp))
          LocusInput(value = name, onValueChange = { name = it }, label = "Digite seu nome...", modifier = Modifier.fillMaxWidth(), singleLine = true)
          Spacer(Modifier.height(16.dp))
          Text("Email", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(8.dp))
          LocusInput(value = email, onValueChange = { email = it }, label = "Digite seu e-mail...", modifier = Modifier.fillMaxWidth(), singleLine = true)
          Spacer(Modifier.height(16.dp))
          Text("Senha", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(8.dp))
          LocusInput(value = password, onValueChange = { password = it }, label = "Crie uma senha...", modifier = Modifier.fillMaxWidth(), singleLine = true)
          Spacer(Modifier.height(16.dp))
          Text("Confirmar senha", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(8.dp))
          LocusInput(value = confirm, onValueChange = { confirm = it }, label = "Repita a senha...", modifier = Modifier.fillMaxWidth(), singleLine = true)
          Spacer(Modifier.height(16.dp))
          PrimaryButton(text = if (loading) "Cadastrando..." else "Cadastrar", onClick = {
            val v = validate()
            if (v != null) { error = v; return@PrimaryButton }
            scope.launch {
              loading = true; error = null
              try {
                repo.signUp(name.trim(), email.trim(), password)
                onSignupSuccess()
              } catch (e: Exception) {
                error = e.message ?: "Erro ao cadastrar"
              } finally {
                loading = false
              }
            }
          }, modifier = Modifier.fillMaxWidth())
        }
      }

      Spacer(Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
      ) {
        Text("Já tem conta? ", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        TextButton(onClick = onGoLogin) {
          Text("Entrar", color = MaterialTheme.colorScheme.primary)
        }
      }

      if (error != null) {
        Spacer(Modifier.height(8.dp))
        Text(error!!, color = MaterialTheme.colorScheme.error)
      }
    }
  }
}