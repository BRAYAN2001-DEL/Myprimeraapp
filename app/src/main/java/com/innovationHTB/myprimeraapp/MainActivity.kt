package com.innovationHTB.myprimeraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovationHTB.myprimeraapp.ui.theme.MyPrimeraAppTheme
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract
import kotlinx.coroutines.launch

import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private var qrResult by mutableStateOf("")

    private val scanQrLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            qrResult = result.contents
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPrimeraAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            QRButton(onScanClick = { openQrScanner() })
                        }
                    }
                )

                if (qrResult.isNotEmpty()) {
                    QRResultDialog(qrResult) {
                        qrResult = ""
                    }
                }
            }
        }
    }

    private fun openQrScanner() {
        scanQrLauncher.launch(ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setCameraId(0)
            setPrompt("Escanear código QR")
            setBeepEnabled(true)
        })
    }
}

@Composable
fun QRButton(onScanClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Escáner Auditoria QR",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(onClick = onScanClick) {
                Text("Escanear QR")
            }
        }
    }
}

@Composable
fun QRResultDialog(result: String, onEditClick: () -> Unit) {
    var modelo by remember { mutableStateOf("") }
    var serie by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var nombreActivo by remember { mutableStateOf("") }
    var personaAsignada by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("Resultado del Activo Escaneado")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = result,
                    onValueChange = {},
                    label = { Text("Resultado") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = serie, onValueChange = { serie = it }, label = { Text("Serie") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = nombreActivo, onValueChange = { nombreActivo = it }, label = { Text("Nombre del Activo") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = personaAsignada, onValueChange = { personaAsignada = it }, label = { Text("Persona Asignada") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cedula") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = onEditClick) {
                Text("Editar")
            }
        }
    )
}
