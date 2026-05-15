package com.itson.edu.mx.sigmapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var chartInventario: BarChart
    private lateinit var chartVentas: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chartInventario = findViewById(R.id.chartInventario)
        chartVentas = findViewById(R.id.chartVentas)

        cargarDatos()
    }

    private fun cargarDatos() {
        // lifecycleScope maneja el hilo secundario automáticamente
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Simulamos el Login (Cualquier usuario registrado en la BD)
                val loginResponse = RetrofitClient.apiService.login(
                    LoginRequest("gerente@miempresa.com", "clave123")
                )

                // Guardamos el token en nuestro cliente para las siguientes peticiones
                RetrofitClient.authToken = loginResponse.token

                // 2. Traemos los datos llamando a la API Express
                val inventarioBajo = RetrofitClient.apiService.getInventarioBajo()
                val ventasRecientes = RetrofitClient.apiService.getVentasRecientes()

                // 3. Volvemos al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {
                    dibujarGraficaInventario(inventarioBajo)
                    dibujarGraficaVentas(ventasRecientes)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun dibujarGraficaInventario(productos: List<ProductoStock>) {
        val entradas = ArrayList<BarEntry>()
        // Iteramos los productos para poner la posición X y el valor de stock en Y
        productos.forEachIndexed { index, producto ->
            entradas.add(BarEntry(index.toFloat(), producto.inventario.toFloat()))
        }

        val dataSet = BarDataSet(entradas, "Stock (Unidades)")
        // Cambio de color aquí
        dataSet.color = resources.getColor(android.R.color.holo_red_light, null)

        chartInventario.data = BarData(dataSet)
        chartInventario.description.isEnabled = false
        chartInventario.animateY(1000)
        chartInventario.invalidate() // Refrescar gráfica
    }

    private fun dibujarGraficaVentas(ventas: List<VentaReciente>) {
        val entradas = ArrayList<BarEntry>()

        // El 'total' de la API viene como String ("250.00"), lo pasamos a Float
        ventas.forEachIndexed { index, venta ->
            entradas.add(BarEntry(index.toFloat(), venta.total.toFloat()))
        }

        val dataSet = BarDataSet(entradas, "Total de Ventas ($)")
        dataSet.color = resources.getColor(android.R.color.holo_green_dark, null)

        chartVentas.data = BarData(dataSet)
        chartVentas.description.isEnabled = false
        chartVentas.animateY(1000)
        chartVentas.invalidate()
    }
}