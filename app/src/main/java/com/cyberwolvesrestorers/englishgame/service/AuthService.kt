package com.cyberwolvesrestorers.englishgame.service

import com.cyberwolvesrestorers.englishgame.model.LoginRequest
import com.cyberwolvesrestorers.englishgame.model.RegisterRequest
import com.cyberwolvesrestorers.englishgame.model.TokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<TokenDto>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<TokenDto>
}