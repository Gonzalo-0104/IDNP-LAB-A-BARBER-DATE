package com.example.barberdate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EspecialistaDao {

    @Query("SELECT * FROM especialistas ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<EspecialistaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(especialista: EspecialistaEntity)

    @Delete
    suspend fun eliminar(especialista: EspecialistaEntity)
}