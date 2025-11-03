package com.brunof3l.locus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brunof3l.locus.data.FirebaseRepository
import com.brunof3l.locus.data.User
import com.brunof3l.locus.ui.components.LocusHeader
import com.brunof3l.locus.ui.components.LocusCard
import com.brunof3l.locus.ui.components.LocusInput
import com.brunof3l.locus.ui.components.OutlineButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onGoHome: () -> Unit, onGoItems: () -> Unit, onBack: () -> Unit = {}) {
  val repo = remember { FirebaseRepository() }
  var users by remember { mutableStateOf(listOf<User>()) }
  var queryText by remember { mutableStateOf("") }
  var editRoles by remember { mutableStateOf(mutableMapOf<String, String>()) }
  var savingUid by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    repo.users().collect { list ->
      users = list
      // inicializar papéis editáveis
      val next = editRoles.toMutableMap()
      list.forEach { u -> if (next[u.uid] == null) next[u.uid] = u.role }
      editRoles = next
    }
  }

  val filtered = remember(users, queryText) {
    val s = queryText.lowercase()
    if (s.isBlank()) users else users.filter { u ->
      listOf(u.uid, u.email ?: "", u.displayName ?: "", u.role)
        .map { it.lowercase() }
        .any { it.contains(s) }
    }
  }

  Scaffold(
    bottomBar = {
      NavigationBar {
        NavigationBarItem(
          selected = false,
          onClick = onGoHome,
          icon = {},
          label = { Text("Menu") }
        )
        NavigationBarItem(
          selected = false,
          onClick = onGoItems,
          icon = {},
          label = { Text("Itens") }
        )
        NavigationBarItem(
          selected = true,
          onClick = {},
          icon = {},
          label = { Text("Admin") }
        )
      }
    }
  ) { padding ->
    Column(Modifier.fillMaxSize().padding(padding)) {
      LocusHeader(title = "Administração")
      Column(Modifier.fillMaxWidth().padding(16.dp)) {
        LocusInput(
          value = queryText,
          onValueChange = { queryText = it },
          label = "Buscar por nome, e-mail, UID ou papel",
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(Modifier.fillMaxSize()) {
          items(filtered) { u ->
            val currentRole = u.role
            val editedRole = editRoles[u.uid] ?: currentRole
            val dirty = editedRole != currentRole
            LocusCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
              Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(u.displayName ?: u.email ?: u.uid, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(4.dp))
                Text("E-mail: ${u.email ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                Text("UID: ${u.uid}", style = MaterialTheme.typography.bodyMedium)
                Text("Papel atual: $currentRole", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                  TextField(
                    value = editedRole,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Papel") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                  )
                  ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("user", "admin").forEach { opt ->
                      DropdownMenuItem(text = { Text(opt) }, onClick = {
                        editRoles = editRoles.toMutableMap().apply { put(u.uid, opt) }
                        expanded = false
                      })
                    }
                  }
                }

                Spacer(Modifier.height(8.dp))
                OutlineButton(
                  text = if (savingUid == u.uid) "Salvando..." else if (dirty) "Salvar papel" else "Sem alterações",
                  onClick = {
                    val roleToSave = editedRole
                    scope.launch {
                      try {
                        savingUid = u.uid
                        repo.updateUserRole(u.uid, roleToSave)
                      } catch (e: Exception) {
                        // em produção, mostrar um snackbar/Toast
                      } finally {
                        savingUid = null
                      }
                    }
                  },
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }
          if (filtered.isEmpty()) {
            item {
              Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("Nenhum usuário encontrado", style = MaterialTheme.typography.bodyMedium)
              }
            }
          }
        }
      }
    }
  }
}