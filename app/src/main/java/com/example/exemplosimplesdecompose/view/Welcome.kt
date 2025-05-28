package com.example.exemplosimplesdecompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    var selectedLanguage by remember { mutableStateOf("pt") }

    val appName = "FuelWise"
    val slogan = if (selectedLanguage == "pt") "Seu assistente inteligente de combustível!" else "Your smart fuel assistant!"
    val imageDescription = if (selectedLanguage == "pt") "Logo do FuelWise" else "FuelWise Logo"
    val selectLanguageText = if (selectedLanguage == "pt") "Selecione o idioma:" else "Select language:"
    val buttonPortuguese = "Português"
    val buttonEnglish = "English"
    val continueButtonText = if (selectedLanguage == "pt") "Começar a Calcular" else "Get Started" // Texto do botão principal ajustado
    val viewSavedPostsButtonText = if (selectedLanguage == "pt") "Ver Postos Salvos" else "View Saved Stations"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.gas),
            contentDescription = imageDescription,
            modifier = Modifier.size(160.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = appName,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = slogan,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("mainalcgas") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = continueButtonText,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("lista/todos/0.0/0.0")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                Icons.Filled.List,
                contentDescription = viewSavedPostsButtonText,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = viewSavedPostsButtonText,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}