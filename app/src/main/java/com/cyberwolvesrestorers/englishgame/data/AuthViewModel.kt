package com.cyberwolvesrestorers.englishgame.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.cyberwolvesrestorers.englishgame.model.LoginRequest
import com.cyberwolvesrestorers.englishgame.model.RegisterRequest
import com.cyberwolvesrestorers.englishgame.service.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthViewModel : ViewModel() {
    private val authService: AuthService = Retrofit.Builder()
        .baseUrl("http://eng.quassbot.ru/v1/auth/")                    // 10.0.2.2 - localhost
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthService::class.java)

    fun login(email: String, password: String) = liveData {
        try {
            val response = authService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                emit(null) // Ошибка аутентификации
            }
        } catch (e: Exception) {
            emit(null) // Ошибка сети или других проблем
            Log.e("RegisterError", "Ошибка сети: ${e.localizedMessage}", e)
        }
    }

    fun register(email: String, username: String, password: String) = liveData {
        try {
            val response = authService.register(RegisterRequest(email, username, password))
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                emit(null) // Ошибка регистрации
                Log.e("RegisterError", "Ошибка регистрации: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            emit(null) // Ошибка сети или других проблем
            Log.e("RegisterError", "Ошибка сети: ${e.localizedMessage}", e)
        }
    }
}
