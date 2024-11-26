package com.innovationHTB.myprimeraapp

import android.os.Bundle
import android.util.Log
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
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("Resultado del Activo Escaneado")
        },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = result,
                        onValueChange = {},
                        label = { Text("Resultado") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                Log.d("QRResultDialog", "Iniciando solicitud al servidor...")

                                val activo = RetrofitInstance.apiService.getActivo(result)
                                modelo = activo.modelo
                                serie = activo.serie
                                marca = activo.marca
                                nombreActivo = activo.nombre_del_activo
                                personaAsignada = activo.persona_asignada
                                cedula = activo.cedula
                                Log.d("QRResultDialog", "Datos recibidos: $activo")

                            } catch (e: Exception) {
                                Log.e("QRResultDialog", "Error al obtener datos: ${e.message}")
                            }
                        }
                    }) {
                        Text("Buscar")
                    }
                }

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
                OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cédula") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = onEditClick) {
                Text("Editar")
            }
        }
    )
}

data class Activo(
    val modelo: String,
    val serie: String,
    val marca: String,
    val nombre_del_activo: String,
    val persona_asignada: String,
    val cedula: String
)

interface ApiService {
    @GET("activos_auditoria.php")
    suspend fun getActivo(@Query("id") id: String): Activo
}

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.66.32/activosqr/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
