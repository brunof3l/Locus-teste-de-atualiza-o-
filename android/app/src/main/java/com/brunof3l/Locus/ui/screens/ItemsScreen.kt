package com.brunof3l.locus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brunof3l.locus.data.FirebaseRepository
import com.brunof3l.locus.data.Patrimonio
import com.brunof3l.locus.ui.components.LocusHeader
import com.brunof3l.locus.ui.components.LocusCard
import com.brunof3l.locus.ui.components.LocusInput
import kotlinx.coroutines.launch

@Composable
fun ItemsScreen(onOpenDetail: (String) -> Unit, onGoHome: () -> Unit, onGoAdmin: () -> Unit) {
  val repo = remember { FirebaseRepository() }
  var queryText by remember { mutableStateOf("") }
  var items by remember { mutableStateOf<List<Patrimonio>>(emptyList()) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) { scope.launch { repo.items().collect { items = it } } }

  Scaffold(
    bottomBar = {
      NavigationBar {
        NavigationBarItem(selected = false, onClick = onGoHome, icon = {}, label = { Text("Menu") })
        NavigationBarItem(selected = true, onClick = {}, icon = {}, label = { Text("Itens") })
        NavigationBarItem(selected = false, onClick = onGoAdmin, icon = {}, label = { Text("Admin") })
      }
    }
  ) { padding ->
    Column(Modifier.fillMaxSize().padding(padding)) {
      LocusHeader(title = "Itens Cadastrados")
      Column(Modifier.fillMaxSize().padding(16.dp)) {
        LocusInput(value = queryText, onValueChange = { queryText = it }, label = "Buscar", modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.fillMaxSize()) {
          val filtered = items.filter {
            it.DESCRICAO.contains(queryText, ignoreCase = true) ||
            it.COD.contains(queryText, ignoreCase = true) ||
            it.LOCALIZACAO.contains(queryText, ignoreCase = true)
          }
          itemsIndexed(filtered) { _, item ->
            LocusCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onOpenDetail(item.COD) }) {
              Column(Modifier.fillMaxWidth()) {
                Text(item.DESCRICAO.ifBlank { "Sem descrição" }, style = MaterialTheme.typography.titleMedium)
                Text("Nº Patrimônio: ${item.COD}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text("Localização: ${item.LOCALIZACAO}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
              }
            }
          }
        }
      }
    }
  }
}