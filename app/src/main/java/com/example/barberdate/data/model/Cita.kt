package com.example.barberdate.data.model

import com.google.firebase.Timestamp

data class Cita(
    val id: String = "",
    val clienteId: String = "",
    val barberoId: String = "",
    val servicio: String = "",
    val fecha: Timestamp? = null,
    val estado: String = "PENDIENTE",
    val creadoEn: Timestamp? = null
)
