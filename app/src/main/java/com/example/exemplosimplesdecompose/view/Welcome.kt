package com.example.exemplosimplesdecompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R

@Composable
fun Welcome(navController: NavHostController) {
    var selectedLanguage by remember { mutableStateOf("pt") } // pt ou en

    // Textos dinâmicos baseados no idioma selecionado
    val appName = "FuelWise"
    val slogan = if (selectedLanguage == "pt") "Seu assistente inteligente de combustível!" else "Your smart fuel assistant!"
    val imageDescription = if (selectedLanguage == "pt") "Logo do FuelWise" else "FuelWise Logo"
    val selectLanguageText = if (selectedLanguage == "pt") "Selecione o idioma:" else "Select language:"
    val buttonPortuguese = "Português" // Mantido como está, pois "Português" é universalmente entendido no contexto
    val buttonEnglish = "English" // Mantido como está
    val continueButtonText = if (selectedLanguage == "pt") "Começar" else "Get Started"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Utiliza a cor de fundo do tema
            .padding(32.dp), // Aumenta o padding geral
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo do Aplicativo
        Image(
            painter = painterResource(id = R.drawable.gas),
            contentDescription = imageDescription,
            modifier = Modifier.size(160.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Nome do Aplicativo
        Text(
            text = appName,
            style = MaterialTheme.typography.headlineLarge, // Estilo de título maior
            color = MaterialTheme.colorScheme.primary, // Cor primária do tema
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Slogan
        Text(
            text = slogan,
            style = MaterialTheme.typography.titleMedium, // Estilo para o slogan
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Cor para texto secundário
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Seleção de Idioma
        Text(
            text = selectLanguageText,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { selectedLanguage = "pt" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedLanguage == "pt") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedLanguage == "pt") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(text = buttonPortuguese)
            }
            Button(
                onClick = { selectedLanguage = "en" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedLanguage == "en") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedLanguage == "en") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(text = buttonEnglish)
            }
        }
        Spacer(modifier = Modifier.height(48.dp))

        // Botão Continuar
        Button(
            onClick = { navController.navigate("mainalcgas") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // Altura padrão para botões
        ) {
            Text(
                text = continueButtonText,
                style = MaterialTheme.typography.labelLarge // Estilo para texto de botão
            )
        }
    }
}