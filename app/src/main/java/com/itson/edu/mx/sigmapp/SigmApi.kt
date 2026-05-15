package com.itson.edu.mx.sigmapp

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SigmApi {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("/api/reportes/ventas_recientes")
    suspend fun getVentasRecientes(): List<VentaReciente>

    @GET("/api/reportes/inventario_bajo")
    suspend fun getInventarioBajo(): List<ProductoStock>
}