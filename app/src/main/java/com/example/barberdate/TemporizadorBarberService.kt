package com.example.barberdate

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TemporizadorBarberService : Service() {

    companion object {
        const val CHANNEL_ID = "barberdate_timer_channel"
        const val CHANNEL_NAME = "Temporizador de Servicio"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.barberdate.ACTION_START"
        const val ACTION_STOP = "com.example.barberdate.ACTION_STOP"

        private val _segundosTranscurridos = MutableStateFlow(0L)
        val segundosTranscurridos: StateFlow<Long> = _segundosTranscurridos.asStateFlow()

        private val _estaActivo = MutableStateFlow(false)
        val estaActivo: StateFlow<Boolean> = _estaActivo.asStateFlow()

        fun formatearTiempo(segundos: Long): String {
            val minutos = segundos / 60
            val segs = segundos % 60
            return String.format("%02d:%02d", minutos, segs)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        crearCanalDeNotificacion()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> iniciarTemporizador()
            ACTION_STOP -> detenerTemporizador()
        }
        return START_NOT_STICKY
    }

    private fun iniciarTemporizador() {
        if (_estaActivo.value) return

        _estaActivo.value = true
        _segundosTranscurridos.value = 0L

        val notificacionInicial = construirNotificacion(0L)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notificacionInicial,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notificacionInicial)
        }

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive && _estaActivo.value) {
                delay(1000L)
                val nuevoTiempo = _segundosTranscurridos.value + 1L
                _segundosTranscurridos.value = nuevoTiempo
                actualizarNotificacion(nuevoTiempo)
            }
        }
    }

    private fun detenerTemporizador() {
        _estaActivo.value = false
        timerJob?.cancel()
        timerJob = null
        _segundosTranscurridos.value = 0L

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Muestra el tiempo transcurrido del servicio de barbería en curso"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun construirNotificacion(segundos: Long): Notification {
        val abrirAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val abrirAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            abrirAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val detenerIntent = Intent(this, TemporizadorBarberService::class.java).apply {
            action = ACTION_STOP
        }
        val detenerPendingIntent = PendingIntent.getService(
            this,
            1,
            detenerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Barber Date - Servicio en curso")
            .setContentText("Tiempo transcurrido: ${formatearTiempo(segundos)}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(abrirAppPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Detener", detenerPendingIntent)
            .build()
    }

    private fun actualizarNotificacion(segundos: Long) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, construirNotificacion(segundos))
    }

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.cancel()
        _estaActivo.value = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}