// com.example.exemplosimplesdecompose.view.EditarPostoScreen
package com.example.exemplosimplesdecompose.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate // Ícone para Recalcular
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EvStation // Ícone para Álcool
import androidx.compose.material.icons.filled.LocalGasStation // Ícone para Gasolina
import androidx.compose.material.icons.filled.Save // Ícone para Salvar
import androidx.compose.material.icons.filled.Storefront // Ícone para Nome do Posto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.Utils.SwitchPreferences
import com.example.exemplosimplesdecompose.data.Posto
import com.example.exemplosimplesdecompose.data.PostoPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPostoScreen(index: Int, navController: NavHostController) {
    val context = LocalContext.current
    val postoPrefs = remember { PostoPrefs(context) }
    val switchPrefs = remember { SwitchPreferences(context) }

    // Tenta carregar o posto. Se não encontrar, exibe mensagem e retorna.
    val postoOriginal = postoPrefs.getPostos().getOrNull(index)
    if (postoOriginal == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Erro") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Posto não encontrado ou índice inválido.")
            }
        }
        return
    }

    var nome by remember { mutableStateOf(postoOriginal.nome ?: "") }
    var precoAlcool by remember { mutableStateOf(postoOriginal.precoAlcool?.toString()?.replace('.', ',') ?: "") }
    var precoGasolina by remember { mutableStateOf(postoOriginal.precoGasolina?.toString()?.replace('.', ',') ?: "") }

    // O estado do switch é carregado das preferências, mas pode ser atualizado pelo cálculo
    var checkedState by remember { mutableStateOf(switchPrefs.getSwitchState()) }
    var resultadoCalculo by remember { mutableStateOf("Preencha os valores para calcular.") }

    // Atualiza o resultado se já houver preços no posto original
    LaunchedEffect(postoOriginal) {
        val alcohol = postoOriginal.precoAlcool
        val gas = postoOriginal.precoGasolina
        val currentPostoNome = nome.ifEmpty { "Este posto" }

        if (alcohol != null && gas != null) {
            when {
                gas == 0.0 -> resultadoCalculo = "Preço da gasolina inválido."
                alcohol / gas < 0.7 -> {
                    checkedState = true
                    // switchPrefs.saveSwitchState(true) // Não salvar aqui, apenas refletir
                    resultadoCalculo = "$currentPostoNome: Álcool é a melhor opção!"
                }
                else -> {
                    checkedState = false
                    // switchPrefs.saveSwitchState(false) // Não salvar aqui, apenas refletir
                    resultadoCalculo = "$currentPostoNome: Gasolina é a melhor opção!"
                }
            }
        } else {
            resultadoCalculo = "Preencha os preços para calcular."
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
                text = "Modifique os dados do posto:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do Posto") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Storefront, contentDescription = "Nome do Posto") },
                singleLine = true
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = precoAlcool,
                    onValueChange = { precoAlcool = it },
                    label = { Text("Álcool") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Filled.EvStation, contentDescription = "Preço Álcool") },
                    trailingIcon = { Text("R$") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = precoGasolina,
                    onValueChange = { precoGasolina = it },
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
                            // A preferência do switch pode ser salva, mas o cálculo a sobrescreverá.
                            // A principal função do switch aqui é refletir o cálculo ou permitir uma substituição manual.
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
                    val alcohol = precoAlcool.replace(",", ".").toDoubleOrNull()
                    val gas = precoGasolina.replace(",", ".").toDoubleOrNull()
                    val currentPostoNome = nome.ifEmpty { "Este posto" }

                    when {
                        alcohol == null || gas == null -> {
                            resultadoCalculo = "Valores de preço inválidos."
                        }
                        gas == 0.0 -> {
                            resultadoCalculo = "Preço da gasolina não pode ser zero."
                        }
                        alcohol / gas < 0.7 -> {
                            checkedState = true // Atualiza o switch
                            // switchPrefs.saveSwitchState(true) // Salva o estado se o switch for manipulado manualmente
                            resultadoCalculo = "$currentPostoNome: Álcool é a melhor opção!"
                        }
                        else -> {
                            checkedState = false // Atualiza o switch
                            // switchPrefs.saveSwitchState(false) // Salva o estado se o switch for manipulado manualmente
                            resultadoCalculo = "$currentPostoNome: Gasolina é a melhor opção!"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Filled.Calculate, contentDescription = "Recalcular", modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Recalcular Melhor Opção")
            }

            if (resultadoCalculo != "Preencha os valores para calcular.") {
                Text(
                    text = resultadoCalculo,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .animateContentSize(),
                    textAlign = TextAlign.Center,
                    color = if (resultadoCalculo.contains("Álcool", ignoreCase = true) && checkedState) MaterialTheme.colorScheme.primary
                    else if (resultadoCalculo.contains("Gasolina", ignoreCase = true) && !checkedState) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Empurra o botão Salvar para baixo se houver espaço

            Button(
                onClick = {
                    val novoPrecoAlcoolDouble = precoAlcool.replace(",", ".").toDoubleOrNull()
                    val novoPrecoGasolinaDouble = precoGasolina.replace(",", ".").toDoubleOrNull()

                    // Cria um novo objeto Posto com os valores atualizados
                    // As coordenadas originais são mantidas
                    val postoAtualizado = postoOriginal.copy(
                        nome = nome,
                        precoAlcool = novoPrecoAlcoolDouble,
                        precoGasolina = novoPrecoGasolinaDouble
                        // As coordenadas não são editadas nesta tela, então mantemos as originais.
                    )
                    postoPrefs.atualizarPosto(index, postoAtualizado)
                    navController.popBackStack() // Volta para a tela anterior (ListaDePostos)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Save, contentDescription = "Salvar", modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Salvar Alterações")
            }
        }
    }
}