package com.cyberwolvesrestorers.englishgame

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.cyberwolvesrestorers.englishgame.data.AuthViewModel
import com.cyberwolvesrestorers.englishgame.data.ProfileViewModel
import com.cyberwolvesrestorers.englishgame.model.ProfileData
import com.cyberwolvesrestorers.englishgame.ui.theme.EnglishGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnglishGameTheme {
                MyApp(applicationContext)
            }
        }
    }
}
// логин: kosty@kosty.ru
// пароль:

@Composable
fun MyApp(context: Context) {
    val navController = rememberNavController()
    val preferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val storedToken = preferences.getString("token", null)
    val isLoggedIn = remember { mutableStateOf(storedToken != null) }

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { paddingValues ->
        NavHost(navController, startDestination = "home", Modifier.padding(paddingValues)) {
            composable("home") { HomeScreen() }
            composable("profile") {
                ProfileScreen(isLoggedIn.value, onLoginStatusChange = {
                    isLoggedIn.value = it
                    if (!it) {
                        preferences.edit().clear().apply()
                    }
                }, preferences)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation {
        BottomNavigationItem(
            selected = false,
            onClick = {
                navController.navigate("home")
            },
            label = { Text("Главная") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Главная") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {
                navController.navigate("profile")
            },
            label = { Text("Профиль") },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Профиль") }
        )
    }
}

@Composable
fun HomeScreen() {
    // Просто пример содержимого главной страницы
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text("Главная страница")
    }
}

@Composable
fun ProfileScreen(isLoggedIn: Boolean, onLoginStatusChange: (Boolean) -> Unit, preferences: SharedPreferences, profileViewModel: ProfileViewModel = viewModel()) {
    var showLogin by remember { mutableStateOf(false) }
    var showRegister by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)) {
        Text("Профиль")
        when {
            isLoggedIn -> {
                var profileData by remember { mutableStateOf<ProfileData?>(null) }
                val token = preferences.getString("token", null)

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn && token != null) {
                        profileViewModel.fetchProfile(token).observeForever { data ->
                            profileData = data
                        }
                    }
                }

                Column(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)) {
                    Text("Профиль")
                    when {
                        isLoggedIn -> {
                            profileData?.let {
                                Text("Email: ${it.email}")
                                Text("Имя: ${it.username}")
                                Text("Очки: ${it.points}")
                                Text("Стрик: ${it.streak}")
                            } ?: Text("Загрузка профиля...")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                onLoginStatusChange(false)
                                preferences.edit().clear().apply()
                            }) {
                                Text("Выйти")
                            }
                        }
                        else -> {
                            Text("Вы не вошли в аккаунт")
                        }
                    }
                }
            }
            showLogin -> {
                LoginScreen(preferences, onSuccess = {
                    onLoginStatusChange(true)
                    showLogin = false
                })
            }
            showRegister -> {
                RegisterScreen(preferences, onSuccess = {
                    onLoginStatusChange(true)
                    showRegister = false
                })
            }
            else -> {
                Button(onClick = { showLogin = true }) { Text("Войти") }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { showRegister = true }) { Text("Зарегистрироваться") }
            }
        }
    }
}

@Composable
fun LoginScreen(preferences: SharedPreferences, onSuccess: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.Center) {
        TextField(value = email, onValueChange = { email = it },
            label = { Text("Почта") },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            isLoading = true
            authViewModel.login(email, password).observeForever { tokenDto ->
                isLoading = false
                if (tokenDto != null) {
                    preferences.edit()
                        .putString("token", tokenDto.token)
                        .putString("expires", tokenDto.expires)
                        .apply()
                    onSuccess()
                } else {
                    errorMessage = "Ошибка входа"
                }
            }
        }, enabled = !isLoading) {
            if (isLoading) CircularProgressIndicator() else Text("Войти")
        }
        if (errorMessage != null) Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun RegisterScreen(preferences: SharedPreferences, onSuccess: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.Center) {
        TextField(value = email,
            onValueChange = { email = it },
            label = { Text("Почта") },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        TextField(value = username,
            onValueChange = { username = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        TextField(value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            isLoading = true
            authViewModel.register(email, username, password).observeForever { tokenDto ->
                isLoading = false
                if (tokenDto != null) {
                    preferences.edit()
                        .putString("token", tokenDto.token)
                        .putString("expires", tokenDto.expires)
                        .apply()
                    onSuccess()
                } else {
                    errorMessage = "Ошибка регистрации"
                }
            }
        }, enabled = !isLoading) {
            if (isLoading) CircularProgressIndicator() else Text("Зарегистрироваться")
        }
        if (errorMessage != null) Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
    }
}
