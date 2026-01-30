package es.etg.pmdm.rap.tipoexamen.data

import es.etg.pmdm.rap.tipoexamen.data.ProductosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ProductosAPIService {

    @GET("objects")
    suspend fun getProductos(@Url url: String): Response<List<ProductosResponse>>
}