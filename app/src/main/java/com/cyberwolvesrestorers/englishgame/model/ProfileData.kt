package com.cyberwolvesrestorers.englishgame.model

data class ProfileData(
    val id: Int,
    val email: String,
    val username: String,
    val points: Int,
    val streak: Int
)
