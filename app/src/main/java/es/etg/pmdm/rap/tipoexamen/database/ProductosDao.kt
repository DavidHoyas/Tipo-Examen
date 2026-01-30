package es.etg.pmdm.rap.tipoexamen.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductosDao {

    @Query("SELECT * FROM productos")
    suspend fun getAll(): List<ProductosEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<ProductosEntity>)

    @Query("DELETE FROM productos")
    suspend fun deleteAll()
}
