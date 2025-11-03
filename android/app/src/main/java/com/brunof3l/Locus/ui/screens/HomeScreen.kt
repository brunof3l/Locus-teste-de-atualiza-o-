package com.brunof3l.locus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.brunof3l.locus.data.FirebaseRepository
import com.brunof3l.locus.data.Patrimonio
import com.brunof3l.locus.ui.components.LocusHeader
import com.brunof3l.locus.ui.components.LocusCard
import com.brunof3l.locus.ui.components.PrimaryButton
import com.brunof3l.locus.ui.components.OutlineButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

@Composable
fun HomeScreen(onGoScan: () -> Unit, onGoItems: () -> Unit, onGoAdd: () -> Unit, onGoAdmin: () -> Unit) {
  val repo = remember { FirebaseRepository() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var items by remember { mutableStateOf(listOf<Patrimonio>()) }
  val userName = remember { FirebaseAuth.getInstance().currentUser?.displayName ?: FirebaseAuth.getInstance().currentUser?.email ?: "Usuário" }

  LaunchedEffect(Unit) {
    scope.launch {
      repo.items().collect { items = it }
    }
  }

  val createFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
    if (uri != null) {
      try {
        val header = listOf(
          "COD","DESCRICAO","LOCALIZACAO","ESTADO","MARCA","MODELO","NUMERO_SERIE","SETOR_RESPONSAVEL","OBSERVACAO","CALIBRACAO_VENCIMENTO","FOTO_URL"
        )
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val lines = items.map { p ->
          val venc = p.CALIBRACAO_VENCIMENTO?.let { sdf.format(Date(it)) } ?: ""
          listOf(p.COD,p.DESCRICAO,p.LOCALIZACAO,p.ESTADO,p.MARCA ?: "",p.MODELO ?: "",p.NUMERO_SERIE ?: "",p.SETOR_RESPONSAVEL ?: "",p.OBSERVACAO ?: "",venc,p.imageUrl ?: "")
            .joinToString(",")
        }
        context.contentResolver.openOutputStream(uri)?.use { out ->
          out.write((header.joinToString(",") + "\n").toByteArray())
          lines.forEach { line -> out.write((line + "\n").toByteArray()) }
        }
        Toast.makeText(context, "Exportado com sucesso!", Toast.LENGTH_SHORT).show()
      } catch (e: Exception) {
        Toast.makeText(context, "Erro ao exportar: ${e.message}", Toast.LENGTH_LONG).show()
      }
    }
  }

  Column(Modifier.fillMaxSize()) {
    Scaffold(
      topBar = {
        LocusHeader(title = "Início", right = {
          OutlineButton(text = "Sair", onClick = { repo.signOut(); Toast.makeText(context, "Sessão encerrada", Toast.LENGTH_SHORT).show() })
        })
      },
      bottomBar = {
        NavigationBar {
          NavigationBarItem(
            selected = true,
            onClick = { /* já na Home */ },
            label = { Text("Menu") },
            icon = { }
          )
          NavigationBarItem(
            selected = false,
            onClick = onGoItems,
            label = { Text("Itens") },
            icon = { }
          )
          NavigationBarItem(
            selected = false,
            onClick = onGoAdmin,
            label = { Text("Admin") },
            icon = { }
          )
        }
      }
    ) { padding ->
      Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
        Text("Bem-vindo,", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(4.dp))
        Text(userName, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
    
        Spacer(Modifier.height(16.dp))
        LocusCard(modifier = Modifier.fillMaxWidth()) {
          Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            PrimaryButton(text = "Escanear Novo Patrimônio", onClick = onGoScan, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlineButton(text = "Exportar Dados para Excel", onClick = { createFileLauncher.launch("locus_export_${System.currentTimeMillis()}.csv") }, modifier = Modifier.fillMaxWidth())
          }
        }
        Spacer(Modifier.height(16.dp))
        LocusCard(modifier = Modifier.fillMaxWidth()) {
          Column(Modifier.fillMaxWidth()) {
            Text("Itens já cadastrados", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            if (items.isEmpty()) {
              Text("Nenhum item cadastrado", style = MaterialTheme.typography.bodyMedium)
            } else {
              items.take(5).forEach { p ->
                Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                  Text(p.DESCRICAO.ifBlank { "Sem descrição" }, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                  Spacer(Modifier.height(2.dp))
                  Text("Nº Patrimônio: ${p.COD}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                  Text("Localização: ${p.LOCALIZACAO}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
              }
              Spacer(Modifier.height(8.dp))
              OutlineButton(text = "Ver todos", onClick = onGoItems, modifier = Modifier.fillMaxWidth())
            }
          }
        }
      }
    }
  }
}