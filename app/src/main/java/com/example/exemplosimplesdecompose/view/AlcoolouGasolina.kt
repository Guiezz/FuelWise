package com.example.exemplosimplesdecompose.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate // Adicionado
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EvStation // Adicionado
import androidx.compose.material.icons.filled.LocalGasStation // Adicionado
import androidx.compose.material.icons.filled.Storefront // Adicionado
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.Utils.SwitchPreferences
import com.example.exemplosimplesdecompose.data.Coordenadas
import com.example.exemplosimplesdecompose.data.Posto
import com.example.exemplosimplesdecompose.data.PostoPrefs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlcoolGasolinaPreco(navController: NavHostController) {
    val context = LocalContext.current // Contexto da Composable
    val switchPrefs = remember { SwitchPreferences(context) }
    val postoPrefs = remember { PostoPrefs(context) }

    var alcool by remember { mutableStateOf("") }
    var gasolina by remember { mutableStateOf("") }
    var nomeDoPosto by remember { mutableStateOf("") }

    var checkedState by remember { mutableStateOf(switchPrefs.getSwitchState()) }
    var result by remember { mutableStateOf("Preencha os campos para calcular.") }

    var lastLocation by remember { mutableStateOf<Location?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getLastKnownLocation(context, fusedLocationClient) { // Passando o context
                    lastLocation = it
                }
            } else {
                result = "Permissão de localização negada. Coordenadas não serão salvas."
            }
        }
    )

    LaunchedEffect(Unit) {
        checkedState = switchPrefs.getSwitchState()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getLastKnownLocation(context, fusedLocationClient) { lastLocation = it } // Passando o context
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calcular & Salvar Posto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val alcoholValue = alcool.replace(",", ".").toDoubleOrNull()
                    val gasValue = gasolina.replace(",", ".").toDoubleOrNull()

                    if (nomeDoPosto.isBlank() && (alcoholValue == null || gasValue == null || alcoholValue == 0.0 || gasValue == 0.0)) {
                        result = "Informe o nome do posto e/ou preços válidos para salvar."
                        return@FloatingActionButton
                    }

                    postoPrefs.salvarPosto(
                        Posto(
                            nome = nomeDoPosto.ifBlank { "Posto S/ Nome (${System.currentTimeMillis()})" },
                            coordenadas = Coordenadas(
                                latitude = lastLocation?.latitude ?: 0.0,
                                longitude = lastLocation?.longitude ?: 0.0
                            ),
                            precoAlcool = alcoholValue,
                            precoGasolina = gasValue
                        )
                    )

                    val nomeNavegacao = nomeDoPosto.ifBlank { "Posto" }
                    val latNavegacao = lastLocation?.latitude ?: 0.0
                    val lngNavegacao = lastLocation?.longitude ?: 0.0
                    navController.navigate("lista/$nomeNavegacao/$latNavegacao/$lngNavegacao") {
                        popUpTo("mainalcgas") { inclusive = true }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "Salvar e listar postos")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Dados do Posto e Preços:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = nomeDoPosto,
                onValueChange = { nomeDoPosto = it },
                label = { Text("Nome do Posto (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Storefront, contentDescription = "Nome do Posto") },
                singleLine = true
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = alcool,
                    onValueChange = { alcool = it },
                    label = { Text("Álcool") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Filled.EvStation, contentDescription = "Preço Álcool") },
                    trailingIcon = { Text("R$") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = gasolina,
                    onValueChange = { gasolina = it },
                    label = { Text("Gasolina") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Filled.LocalGasStation, contentDescription = "Preço Gasolina") },
                    trailingIcon = { Text("R$") },
                    singleLine = true
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            "Resultado (Álcool < 70% da Gasolina):",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            if (checkedState) "Álcool parece vantajoso." else "Gasolina parece vantajosa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = checkedState,
                        onCheckedChange = {
                            checkedState = it
                            switchPrefs.saveSwitchState(it)
                        },
                        thumbContent = {
                            if (checkedState) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Álcool Vantajoso",
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }

            Button(
                onClick = {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    } else {
                        getLastKnownLocation(context, fusedLocationClient) { lastLocation = it } // Passando o context
                    }

                    val alcoholPrice = alcool.replace(",", ".").toDoubleOrNull()
                    val gasPrice = gasolina.replace(",", ".").toDoubleOrNull()
                    val currentPostoNome = nomeDoPosto.ifEmpty { "Este posto" }

                    when {
                        alcoholPrice == null || gasPrice == null -> {
                            result = "Valores de preço inválidos."
                        }
                        gasPrice == 0.0 -> {
                            result = "Preço da gasolina não pode ser zero."
                        }
                        alcoholPrice / gasPrice < 0.7 -> {
                            checkedState = true
                            switchPrefs.saveSwitchState(true)
                            result = "$currentPostoNome: Álcool é a melhor opção!"
                        }
                        else -> {
                            checkedState = false
                            switchPrefs.saveSwitchState(false)
                            result = "$currentPostoNome: Gasolina é a melhor opção!"
                        }
                    }

                    lastLocation?.let {
                        result += "\nCoordenadas: (${String.format("%.4f", it.latitude)}, ${String.format("%.4f", it.longitude)})"
                    } ?: run {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            result += "\n(Tentando obter localização...)"
                        } else {
                            result += "\n(Localização não disponível - permissão negada)"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Calculate, contentDescription = "Calcular", modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Calcular Melhor Opção")
            }

            if (result != "Preencha os campos para calcular.") {
                Text(
                    text = result,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .animateContentSize(),
                    textAlign = TextAlign.Center,
                    color = if (result.contains("Álcool", ignoreCase = true) && checkedState) MaterialTheme.colorScheme.primary
                    else if (result.contains("Gasolina", ignoreCase = true) && !checkedState) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getLastKnownLocation(
    context: android.content.Context, // Adicionado o context como parâmetro
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    // A verificação de permissão é feita antes de chamar esta função.
    // No entanto, uma verificação defensiva aqui não é ruim.
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        onLocationReceived(null) // Permissão não concedida
        return
    }

    fusedLocationProviderClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationReceived(location)
            } else {
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L).apply { // Uso do Builder
                    setMinUpdateIntervalMillis(5000L)
                    setMaxUpdates(1) // Corrigido para setMaxUpdates
                }.build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        onLocationReceived(locationResult.lastLocation)
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
        .addOnFailureListener {
            onLocationReceived(null)
        }
}