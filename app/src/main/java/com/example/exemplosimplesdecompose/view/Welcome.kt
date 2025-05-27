package com.example.exemplosimplesdecompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R

@Composable
fun Welcome(navController: NavHostController) {
    var selectedLanguage by remember { mutableStateOf("pt") } // pt ou en

    val welcomeText = if (selectedLanguage == "pt") "Bem-vindos ao Navigation Example!" else "Welcome to the Navigation Example!"
    val imageDescription = if (selectedLanguage == "pt") "Imagem de boas-vindas" else "Welcome image"
    val selectLanguageText = if (selectedLanguage == "pt") "Selecione o idioma:" else "Select language:"
    val buttonPortuguese = if (selectedLanguage == "pt") "Português" else "Portuguese"
    val buttonEnglish = if (selectedLanguage == "pt") "Inglês" else "English"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = welcomeText)
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.welcome),
            contentDescription = imageDescription,
            modifier = Modifier
                .size(128.dp)
                .clickable { navController.navigate("mainalcgas") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = selectLanguageText)
        Spacer(modifier = Modifier.height(8.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { selectedLanguage = "pt" }) {
                Text(text = buttonPortuguese)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { selectedLanguage = "en" }) {
                Text(text = buttonEnglish)
            }
        }
    }
}

