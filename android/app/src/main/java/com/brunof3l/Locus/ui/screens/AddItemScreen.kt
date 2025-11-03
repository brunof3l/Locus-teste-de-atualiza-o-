package com.brunof3l.locus.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.brunof3l.locus.data.FirebaseRepository
import com.brunof3l.locus.data.Patrimonio
import com.brunof3l.locus.ui.components.LocusHeader
import com.brunof3l.locus.ui.components.LocusCard
import com.brunof3l.locus.ui.components.LocusInput
import com.brunof3l.locus.ui.components.PrimaryButton
import com.brunof3l.locus.ui.components.OutlineButton
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddItemScreen(initialCod: String? = null, onSaved: (String) -> Unit, onBack: () -> Unit) {
  val repo = remember { FirebaseRepository() }
  val context = LocalContext.current
  var cod by remember { mutableStateOf(initialCod ?: "") }
  var descricao by remember { mutableStateOf("") }
  var local by remember { mutableStateOf("") }
  var estado by remember { mutableStateOf("Novo") }
  var marca by remember { mutableStateOf("") }
  var modelo by remember { mutableStateOf("") }
  var numeroSerie by remember { mutableStateOf("") }
  var setor by remember { mutableStateOf("") }
  var observacao by remember { mutableStateOf("") }
  var dueDateMillis by remember { mutableStateOf<Long?>(null) }
  var dueDateText by remember { mutableStateOf("") }
  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var saving by remember { mutableStateOf(false) }
  var error by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    selectedImageUri = uri
  }
  val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
    selectedImageBitmap = bmp
  }

  Column(Modifier.fillMaxSize()) {
    LocusHeader(title = "Cadastro")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
      LocusCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
          LocusInput(value = cod, onValueChange = { cod = it }, label = "Nº Patrimônio", singleLine = true)
          Spacer(Modifier.height(8.dp))
          LocusInput(value = descricao, onValueChange = { descricao = it }, label = "Descrição")
          Spacer(Modifier.height(8.dp))
          LocusInput(value = local, onValueChange = { local = it }, label = "Localização")
          Spacer(Modifier.height(8.dp))
          // Substituir campo Estado por dropdown
          Text("Estado", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(8.dp))
          var expanded by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
              value = estado,
              onValueChange = { },
              readOnly = true,
              label = { Text("Selecione o estado") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              listOf("Novo", "Usado", "Em manutenção", "Danificado").forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { estado = option; expanded = false })
              }
            }
          }
          Spacer(Modifier.height(8.dp))
          // Seção de imagem: tirar foto ou escolher da galeria
          Text("Imagem do Item", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(8.dp))
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlineButton(text = "Tirar Foto", onClick = { cameraLauncher.launch(null) }, modifier = Modifier.weight(1f))
            OutlineButton(text = "Galeria", onClick = { imagePicker.launch("image/*") }, modifier = Modifier.weight(1f))
          }
          Spacer(Modifier.height(8.dp))
          if (selectedImageBitmap != null) {
            LocusCard(modifier = Modifier.fillMaxWidth()) {
              Column { Text("Foto capturada", style = MaterialTheme.typography.titleSmall); Spacer(Modifier.height(8.dp)); Image(bitmap = selectedImageBitmap!!.asImageBitmap(), contentDescription = "Prévia", modifier = Modifier.fillMaxWidth().height(180.dp)) }
            }
          } else if (selectedImageUri != null) {
            Text("Imagem selecionada da galeria", style = MaterialTheme.typography.bodyMedium)
          }
          Spacer(Modifier.height(8.dp))
          LocusInput(value = setor, onValueChange = { setor = it }, label = "Setor Responsável")
          Spacer(Modifier.height(8.dp))
          LocusInput(value = observacao, onValueChange = { observacao = it }, label = "Observação")
          Spacer(Modifier.height(12.dp))
          // Vencimento da calibração
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column { Text("Vencimento da Calibração", style = MaterialTheme.typography.titleMedium); if (dueDateText.isNotBlank()) Text(dueDateText, style = MaterialTheme.typography.bodyMedium) }
            OutlineButton(text = if (dueDateMillis != null) "Alterar" else "Selecionar", onClick = {
              val cal = Calendar.getInstance()
              val dlg = DatePickerDialog(context, { _, y, m, d ->
                val picked = Calendar.getInstance().apply { set(y, m, d, 0, 0, 0); set(Calendar.MILLISECOND, 0) }
                dueDateMillis = picked.timeInMillis
                dueDateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(picked.time)
              }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
              dlg.show()
            })
          }
          // Removed duplicate last photo field
        }
      }
      Spacer(Modifier.height(16.dp))
      PrimaryButton(text = if (saving) "Salvando..." else "Salvar", onClick = {
        scope.launch {
          saving = true; error = null
          try {
            var downloadUrl: String? = null
            val codTrim = cod.trim()
            if (selectedImageUri != null && codTrim.isNotBlank()) {
              val ref = FirebaseStorage.getInstance().reference.child("patrimonio/${codTrim}.jpg")
              ref.putFile(selectedImageUri!!).await()
              downloadUrl = ref.downloadUrl.await().toString()
            }
            val p = Patrimonio(
              COD = codTrim,
              DESCRICAO = descricao.trim(),
              LOCALIZACAO = local.trim(),
              ESTADO = estado.trim(),
              MARCA = marca.ifBlank { null },
              MODELO = modelo.ifBlank { null },
              NUMERO_SERIE = numeroSerie.ifBlank { null },
              SETOR_RESPONSAVEL = setor.ifBlank { null },
              OBSERVACAO = observacao.ifBlank { null },
              CALIBRACAO_VENCIMENTO = dueDateMillis,
              imageUrl = downloadUrl
            )
            repo.addOrUpdate(p.COD, p)
            if (dueDateMillis != null) {
              com.brunof3l.locus.ui.alerts.CalibrationAlertScheduler.scheduleCalibrationAlerts(context, p.COD, p.DESCRICAO, dueDateMillis!!)
            }
            onSaved(p.COD)
          } catch (e: Exception) { error = e.message }
          finally { saving = false }
        }
      }, modifier = Modifier.fillMaxWidth())
      Spacer(Modifier.height(8.dp))
      OutlineButton(text = "Voltar para Home", onClick = onBack, modifier = Modifier.fillMaxWidth())
      if (error != null) {
        Spacer(Modifier.height(8.dp))
        Text(error!!, color = MaterialTheme.colorScheme.error)
      }
    }
  }
}