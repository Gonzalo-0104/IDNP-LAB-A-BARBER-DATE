package com.example.barberdate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "especialistas")
data class EspecialistaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val especialidad: String,
    val experiencia: String
)