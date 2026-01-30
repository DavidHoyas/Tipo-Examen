package es.etg.pmdm.rap.tipoexamen.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProductosEntity::class], version = 3)
abstract class ProductosDatabase: RoomDatabase() {

    abstract fun productosDao(): ProductosDao
}