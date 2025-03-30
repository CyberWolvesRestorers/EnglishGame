package com.cyberwolvesrestorers.englishgame.model

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)
