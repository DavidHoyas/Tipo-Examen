package es.etg.pmdm.rap.tipoexamen.data

import retrofit2.Response
import retrofit2.http.GET

interface ProductosAPIService {

    @GET("objects")
    suspend fun getProductos(): Response<List<ProductosResponse>>
}
