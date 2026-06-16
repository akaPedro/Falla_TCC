# 🗣️ Falla — Comunicação Aumentativa e Alternativa

<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_foreground.png" width="120" alt="Logo Falla"/>
</p>

<p align="center">
  Aplicativo Android de CAA desenvolvido como Trabalho de Conclusão de Curso (TCC),<br>
  voltado para pessoas não verbais, com foco em autistas não verbais.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Database-Room-003B57?style=for-the-badge&logo=sqlite&logoColor=white"/>
  <img src="https://img.shields.io/badge/TCC-2026-2D4A43?style=for-the-badge"/>
</p>

---

## 📖 Sobre o projeto

O **Falla** é um aplicativo de **Comunicação Aumentativa e Alternativa (CAA)** desenvolvido para auxiliar pessoas não verbais — especialmente crianças e jovens no espectro autista — a se expressar com autonomia e dignidade.

O app utiliza o **Android Text-to-Speech (TTS)** para "falar" pelo usuário ao tocar em pictogramas organizados em gavetas por categoria, com subcategorias e cards personalizáveis.

Desenvolvido como TCC do curso de **Ciências da computação** na **Universidade Estadual do Piaui**.

---

## ✨ Funcionalidades

### Comunicação
- 🟢 **Cards Sim/Não** fixos no topo para respostas rápidas e imediatas
- 🗂️ **Gavetas por categoria** — Pessoal, Alimentos, Lazer, Aprendizado
- 📂 **Subgavetas temáticas** dentro de cada categoria
- ⭐ **Cards coringas** no topo de cada gaveta para comunicações urgentes
- ⌨️ **Digitação livre** com TTS para expressões não previstas nos cards
- ❤️ **Favoritos** para acesso rápido aos cards mais usados

### Personalização
- 🎨 **Cores por categoria** configuráveis com paleta visual
- 📏 **Tamanho dos cards** em 2 ou 3 colunas
- 🖼️ **Imagens personalizadas** via galeria ou banco de assets
- 👤 **Perfil do usuário** com nome e foto
- 🌙 **Modo escuro** com paleta adequada para autistas

### Cuidador
- 📋 **Histórico de comunicação** com data, hora, imagem e texto
- ➕ **Criação de cards** com seleção de categoria e subcategoria
- ✏️ **Edição e exclusão** de cards existentes

### Acessibilidade (WCAG 2.1)
- 🔊 **TalkBack** com `contentDescription` dinâmico em todos os cards
- 🕹️ **Switch Access** com `focusable` e `importantForAccessibility`
- 🎯 **Alvos de toque** dimensionados para dificuldades motoras finas
- 🌈 **Contraste** mínimo de 4,5:1 em todos os textos principais
- 🇧🇷 **TTS em Português Brasil** nativo

---

## 🏗️ Arquitetura

```
app/
├── activities/
│   ├── MainActivity.java         # Tela principal com gavetas e cards
│   ├── FallaActivity.java        # Digitação livre com TTS
│   ├── PerfilActivity.java       # Configuração do perfil
│   ├── HistoricoActivity.java    # Histórico de comunicação
│   ├── OnboardingActivity.java   # Tutorial de primeiro uso
│   ├── SplashActivity.java       # Tela de carregamento
│   └── SobreActivity.java        # Informações do app
├── card/
│   ├── ItemCard.java             # Entidade do card (Room)
│   ├── ItemHistorico.java        # Entidade do histórico (Room)
│   ├── CategoriaItem.java        # Enum de categorias e subcategorias
│   ├── ItemCardDao.java          # DAO dos cards
│   └── AssetImageHelper.java     # Helper para carregar imagens dos assets
├── DAO/
│   └── AppDatabase.java          # Banco de dados Room
├── historico/
│   └── HistoricoDao.java         # DAO do histórico
└── usuario/
    ├── Usuario.java              # Entidade do usuário (Room)
    └── UsuarioDao.java           # DAO do usuário
```

---

## 🗃️ Banco de Dados

O app utiliza **Room (SQLite)** com três entidades:

- **`ItemCard`** — cards com texto, categoria, imagem e flag de favorito
- **`ItemHistorico`** — registro de cada comunicação com data, hora e imagem
- **`Usuario`** — perfil único com nome, gênero e foto

---

## 🖼️ Assets e Pictogramas

As imagens dos cards são armazenadas em `app/src/main/assets/` organizadas por categoria:

```
assets/
├── pessoal/       # Emoções, ações, referência, saúde, cuidados, roupas
├── alimentos/     # Refeições, café, bebidas, doces
├── lazer/         # Jogos, telas, ao ar livre, social
└── aprendizado/   # Números, alfabeto, vogais, cores, formas
```

Os pictogramas utilizam imagens do **ARASAAC** — Centro Aragonês de Comunicação Aumentativa e Alternativa, sob licença Creative Commons BY-NC-SA.

---

## 🚀 Como executar

### Pré-requisitos
- Android Studio Hedgehog ou superior
- SDK Android mínimo: API 24 (Android 7.0)
- SDK Android alvo: API 34 (Android 14)

### Passos

```bash
# Clone o repositório
git clone https://github.com/akaPedro/Falla_TCC.git

# Abra no Android Studio
# File → Open → selecione a pasta Falla_TCC

# Sincronize o Gradle
# File → Sync Project with Gradle Files

# Execute no dispositivo ou emulador
# Run → Run 'app'
```

### Dependências principais

```gradle
implementation 'androidx.room:room-runtime:2.6.1'
implementation 'androidx.viewpager2:viewpager2:1.0.0'
implementation 'androidx.drawerlayout:drawerlayout:1.2.0'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.cardview:cardview:1.0.0'
```

---

## ♿ Acessibilidade

O Falla foi desenvolvido com foco em conformidade com a **WCAG 2.1**:

| Critério | Nível | Status |
|---|---|---|
| 1.1.1 Conteúdo não textual | A | ✅ |
| 1.4.3 Contraste mínimo | AA | ✅ |
| 1.4.4 Redimensionamento de texto | AA | ✅ |
| 2.1.1 Acessibilidade do teclado | A | ✅ |
| 2.4.3 Ordem de foco | A | ✅ |
| 2.5.5 Tamanho do alvo | AAA | ⚠️ Parcial |
| 3.1.1 Idioma da página | A | ✅ |
| 3.2.4 Identificação consistente | AA | ✅ |

---

## 📚 Referências

- [ARASAAC — Pictogramas](https://arasaac.org)
- [WCAG 2.1 — W3C](https://www.w3.org/TR/WCAG21/)
- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Android Text-to-Speech](https://developer.android.com/reference/android/speech/tts/TextToSpeech)

---

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos como Trabalho de Conclusão de Curso.

---

<p align="center">
  Desenvolvido com 💚 como TCC — 2026
</p>
