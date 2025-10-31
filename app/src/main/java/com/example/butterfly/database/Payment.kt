package com.example.butterfly.database

/**
 * Clase modelo para representar un pago
 */
data class Payment(
    val id: Long = 0,
    val patientId: Long,
    val amount: Double,
    val concept: String,
    val paymentMethod: String, // "Efectivo", "Tarjeta", "Transferencia", "Otro"
    val paymentDate: String, // Format: yyyy-MM-dd HH:mm
    val psychologistEmail: String,
    val notes: String? = null,
    val createdAt: String? = null
) {
    /**
     * Formatea el monto con símbolo de moneda
     */
    fun getFormattedAmount(): String {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "MX"))
        return format.format(amount)
    }

    /**
     * Formatea la fecha del pago para mostrar
     */
    fun getFormattedDate(): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val date = inputFormat.parse(paymentDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            paymentDate
        }
    }

    /**
     * Retorna una descripción corta del pago
     */
    fun getSummary(): String {
        return "${getFormattedAmount()} - $paymentMethod - ${getFormattedDate()}"
    }

    override fun toString(): String {
        return "Payment(id=$id, patientId=$patientId, amount=$amount, paymentMethod='$paymentMethod')"
    }
}