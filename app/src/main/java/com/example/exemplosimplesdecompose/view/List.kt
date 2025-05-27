package com.example.exemplosimplesdecompose.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.data.PostoPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDePostos(
    navController: NavHostController,
    nome: String,
    toDouble: Double,
    toDouble1: Double
) {
    val context = LocalContext.current
    val postoPrefs = remember { PostoPrefs(context) }
    var postos by remember { mutableStateOf(postoPrefs.getPostos()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lista de Postos") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(postos) { index, posto ->
                Card(
                    onClick = {
                        val gmmIntentUri =
                            "geo:0,0?q=${posto.coordenadas?.latitude},${posto.coordenadas?.longitude}(${
                                Uri.encode(posto.nome)
                            })".toUri()

                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = posto.nome.toString())

                        Row {
                            IconButton(onClick = {
                                // Navega para tela de edição passando o índice
                                navController.navigate("editar/$index")
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }

                            IconButton(onClick = {
                                postoPrefs.deletarPosto(index)
                                postos = postoPrefs.getPostos()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir")
                            }
                        }
                    }
                }
            }
        }
    }
}
