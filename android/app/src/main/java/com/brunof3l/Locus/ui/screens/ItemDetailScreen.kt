package com.brunof3l.locus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brunof3l.locus.data.FirebaseRepository
import com.brunof3l.locus.data.Patrimonio
import com.brunof3l.locus.ui.components.LocusHeader
import com.brunof3l.locus.ui.components.LocusCard
import com.brunof3l.locus.ui.components.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun ItemDetailScreen(cod: String, onBack: () -> Unit) {
  val repo = remember { FirebaseRepository() }
  var item by remember { mutableStateOf<Patrimonio?>(null) }
  var error by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(cod) {
    scope.launch {
      try { item = repo.getByCod(cod) } catch (e: Exception) { error = e.message }
    }
  }

  Column(Modifier.fillMaxSize()) {
    LocusHeader(title = "Detalhe")
    Column(Modifier.fillMaxSize().padding(16.dp)) {
      LocusCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
          Text("Nº Patrimônio: $cod", style = MaterialTheme.typography.titleMedium)
          Spacer(Modifier.height(8.dp))
          Text(item?.DESCRICAO ?: "Sem descrição", style = MaterialTheme.typography.bodyLarge)
          Spacer(Modifier.height(8.dp))
          Text("Localização: ${item?.LOCALIZACAO ?: "-"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
      }
      Spacer(Modifier.height(24.dp))
      PrimaryButton(text = "Voltar", onClick = onBack, modifier = Modifier.fillMaxWidth())
    }
  }
}