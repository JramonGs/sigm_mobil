package com.itson.edu.mx.sigmapp

// Modelos para el Login
data class LoginRequest(val email: String, val contrasena: String)
data class LoginResponse(val token: String)

// Modelos para las Gráficas
data class VentaReciente(
    val id: Int,
    val fecha: String,
    val total: String,
    val cliente: String
)

data class ProductoStock(
    val id: Int,
    val nombre: String,
    val inventario: Int
)