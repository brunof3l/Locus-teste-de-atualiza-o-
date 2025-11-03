package com.brunof3l.locus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun LocusHeader(title: String, left: (@Composable () -> Unit)? = null, right: (@Composable () -> Unit)? = null) {
  Surface(color = MaterialTheme.colorScheme.surface) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(Modifier.weight(1f)) { left?.invoke() }
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.weight(2f)
        )
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) { right?.invoke() }
      }
      Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
    }
  }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, leading: (@Composable (() -> Unit))? = null) {
  Button(
    onClick = onClick,
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    )
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (leading != null) { leading(); Spacer(Modifier.width(8.dp)) }
      Text(text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
    }
  }
}

@Composable
fun OutlineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, active: Boolean = false) {
  val borderColor = if (active) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
  val container = if (active) MaterialTheme.colorScheme.secondary else Color.Transparent
  OutlinedButton(
    onClick = onClick,
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.5.dp, borderColor),
    colors = ButtonDefaults.outlinedButtonColors(containerColor = container, contentColor = MaterialTheme.colorScheme.primary)
  ) { Text(text, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)) }
}

@Composable
fun LocusCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Column(Modifier.padding(20.dp), content = content)
  }
}

@Composable
fun LocusInput(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  singleLine: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  trailingIcon: (@Composable (() -> Unit))? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    singleLine = singleLine,
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    visualTransformation = visualTransformation,
    trailingIcon = trailingIcon,
    keyboardOptions = keyboardOptions
  )
}