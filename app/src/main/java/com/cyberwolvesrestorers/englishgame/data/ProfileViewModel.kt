package com.cyberwolvesrestorers.englishgame.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.cyberwolvesrestorers.englishgame.service.ProfileService
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileViewModel : ViewModel() {
    private val profileService: ProfileService = Retrofit.Builder()
        .baseUrl("https://eng.quassbot.ru/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProfileService::class.java)

    fun fetchProfile(token: String) = liveData(Dispatchers.IO) {
        try {
            val s = "Bearer $token"
            Log.d("Token", s)
            val response = profileService.getProfile("Bearer $token")
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                emit(null)
                Log.e("ProfileError", "Ошибка загрузки профиля: ${'$'}{response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            emit(null)
            Log.e("ProfileError", "Ошибка сети: ${'$'}{e.localizedMessage}", e)
        }
    }
}