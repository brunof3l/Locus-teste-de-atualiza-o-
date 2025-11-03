package com.brunof3l.locus.ui.screens

import android.annotation.SuppressLint
import android.graphics.Rect
import java.util.concurrent.TimeUnit

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.FocusMeteringAction
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.camera.core.ImageProxy
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import com.brunof3l.locus.ui.components.LocusHeader
import com.brunof3l.locus.ui.components.PrimaryButton
import androidx.compose.foundation.shape.RoundedCornerShape

@SuppressLint("UnsafeOptInUsageError")
private fun setupAnalyzer(onBarcode: (String) -> Unit): ImageAnalysis.Analyzer {
  val options = BarcodeScannerOptions.Builder()
    .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39, Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
    .build()
  val scanner = BarcodeScanning.getClient(options)
  return ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
      val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
      scanner.process(image)
        .addOnSuccessListener { barcodes ->
          val w = imageProxy.width
          val h = imageProxy.height
          // ROI central (aprox) para priorizar leitura no centro
          val roi = Rect((w * 0.2).toInt(), (h * 0.25).toInt(), (w * 0.8).toInt(), (h * 0.75).toInt())
          val centered = barcodes.firstOrNull { b ->
            val box = b.boundingBox
            box != null && roi.contains(box.centerX(), box.centerY())
          }
          val raw = (centered ?: barcodes.firstOrNull())?.rawValue
          if (raw != null) onBarcode(raw)
        }
        .addOnCompleteListener { imageProxy.close() }
        .addOnFailureListener { imageProxy.close() }
    } else { imageProxy.close() }
  }
}

@Composable
fun ScannerScreen(onFound: (String) -> Unit, onNotFound: (String) -> Unit, onBack: () -> Unit) {
  val context = LocalContext.current
  val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
  var locked by remember { mutableStateOf(false) }
  val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }
  LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.CAMERA) }

  Column(Modifier.fillMaxSize()) {
    LocusHeader(title = "Scanner")
    Column(Modifier.fillMaxSize().padding(16.dp)) {
      Text("Centralize o código no retângulo para ler", style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(8.dp))
      Box(Modifier.fillMaxWidth().weight(1f)) {
        AndroidView(factory = { ctx -> PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } },
          modifier = Modifier.matchParentSize(),
          update = { previewView ->
            val providerFuture = ProcessCameraProvider.getInstance(context)
            providerFuture.addListener({
              val cameraProvider = providerFuture.get()
              val preview = Preview.Builder().build().apply { setSurfaceProvider(previewView.surfaceProvider) }
              val analysis = ImageAnalysis.Builder()
                .setTargetRotation(previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
              analysis.setAnalyzer(ContextCompat.getMainExecutor(context), setupAnalyzer { raw ->
                if (!locked) { locked = true; onFound(raw.trim()) }
              })
              val selector = CameraSelector.DEFAULT_BACK_CAMERA
              try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
                // Foco/Metering no centro do retângulo
                val factory = previewView.meteringPointFactory
                val centerPoint = factory.createPoint(previewView.width / 2f, previewView.height / 2f)
                val action = FocusMeteringAction.Builder(centerPoint, FocusMeteringAction.FLAG_AF)
                  .setAutoCancelDuration(3, TimeUnit.SECONDS)
                  .build()
                camera.cameraControl.startFocusAndMetering(action)
              } catch (_: Exception) {}
            }, ContextCompat.getMainExecutor(context))
          }
        )
        // Overlay escurecido fora do retângulo central
        BoxWithConstraints(Modifier.matchParentSize()) {
          val frameWidth = maxWidth * 0.9f
          val frameHeight = frameWidth / 1.8f
          val topHeight = (maxHeight - frameHeight) / 2
          val sideWidth = (maxWidth - frameWidth) / 2
          // Topo
          Box(Modifier.align(Alignment.TopCenter).fillMaxWidth().height(topHeight).background(Color(0x99000000)))
          // Base
          Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(topHeight).background(Color(0x99000000)))
          // Lado esquerdo
          Box(Modifier.align(Alignment.CenterStart).width(sideWidth).height(frameHeight).background(Color(0x99000000)))
          // Lado direito
          Box(Modifier.align(Alignment.CenterEnd).width(sideWidth).height(frameHeight).background(Color(0x99000000)))
        }
        // Moldura retangular central
        Box(
          Modifier
            .align(Alignment.Center)
            .fillMaxWidth(0.9f)
            .aspectRatio(1.8f)
            .border(3.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
        )
      }
      Spacer(Modifier.height(12.dp))
      PrimaryButton(text = "Voltar", onClick = onBack, modifier = Modifier.fillMaxWidth())
    }
  }
}