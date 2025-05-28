package com.example.exemplosimplesdecompose.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.Utils.SwitchPreferences
import com.example.exemplosimplesdecompose.data.PostoPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPostoScreen(index: Int, navController: NavHostController) {
    val context = LocalContext.current
    val postoPrefs = remember { PostoPrefs(context) }
    val switchPrefs = remember { SwitchPreferences(context) }

    val postoOriginal = postoPrefs.getPostos().getOrNull(index)
    if (postoOriginal == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.error)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.edit_station_not_found))
            }
        }
        return
    }

    var nome by remember { mutableStateOf(postoOriginal.nome ?: "") }
    var precoAlcool by remember { mutableStateOf(postoOriginal.precoAlcool?.toString()?.replace('.', ',') ?: "") }
    var precoGasolina by remember { mutableStateOf(postoOriginal.precoGasolina?.toString()?.replace('.', ',') ?: "") }

    var checkedState by remember { mutableStateOf(switchPrefs.getSwitchState()) }
    var resultadoCalculo by remember { mutableStateOf(context.getString(R.string.edit_station_fill_prices_to_calculate)) }


    LaunchedEffect(postoOriginal) {
        val alcohol = postoOriginal.precoAlcool
        val gas = postoOriginal.precoGasolina
        val currentPostoNome = nome.ifEmpty { context.getString(R.string.this_station) }

        if (alcohol != null && gas != null) {
            when {
                gas == 0.0 -> resultadoCalculo = context.getString(R.string.gasoline_price_cannot_be_zero)
                alcohol / gas < 0.7 -> {
                    checkedState = true
                    resultadoCalculo = context.getString(R.string.alcohol_is_better_option, currentPostoNome)
                }
                else -> {
                    checkedState = false
                    resultadoCalculo = context.getString(R.string.gasoline_is_better_option, currentPostoNome)
                }
            }
        } else {
            resultadoCalculo = context.getString(R.string.edit_station_fill_prices_to_calculate)
        }
    }


    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.edit_station_modify_data),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text(stringResource(R.string.station_name)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Storefront, contentDescription = stringResource(R.string.station_name)) },
                singleLine = true
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = precoAlcool,
                    onValueChange = { precoAlcool = it },
                    label = { Text(stringResource(R.string.alcohol)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Filled.EvStation, contentDescription = stringResource(R.string.price_alcohol)) },
                    trailingIcon = { Text("R$") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = precoGasolina,
                    onValueChange = { precoGasolina = it },
                    label = { Text(stringResource(R.string.gasoline)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Filled.LocalGasStation, contentDescription = stringResource(R.string.price_gasoline)) },
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
                    val alcohol = precoAlcool.replace(",", ".").toDoubleOrNull()
                    val gas = precoGasolina.replace(",", ".").toDoubleOrNull()
                    val currentPostoNome = nome.ifEmpty { context.getString(R.string.this_station) }

                    when {
                        alcohol == null || gas == null -> {
                            resultadoCalculo = context.getString(R.string.invalid_price_values)
                        }
                        gas == 0.0 -> {
                            resultadoCalculo = context.getString(R.string.gasoline_price_cannot_be_zero) // Alterado
                        }
                        alcohol / gas < 0.7 -> {
                            checkedState = true
                            resultadoCalculo = context.getString(R.string.alcohol_is_better_option, currentPostoNome)
                        }
                        else -> {
                            checkedState = false
                            resultadoCalculo = context.getString(R.string.gasoline_is_better_option, currentPostoNome)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Filled.Calculate, contentDescription = stringResource(R.string.edit_station_button_recalculate), modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.edit_station_button_recalculate_best_option))
            }

            if (resultadoCalculo != stringResource(R.string.edit_station_fill_prices_to_calculate)) {
                Text(
                    text = resultadoCalculo,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .animateContentSize(),
                    textAlign = TextAlign.Center,
                    color = if (resultadoCalculo.contains(stringResource(R.string.alcohol), ignoreCase = true) && checkedState) MaterialTheme.colorScheme.primary
                    else if (resultadoCalculo.contains(stringResource(R.string.gasoline), ignoreCase = true) && !checkedState) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val novoPrecoAlcoolDouble = precoAlcool.replace(",", ".").toDoubleOrNull()
                    val novoPrecoGasolinaDouble = precoGasolina.replace(",", ".").toDoubleOrNull()

                    val postoAtualizado = postoOriginal.copy(
                        nome = nome,
                        precoAlcool = novoPrecoAlcoolDouble,
                        precoGasolina = novoPrecoGasolinaDouble
                    )
                    postoPrefs.atualizarPosto(index, postoAtualizado)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save), modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.edit_station_button_save_changes))
            }
        }
    }
}