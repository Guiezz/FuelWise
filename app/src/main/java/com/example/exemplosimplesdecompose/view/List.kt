package com.example.exemplosimplesdecompose.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.Posto
import com.example.exemplosimplesdecompose.data.PostoPrefs
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDePostos(
    navController: NavHostController,
    @Suppress("UNUSED_PARAMETER") nomeNovoPosto: String,
    @Suppress("UNUSED_PARAMETER") latNovoPosto: Double,
    @Suppress("UNUSED_PARAMETER") lngNovoPosto: Double
) {
    val context = LocalContext.current
    val postoPrefs = remember { PostoPrefs(context) }
    var postos by remember { mutableStateOf(emptyList<Posto>()) }


    LaunchedEffect(key1 = postoPrefs) {
        postos = postoPrefs.getPostos()
    }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.list_my_saved_stations)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("mainalcgas") },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.list_add_new_station))
            }
        }
    ) { innerPadding ->
        if (postos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.WarningAmber,
                        contentDescription = stringResource(id = R.string.list_empty_icon_description),
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.list_empty_message),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.list_empty_add_instruction),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(postos, key = { _, posto -> posto.nome.toString() + (posto.coordenadas?.latitude ?: 0.0) }) { index, posto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalGasStation,
                                contentDescription = stringResource(id = R.string.list_station_icon_description),
                                modifier = Modifier.size(40.dp).padding(end = 16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = posto.nome ?: stringResource(id = R.string.unnamed_station),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    posto.precoAlcool?.let {
                                        Text(
                                            text = stringResource(id = R.string.list_alcohol_price_prefix) + currencyFormatter.format(it),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                    posto.precoGasolina?.let {
                                        Text(
                                            text = stringResource(id = R.string.list_gasoline_price_prefix) + currencyFormatter.format(it),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    navController.navigate("editar/$index")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.list_edit_station), tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = {
                                    postoPrefs.deletarPosto(index)
                                    postos = postoPrefs.getPostos()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.list_delete_station), tint = MaterialTheme.colorScheme.error)
                                }
                                if (posto.coordenadas != null && (posto.coordenadas.latitude != 0.0 || posto.coordenadas.longitude != 0.0)) {
                                    IconButton(onClick = {
                                        val mapLabel = posto.nome ?: context.getString(R.string.list_map_fallback_station_name)
                                        val gmmIntentUri =
                                            "geo:0,0?q=${posto.coordenadas.latitude},${posto.coordenadas.longitude}(${
                                                Uri.encode(mapLabel)
                                            })".toUri()

                                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                            setPackage("com.google.android.apps.maps")
                                        }
                                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(mapIntent)
                                        } else {
                                            val webIntentUri = "https://www.google.com/maps/search/?api=1&query=${posto.coordenadas.latitude},${posto.coordenadas.longitude}".toUri()
                                            val webIntent = Intent(Intent.ACTION_VIEW, webIntentUri)
                                            context.startActivity(webIntent)
                                        }
                                    }) {
                                        Icon(Icons.Filled.Map, contentDescription = stringResource(id = R.string.list_open_in_map), tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}