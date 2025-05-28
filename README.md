# FuelWise ⛽️📱

**Seu assistente inteligente de combustível!** 💡 (Your smart fuel assistant!)

## Visão Geral 🗺️

FuelWise é um aplicativo Android 🤖 desenvolvido para ajudar os usuários a tomar decisões mais inteligentes sobre qual combustível utilizar (álcool ou gasolina) com base nos preços atuais. Além disso, o aplicativo permite que os usuários salvem informações sobre postos de gasolina, incluindo seus preços e localização, para referência futura. 💾

O projeto é construído utilizando Kotlin e Jetpack Compose, seguindo as modernas práticas de desenvolvimento Android. 🚀

## Funcionalidades ✨

* **Cálculo de Custo-Benefício  calculadora:** Insira os preços do álcool e da gasolina para determinar qual combustível é mais vantajoso (considerando a regra comum de que o álcool é vantajoso se custar menos de 70% do preço da gasolina).
* **Salvar Postos de Gasolina ⛽️:**
    * Armazene o nome do posto (opcional), preços do álcool e da gasolina.
    * Salve as coordenadas geográficas (latitude e longitude) do posto 📍, obtidas automaticamente se a permissão de localização for concedida.
* **Listagem de Postos Salvos 📋:** Visualize uma lista de todos os postos de gasolina que você salvou.
* **Edição de Dados ✏️:** Modifique as informações de um posto salvo (nome, preços).
* **Exclusão de Postos 🗑️:** Remova postos da sua lista salva.
* **Integração com Mapas 🗺️➡️📱:** Abra a localização de um posto salvo diretamente em um aplicativo de mapas (como o Google Maps).
* **Preferência de Cálculo 📊:** Um switch permite ao usuário indicar manually se o álcool ou a gasolina é mais vantajoso, e essa preferência pode ser salva.
* **Interface Intuitiva 👋:** Tela de boas-vindas e navegação clara entre as funcionalidades.

## Tecnologias Utilizadas 🛠️

* **Linguagem de Programação:** Kotlin 𝕂
* **UI Toolkit:** Jetpack Compose 🖼️
* **Design:** Material Design 3 🎨
* **Navegação:** Android Navigation Component para Compose 🧭
* **Localização:** Google Play Services Location API 🌍
* **Persistência de Dados:** SharedPreferences (gerenciada pelas classes `PostoPrefs` e `SwitchPreferences`) 🗄️.
* **Serialização:** Gson para serializar/desserializar objetos para SharedPreferences 🔄.

## Estrutura do Projeto 📁 (Simplificada)

O código-fonte principal do aplicativo está localizado em `app/src/main/java/com/example/exemplosimplesdecompose/`:

* `MainActivity.kt`: Ponto de entrada do aplicativo, hospeda o `NavHost` para navegação entre as telas 🚪.
* `view/`: Contém todas as telas (Composables) da interface do usuário:
    * `Welcome.kt`: Tela inicial de boas-vindas 👋.
    * `AlcoolouGasolina.kt`: Tela para cálculo e adição de novos postos ➕⛽️.
    * `ListaDePostos.kt`: Tela para exibir os postos salvos 📜.
    * `EditarPostoScreen.kt`: Tela para editar informações de um posto existente ✍️.
* `data/`: Contém as classes de modelo de dados e a lógica de persistência:
    * `Posto.kt`: Classe de dados que representa um posto de gasolina ⛽️📄.
    * `Coordenadas.kt`: Classe de dados para latitude e longitude 🗺️📍.
    * `PostoPrefs.kt`: Gerencia o salvamento e recuperação de dados dos postos usando SharedPreferences e Gson 💾⚙️.
* `Utils/`: Classes utilitárias:
    * `LocationUtils.kt`: Funções para obter a localização atual do dispositivo 🛰️.
    * `SwitchPreferences.kt`: Gerencia o estado de uma preferência de switch usando SharedPreferences ⚙️👍.
* `ui/theme/`: Definições de tema, cores e tipografia para o Jetpack Compose 🖌️.
* `app/src/main/res/`: Recursos do Android como drawables 🖼️, strings 📝 (o nome do app aqui está como "ExemploSimplesDeCompose", mas "FuelWise" é usado na tela de boas-vindas), e temas XML.

## Configuração e Instalação ⚙️🚀

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/FuelWise.git](https://github.com/seu-usuario/FuelWise.git)
    ```
    *(Substitua `seu-usuario/FuelWise.git` pelo URL real do repositório se aplicável)*
2.  **Abra no Android Studio:**
    * Inicie o Android Studio 👨‍💻.
    * Selecione "Open an Existing Project".
    * Navegue até o diretório onde você clonou o repositório e selecione-o.
3.  **Construa o Projeto 🏗️:**
    * O Android Studio deve sincronizar o Gradle automaticamente. Se não, clique em "Sync Project with Gradle Files" 🔄.
    * Clique em "Build" > "Make Project" ou use o atalho (Ctrl+F9 ou Cmd+F9).
4.  **Execute o Aplicativo ▶️:**
    * Selecione um emulador ou conecte um dispositivo Android 📱.
    * Clique em "Run" > "Run 'app'" ou use o atalho (Shift+F10).
5.  **Permissões ✅:**
    * O aplicativo solicitará permissão de localização (`ACCESS_FINE_LOCATION` e `ACCESS_COARSE_LOCATION` declaradas no `AndroidManifest.xml`) para salvar as coordenadas dos postos. Certifique-se de conceder essa permissão para a funcionalidade completa.

---
