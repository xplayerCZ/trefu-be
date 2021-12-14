package com.example.data.user

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val age: Int
)

data class UserDTO(
    val firstName: String,
    val lastName: String,
    val age: Int
)