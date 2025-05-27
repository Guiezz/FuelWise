// com.example.exemplosimplesdecompose.view.EditarPostoScreen
package com.example.exemplosimplesdecompose.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.Utils.SwitchPreferences // Certifique-se de que este import está correto
import com.example.exemplosimplesdecompose.data.Posto
import com.example.exemplosimplesdecompose.data.PostoPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPostoScreen(index: Int, navController: NavHostController) {
    val context = LocalContext.current
    val postoPrefs = remember { PostoPrefs(context) }
    val switchPrefs = remember { SwitchPreferences(context) } // Instancia o SwitchPreferences

    var posto by remember { mutableStateOf(postoPrefs.getPostos().getOrNull(index)) }

    if (posto == null) {
        Text("Posto não encontrado")
        return
    }

    var nome by remember { mutableStateOf(posto!!.nome ?: "") }
    // Adiciona estados para os preços
    var precoAlcool by remember { mutableStateOf(posto!!.precoAlcool?.toString() ?: "") }
    var precoGasolina by remember { mutableStateOf(posto!!.precoGasolina?.toString() ?: "") }

    var checkedState by remember { mutableStateOf(switchPrefs.getSwitchState()) } // Estado do switch
    var resultadoCalculo by remember { mutableStateOf("Preencha os valores para calcular") } // Resultado do cálculo

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Posto") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Adiciona alinhamento central
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do Posto") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campos para preço do álcool e gasolina
            OutlinedTextField(
                value = precoAlcool,
                onValueChange = { precoAlcool = it },
                label = { Text("Preço do Álcool (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = precoGasolina,
                onValueChange = { precoGasolina = it },
                label = { Text("Preço da Gasolina (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Switch e lógica de cálculo replicada da AlcoolGasolinaPreco
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Ajuste o padding
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

            // Botão para recalcular
            Button(
                onClick = {
                    val alcohol = precoAlcool.replace(",", ".").toDoubleOrNull()
                    val gas = precoGasolina.replace(",", ".").toDoubleOrNull()

                    when {
                        alcohol == null || gas == null -> {
                            resultadoCalculo = "Digite valores válidos"
                        }
                        gas == 0.0 -> {
                            resultadoCalculo = "Preço da gasolina inválido"
                        }
                        alcohol / gas < 0.7 -> {
                            checkedState = true
                            switchPrefs.saveSwitchState(true)
                            resultadoCalculo = "${nome.ifEmpty { "O posto" }}: Melhor Álcool!"
                        }
                        else -> {
                            checkedState = false
                            switchPrefs.saveSwitchState(false)
                            resultadoCalculo = "${nome.ifEmpty { "O posto" }}: Melhor Gasolina!"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recalcular Melhor Opção")
            }

            Text(
                text = resultadoCalculo,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .animateContentSize()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Salvar
            Button(
                onClick = {
                    val novoPrecoAlcool = precoAlcool.replace(",", ".").toDoubleOrNull()
                    val novoPrecoGasolina = precoGasolina.replace(",", ".").toDoubleOrNull()

                    // Cria um novo objeto Posto com os valores atualizados
                    val postoAtualizado = posto!!.copy(
                        nome = nome,
                        precoAlcool = novoPrecoAlcool,
                        precoGasolina = novoPrecoGasolina
                    )
                    postoPrefs.atualizarPosto(index, postoAtualizado)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Alterações")
            }
        }
    }
}