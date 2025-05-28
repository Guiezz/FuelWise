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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
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
    val context = LocalContext.current
    val switchPrefs = remember { SwitchPreferences(context) }
    val postoPrefs = remember { PostoPrefs(context) }

    var alcool by remember { mutableStateOf("") }
    var gasolina by remember { mutableStateOf("") }
    var nomeDoPosto by remember { mutableStateOf("") }

    var checkedState by remember { mutableStateOf(switchPrefs.getSwitchState()) }
    var result by remember { mutableStateOf(context.getString(R.string.fill_fields_to_calculate)) }

    var lastLocation by remember { mutableStateOf<Location?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getLastKnownLocation(context, fusedLocationClient) {
                    lastLocation = it
                }
            } else {
                result = context.getString(R.string.alc_gas_permission_denied)
            }
        }
    )

    LaunchedEffect(Unit) {
        checkedState = switchPrefs.getSwitchState()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getLastKnownLocation(context, fusedLocationClient) {
                lastLocation = it
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val alcoholValue = alcool.replace(",", ".").toDoubleOrNull()
                    val gasValue = gasolina.replace(",", ".").toDoubleOrNull()

                    if (nomeDoPosto.isBlank() && (alcoholValue == null || gasValue == null || alcoholValue == 0.0 || gasValue == 0.0)) {
                        result = context.getString(R.string.alc_gas_provide_station_name_or_prices)
                        return@FloatingActionButton
                    }

                    postoPrefs.salvarPosto(
                        Posto(
                            nome = nomeDoPosto.ifBlank { context.getString(R.string.unnamed_station) + " (${System.currentTimeMillis()})" },
                            coordenadas = Coordenadas(
                                latitude = lastLocation?.latitude ?: 0.0,
                                longitude = lastLocation?.longitude ?: 0.0
                            ),
                            precoAlcool = alcoholValue,
                            precoGasolina = gasValue
                        )
                    )

                    val nomeNavegacao = nomeDoPosto.ifBlank { context.getString(R.string.list_map_fallback_station_name) } // Usando um fallback genérico
                    val latNavegacao = lastLocation?.latitude ?: 0.0
                    val lngNavegacao = lastLocation?.longitude ?: 0.0
                    navController.navigate("lista/$nomeNavegacao/$latNavegacao/$lngNavegacao") {
                        popUpTo("mainalcgas") { inclusive = true }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.alc_gas_fab_save_list_stations))
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
                text = stringResource(R.string.alc_gas_station_data_prices),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = nomeDoPosto,
                onValueChange = { nomeDoPosto = it },
                label = { Text(stringResource(R.string.alc_gas_station_name_optional)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Filled.Storefront,
                        contentDescription = stringResource(R.string.station_name)
                    )
                },
                singleLine = true
            )


            OutlinedTextField(
                value = alcool,
                onValueChange = { alcool = it },
                label = { Text(stringResource(R.string.alcohol)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.EvStation, contentDescription = stringResource(R.string.price_alcohol)) },
                trailingIcon = { Text("R$") },
                singleLine = true
            )
            OutlinedTextField(
                value = gasolina,
                onValueChange = { gasolina = it },
                label = { Text(stringResource(R.string.gasoline)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(
                        Icons.Filled.LocalGasStation,
                        contentDescription = stringResource(R.string.price_gasoline)
                    )
                },
                trailingIcon = { Text("R$") },
                singleLine = true
            )


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
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)) {
                        Text(
                            stringResource(R.string.alc_gas_result_label),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            if (checkedState) stringResource(R.string.alc_gas_alcohol_seems_advantageous) else stringResource(R.string.alc_gas_gasoline_seems_advantageous),
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
                                    contentDescription = stringResource(R.string.alc_gas_switch_alcohol_advantageous),
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
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    } else {
                        getLastKnownLocation(context, fusedLocationClient) {
                            lastLocation = it
                        }
                    }

                    val alcoholPrice = alcool.replace(",", ".").toDoubleOrNull()
                    val gasPrice = gasolina.replace(",", ".").toDoubleOrNull()
                    val currentPostoNome = nomeDoPosto.ifEmpty { context.getString(R.string.this_station) }

                    when {
                        alcoholPrice == null || gasPrice == null -> {
                            result = context.getString(R.string.invalid_price_values)
                        }

                        gasPrice == 0.0 -> {
                            result = context.getString(R.string.gasoline_price_cannot_be_zero)
                        }

                        alcoholPrice / gasPrice < 0.7 -> {
                            checkedState = true
                            switchPrefs.saveSwitchState(true)
                            result = context.getString(R.string.alcohol_is_better_option, currentPostoNome)
                        }

                        else -> {
                            checkedState = false
                            switchPrefs.saveSwitchState(false)
                            result = context.getString(R.string.gasoline_is_better_option, currentPostoNome)
                        }
                    }

                    lastLocation?.let {
                        result += context.getString(R.string.alc_gas_coordinates_label,
                            String.format(java.util.Locale.US, "%.4f", it.latitude),
                            String.format(java.util.Locale.US, "%.4f", it.longitude))
                    } ?: run {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            result += context.getString(R.string.alc_gas_getting_location)
                        } else {
                            result += context.getString(R.string.alc_gas_location_not_available)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Filled.Calculate,
                    contentDescription = stringResource(R.string.alc_gas_button_calculate),
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.alc_gas_button_calculate_best_option))
            }

            if (result != stringResource(R.string.fill_fields_to_calculate)) {
                Text(
                    text = result,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .animateContentSize(),
                    textAlign = TextAlign.Center,
                    color = if (result.contains(stringResource(R.string.alcohol), ignoreCase = true) && checkedState) MaterialTheme.colorScheme.primary
                    else if (result.contains(stringResource(R.string.gasoline), ignoreCase = true) && !checkedState) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getLastKnownLocation(
    context: android.content.Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        onLocationReceived(null)
        return
    }

    fusedLocationProviderClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationReceived(location)
            } else {
                val locationRequest =
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
                        .apply {
                            setMinUpdateIntervalMillis(5000L)
                            setMaxUpdates(1)
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