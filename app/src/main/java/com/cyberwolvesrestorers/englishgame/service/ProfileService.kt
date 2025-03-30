package com.cyberwolvesrestorers.englishgame.service

import com.cyberwolvesrestorers.englishgame.model.ProfileData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ProfileService {
    @GET("profile/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileData>
}