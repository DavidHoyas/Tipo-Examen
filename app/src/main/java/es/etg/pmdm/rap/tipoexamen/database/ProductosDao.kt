package es.etg.pmdm.rap.tipoexamen.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductosDao {
    @Query("SELECT * FROM productos")
    suspend fun getAll(): List<ProductosEntity>

    @Insert
    suspend fun insertAll(productos: List<ProductosEntity>)
}
