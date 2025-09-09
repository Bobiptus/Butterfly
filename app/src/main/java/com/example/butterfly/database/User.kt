package com.example.butterfly.database

/**
 * Clase modelo para representar un usuario en la aplicación
 */
data class User(
    val id: Long = 0,
    val email: String,
    val name: String? = null,
    val createdAt: String? = null,
    val lastLogin: String? = null
) {
    /**
     * Retorna el nombre del usuario o un valor por defecto
     */
    fun getDisplayName(): String {
        return when {
            !name.isNullOrBlank() -> name
            else -> email.substringBefore("@")
        }
    }

    /**
     * Verifica si el usuario tiene un nombre establecido
     */
    fun hasName(): Boolean = !name.isNullOrBlank()

    override fun toString(): String {
        return "User(id=$id, email='$email', name='$name')"
    }
}