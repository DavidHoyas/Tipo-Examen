package es.etg.pmdm.rap.tipoexamen

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.gson.Gson
import es.etg.pmdm.rap.tipoexamen.data.ProductosAPIService
import es.etg.pmdm.rap.tipoexamen.database.ProductosEntity
import es.etg.pmdm.rap.tipoexamen.database.ProductosDatabase
import es.etg.pmdm.rap.tipoexamen.databinding.ActivitySegundaPantallaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SegundaPantalla : AppCompatActivity() {

    private lateinit var binding: ActivitySegundaPantallaBinding
    private lateinit var productosAdapter: ProductosAdapter

    companion object {
        const val BASE_URL = "https://api.restful-api.dev/"
        const val DATABASE_NAME = "productos-db"
    }

    private val database: ProductosDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            ProductosDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySegundaPantallaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        cargarDatosIniciales()

        binding.tvActualizar.setOnClickListener {
            comprobarDatos()
        }
    }

    private fun setupRecyclerView() {
        productosAdapter = ProductosAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = productosAdapter
    }

    private fun getRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private fun cargarDatosIniciales() {
        lifecycleScope.launch(Dispatchers.IO) {
            val productos = database.productosDao().getAll()
            withContext(Dispatchers.Main) {
                productosAdapter.actualizarProductos(productos)
            }
        }
    }

    private fun comprobarDatos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val productosBD = database.productosDao().getAll()

            if (productosBD.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SegundaPantalla,
                        "No hay datos. Cargando desde la API...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                insertarDatos()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SegundaPantalla,
                        "Cargando datos desde la base de datos",
                        Toast.LENGTH_SHORT
                    ).show()
                    productosAdapter.actualizarProductos(productosBD)
                }
            }
        }
    }

    private fun insertarDatos() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = getRetrofit().create(ProductosAPIService::class.java)
                val response = api.getProductos("objects")

                if (response.isSuccessful) {
                    val productosAPI = response.body() ?: emptyList()
                    val gson = Gson()

                    val productosParaBD = productosAPI.map { p ->
                        ProductosEntity(
                            nombre = p.name ?: "Sin nombre",
                            data = gson.toJson(p.data)
                        )
                    }

                    database.productosDao().insertAll(productosParaBD)

                    val productosBD = database.productosDao().getAll()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SegundaPantalla,
                            "Datos guardados correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        productosAdapter.actualizarProductos(productosBD)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SegundaPantalla,
                            "Error API: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SegundaPantalla,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
