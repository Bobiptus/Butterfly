package com.example.butterfly.database

/**
 * Clase modelo para representar una sesión psicológica
 */
data class Session(
    val id: Long = 0,
    val patientId: Long,
    val sessionDate: String, // Format: yyyy-MM-dd HH:mm
    val duration: Int, // En minutos
    val sessionType: String, // "Individual", "Grupal", "Online"
    val notes: String, // Notas de la sesión
    val observations: String?, // Observaciones clínicas
    val homework: String?, // Tareas para el paciente
    val nextSessionDate: String?, // Próxima sesión programada
    val psychologistEmail: String,
    val cost: Double = 0.0, // Costo de la sesión
    val isPaid: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    /**
     * Retorna la duración formateada
     */
    fun getFormattedDuration(): String {
        return if (duration < 60) {
            "$duration minutos"
        } else {
            val hours = duration / 60
            val minutes = duration % 60
            if (minutes > 0) "$hours h $minutes m" else "$hours h"
        }
    }

    /**
     * Formatea la fecha de la sesión para mostrar
     */
    fun getFormattedDate(): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val date = inputFormat.parse(sessionDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            sessionDate
        }
    }

    override fun toString(): String {
        return "Session(id=$id, patientId=$patientId, sessionDate='$sessionDate', duration=$duration)"
    }
}