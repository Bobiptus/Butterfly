package com.example.butterfly.database

import java.text.SimpleDateFormat
import java.util.*

/**
 * Clase modelo para representar un paciente en la aplicación
 */
data class Patient(
    val id: Long = 0,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val birthDate: String? = null, // Format: yyyy-MM-dd
    val address: String? = null,
    val emergencyContact: String? = null,
    val emergencyPhone: String? = null,
    val medicalHistory: String? = null,
    val currentMedication: String? = null,
    val initialReason: String, // Motivo inicial de consulta
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val createdBy: String // Email del psicólogo que lo creó
) {
    /**
     * Calcula la edad del paciente basada en la fecha de nacimiento
     */
    fun getAge(): Int? {
        birthDate?.let { dateStr ->
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val birthDate = sdf.parse(dateStr)
                val now = Calendar.getInstance()
                val birth = Calendar.getInstance()
                birth.time = birthDate

                var age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
                if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }
                return age
            } catch (e: Exception) {
                return null
            }
        }
        return null
    }

    /**
     * Retorna el contacto principal del paciente (teléfono o email)
     */
    fun getPrimaryContact(): String {
        return when {
            !phone.isNullOrBlank() -> phone
            !email.isNullOrBlank() -> email
            else -> "Sin contacto"
        }
    }

    /**
     * Retorna una descripción resumida del paciente para listas
     */
    fun getSummary(): String {
        val ageStr = getAge()?.let { "$it años" } ?: "Edad no especificada"
        val contact = getPrimaryContact()
        return "$name • $ageStr • $contact"
    }

    /**
     * Verifica si el paciente tiene información completa básica
     */
    fun hasBasicInfo(): Boolean {
        return name.isNotBlank() &&
                initialReason.isNotBlank() &&
                (!phone.isNullOrBlank() || !email.isNullOrBlank())
    }

    /**
     * Formatea la fecha de nacimiento para mostrar
     */
    fun getFormattedBirthDate(): String? {
        birthDate?.let { dateStr ->
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateStr)
                return outputFormat.format(date)
            } catch (e: Exception) {
                return dateStr
            }
        }
        return null
    }

    override fun toString(): String {
        return "Patient(id=$id, name='$name', phone='$phone', isActive=$isActive)"
    }
}