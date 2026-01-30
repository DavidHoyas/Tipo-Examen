package es.etg.pmdm.rap.tipoexamen

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.gson.Gson
import es.etg.pmdm.rap.tipoexamen.data.ProductosAPIService
import es.etg.pmdm.rap.tipoexamen.database.ProductosEntity
import es.etg.pmdm.rap.tipoexamen.database.ProductosDatabase
import es.etg.pmdm.rap.tipoexamen.databinding.ActivitySegundaPantallaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SegundaPantalla : AppCompatActivity() {

    private lateinit var binding: ActivitySegundaPantallaBinding
    private lateinit var productosAdapter: ProductosAdapter

    private val database: ProductosDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            ProductosDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    companion object {
        const val BASE_URL = "https://api.restful-api.dev/"
        const val DATABASE_NAME = "productos-db"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySegundaPantallaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        leerDatos()

        binding.tvActualizar.setOnClickListener {
            insertarDatos()
        }
    }

    private fun getRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private fun setupRecyclerView() {
        productosAdapter = ProductosAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = productosAdapter
    }

    private fun insertarDatos() {
        Toast.makeText(this, "Actualizando datos desde la API...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
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

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SegundaPantalla, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show()
                        leerDatos()
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SegundaPantalla, "Error API: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SegundaPantalla, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    private fun leerDatos() {
        CoroutineScope(Dispatchers.IO).launch {
            val productos = database.productosDao().getAll()

            withContext(Dispatchers.Main) {
                productosAdapter.actualizarProductos(productos)
            }
        }
    }
}
