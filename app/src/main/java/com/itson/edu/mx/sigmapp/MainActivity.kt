package com.itson.edu.mx.sigmapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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

        configurarEstiloGrafica(chartInventario)
        configurarEstiloGrafica(chartVentas)

        cargarDatos()
    }

    private fun configurarEstiloGrafica(chart: BarChart) {
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.setPinchZoom(false)
        chart.setDoubleTapToZoomEnabled(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.textColor = ContextCompat.getColor(this, R.color.textSecondary)

        chart.axisLeft.setDrawGridLines(true)
        chart.axisLeft.gridColor = Color.LTGRAY
        chart.axisLeft.textColor = ContextCompat.getColor(this, R.color.textSecondary)
        
        chart.axisRight.isEnabled = false
        chart.legend.textColor = ContextCompat.getColor(this, R.color.textPrimary)
        chart.setNoDataText("Cargando datos...")
    }

    private fun cargarDatos() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val loginResponse = RetrofitClient.apiService.login(
                    LoginRequest("gerente@mitienda.com", "clave123")
                )
                RetrofitClient.authToken = loginResponse.token

                val inventarioBajo = RetrofitClient.apiService.getInventarioBajo()
                val ventasRecientes = RetrofitClient.apiService.getVentasRecientes()

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
        val labels = ArrayList<String>()

        productos.forEachIndexed { index, producto ->
            entradas.add(BarEntry(index.toFloat(), producto.inventario.toFloat()))
            labels.add(producto.nombre)
        }

        val dataSet = BarDataSet(entradas, getString(R.string.stock_unidades))
        dataSet.color = ContextCompat.getColor(this, R.color.chartColor1)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.textPrimary)
        dataSet.valueTextSize = 10f

        chartInventario.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chartInventario.data = BarData(dataSet)
        chartInventario.animateY(1000)
        chartInventario.invalidate()
    }

    private fun dibujarGraficaVentas(ventas: List<VentaReciente>) {
        val entradas = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        ventas.forEachIndexed { index, venta ->
            entradas.add(BarEntry(index.toFloat(), venta.total.toFloat()))
            labels.add("ID: ${venta.id}")
        }

        val dataSet = BarDataSet(entradas, getString(R.string.total_ventas))
        dataSet.color = ContextCompat.getColor(this, R.color.chartColor2)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.textPrimary)
        dataSet.valueTextSize = 10f

        chartVentas.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chartVentas.data = BarData(dataSet)
        chartVentas.animateY(1000)
        chartVentas.invalidate()
    }
}