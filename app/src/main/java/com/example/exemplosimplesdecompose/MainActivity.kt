package com.example.exemplosimplesdecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.exemplosimplesdecompose.ui.theme.ExemploSimplesDeComposeTheme
import com.example.exemplosimplesdecompose.view.AlcoolGasolinaPreco
import com.example.exemplosimplesdecompose.view.InputView
import com.example.exemplosimplesdecompose.view.ListaDePostos
import com.example.exemplosimplesdecompose.view.Welcome
import com.example.exemplosimplesdecompose.view.EditarPostoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExemploSimplesDeComposeTheme {
                val navController: NavHostController = rememberNavController()
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") { Welcome(navController) }
                    composable("input") { InputView(navController) }
                    composable("mainalcgas") { AlcoolGasolinaPreco(navController) }
                    composable(
                        "lista/{nome}/{lat}/{lng}",
                        arguments = listOf(
                            navArgument("nome") { type = NavType.StringType },
                            navArgument("lat") { type = NavType.StringType },
                            navArgument("lng") { type = NavType.StringType }
                        )
                    ) {
                        val nome = it.arguments?.getString("nome") ?: "Posto"
                        val lat = it.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
                        val lng = it.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0
                        ListaDePostos(navController, nome, lat, lng)
                    }

                    composable("editar/{index}") { backStackEntry ->
                        val index = backStackEntry.arguments?.getString("index")?.toIntOrNull()
                        if (index != null) {
                            EditarPostoScreen(index, navController)
                        } else {
                            Text("Index inválido")
                        }
                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExemploSimplesDeComposeTheme {
        Greeting("Android")
    }
}