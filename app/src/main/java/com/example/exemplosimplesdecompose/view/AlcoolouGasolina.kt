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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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

@Composable
fun AlcoolGasolinaPreco(navController: NavHostController) {
    val context = LocalContext.current
    val switchPrefs = remember { SwitchPreferences(context) }
    val postoPrefs = remember { PostoPrefs(context) }

    var alcool by remember { mutableStateOf("") }
    var gasolina by remember { mutableStateOf("") }
    var nomeDoPosto by remember { mutableStateOf("") }

    var checkedState by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf("Vamos Calcular?") }

    var lastLocation by remember { mutableStateOf<Location?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getLastKnownLocation(fusedLocationClient) {
                    lastLocation = it
                }
            } else {
                result = "Permissão de localização negada"
            }
        }
    )

    LaunchedEffect(Unit) {
        checkedState = switchPrefs.getSwitchState()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = alcool,
                onValueChange = { alcool = it },
                label = { Text("Preço do Álcool (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = gasolina,
                onValueChange = { gasolina = it },
                label = { Text("Preço da Gasolina (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = nomeDoPosto,
                onValueChange = { nomeDoPosto = it },
                label = { Text("Nome do Posto (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "75%",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
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
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val alcohol = alcool.replace(",", ".").toDoubleOrNull()
                    val gas = gasolina.replace(",", ".").toDoubleOrNull()

                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        return@Button
                    } else {
                        getLastKnownLocation(fusedLocationClient) {
                            lastLocation = it
                        }
                    }

                    when {
                        alcohol == null || gas == null -> {
                            result = "Digite valores válidos"
                        }

                        gas == 0.0 -> {
                            result = "Preço da gasolina inválido"
                        }

                        alcohol / gas < 0.7 -> {
                            checkedState = true
                            switchPrefs.saveSwitchState(true)
                            result = "${nomeDoPosto.ifEmpty { "O posto" }}: Melhor Álcool!"
                        }

                        else -> {
                            checkedState = false
                            switchPrefs.saveSwitchState(false)
                            result = "${nomeDoPosto.ifEmpty { "O posto" }}: Melhor Gasolina!"
                        }
                    }

                    lastLocation?.let {
                        result += "\nLocalização: (${it.latitude}, ${it.longitude})"
                    } ?: run {
                        result += "\nLocalização não disponível"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcular")
            }

            Text(
                text = result,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .animateContentSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = {
                        val alcohol = alcool.replace(",", ".").toDoubleOrNull()
                        val gas = gasolina.replace(",", ".").toDoubleOrNull()

                        postoPrefs.salvarPosto(
                            Posto(
                                nome = nomeDoPosto.ifBlank { "Posto" },
                                coordenadas = Coordenadas(
                                    latitude = lastLocation?.latitude ?: 0.0,
                                    longitude = lastLocation?.longitude ?: 0.0
                                ),
                                precoAlcool = alcohol, // Salva o preço do álcool
                                precoGasolina = gas // Salva o preço da gasolina
                            )
                        )

                        val nome = nomeDoPosto.ifBlank { "Posto" }
                        val lat = lastLocation?.latitude ?: 0.0
                        val lng = lastLocation?.longitude ?: 0.0

                        navController.navigate("lista/$nome/$lat/$lng")
                    }
                ) {
                    Icon(Icons.Filled.Add, "Inserir Posto")
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getLastKnownLocation(
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    fusedLocationProviderClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationReceived(location)
            } else {
                // Se lastLocation for nula, solicita nova atualização
                val locationRequest = LocationRequest.create().apply {
                    interval = 1000
                    fastestInterval = 500
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                    numUpdates = 1
                }

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        onLocationReceived(result.lastLocation)
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
}
