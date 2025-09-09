package com.example.butterfly.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "butterfly_users.db"
        private const val DATABASE_VERSION = 1

        // Tabla de usuarios
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_LAST_LOGIN = "last_login"

        // Query para crear la tabla
        private const val CREATE_TABLE_USERS = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_NAME TEXT,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_LAST_LOGIN DATETIME
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_USERS)
        Log.d("DatabaseHelper", "Tabla de usuarios creada")

        // Insertar usuario de prueba por defecto
        insertTestUser(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Actualizando base de datos de v$oldVersion a v$newVersion")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertTestUser(db: SQLiteDatabase?) {
        val testEmail = "test@test.com"
        val testPassword = "123456"
        val hashedPassword = hashPassword(testPassword)

        val values = ContentValues().apply {
            put(COLUMN_EMAIL, testEmail)
            put(COLUMN_PASSWORD, hashedPassword)
            put(COLUMN_NAME, "Usuario de Prueba")
        }

        try {
            db?.insert(TABLE_USERS, null, values)
            Log.d("DatabaseHelper", "Usuario de prueba creado: $testEmail")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al crear usuario de prueba: ${e.message}")
        }
    }

    /**
     * Registra un nuevo usuario
     */
    fun registerUser(email: String, password: String, name: String? = null): Boolean {
        val db = writableDatabase

        return try {
            // Verificar si el email ya existe
            if (isEmailExists(email)) {
                Log.w("DatabaseHelper", "Email ya existe: $email")
                false
            } else {
                val hashedPassword = hashPassword(password)
                val values = ContentValues().apply {
                    put(COLUMN_EMAIL, email)
                    put(COLUMN_PASSWORD, hashedPassword)
                    put(COLUMN_NAME, name ?: "")
                }

                val result = db.insert(TABLE_USERS, null, values)
                if (result != -1L) {
                    Log.d("DatabaseHelper", "Usuario registrado exitosamente: $email")
                    true
                } else {
                    Log.e("DatabaseHelper", "Error al registrar usuario: $email")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en registerUser: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    /**
     * Autentica un usuario
     */
    fun authenticateUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val hashedPassword = hashPassword(password)

        return try {
            val cursor = db.query(
                TABLE_USERS,
                arrayOf(COLUMN_ID),
                "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
                arrayOf(email, hashedPassword),
                null, null, null
            )

            val isAuthenticated = cursor.count > 0
            cursor.close()

            if (isAuthenticated) {
                // Actualizar último login
                updateLastLogin(email)
                Log.d("DatabaseHelper", "Autenticación exitosa para: $email")
            } else {
                Log.w("DatabaseHelper", "Autenticación fallida para: $email")
            }

            isAuthenticated
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en authenticateUser: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    /**
     * Verifica si un email ya existe
     */
    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase

        return try {
            val cursor = db.query(
                TABLE_USERS,
                arrayOf(COLUMN_ID),
                "$COLUMN_EMAIL = ?",
                arrayOf(email),
                null, null, null
            )

            val exists = cursor.count > 0
            cursor.close()
            exists
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en isEmailExists: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    /**
     * Obtiene la información del usuario por email
     */
    fun getUserByEmail(email: String): User? {
        val db = readableDatabase

        return try {
            val cursor = db.query(
                TABLE_USERS,
                arrayOf(COLUMN_ID, COLUMN_EMAIL, COLUMN_NAME, COLUMN_CREATED_AT, COLUMN_LAST_LOGIN),
                "$COLUMN_EMAIL = ?",
                arrayOf(email),
                null, null, null
            )

            val user = if (cursor.moveToFirst()) {
                User(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                    lastLogin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_LOGIN))
                )
            } else null

            cursor.close()
            user
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en getUserByEmail: ${e.message}")
            null
        } finally {
            db.close()
        }
    }

    /**
     * Actualiza el último login del usuario
     */
    private fun updateLastLogin(email: String) {
        val db = writableDatabase

        try {
            val values = ContentValues().apply {
                put(COLUMN_LAST_LOGIN, System.currentTimeMillis())
            }

            db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al actualizar último login: ${e.message}")
        } finally {
            db.close()
        }
    }

    /**
     * Cambia la contraseña de un usuario
     */
    fun changePassword(email: String, newPassword: String): Boolean {
        val db = writableDatabase

        return try {
            val hashedPassword = hashPassword(newPassword)
            val values = ContentValues().apply {
                put(COLUMN_PASSWORD, hashedPassword)
            }

            val rowsAffected = db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
            if (rowsAffected > 0) {
                Log.d("DatabaseHelper", "Contraseña actualizada para: $email")
                true
            } else {
                Log.w("DatabaseHelper", "No se pudo actualizar contraseña para: $email")
                false
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al cambiar contraseña: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    /**
     * Obtiene todos los usuarios (para testing/admin)
     */
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase

        try {
            val cursor = db.query(
                TABLE_USERS,
                arrayOf(COLUMN_ID, COLUMN_EMAIL, COLUMN_NAME, COLUMN_CREATED_AT, COLUMN_LAST_LOGIN),
                null, null, null, null,
                "$COLUMN_CREATED_AT DESC"
            )

            while (cursor.moveToNext()) {
                users.add(
                    User(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                        lastLogin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_LOGIN))
                    )
                )
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al obtener usuarios: ${e.message}")
        } finally {
            db.close()
        }

        return users
    }

    /**
     * Elimina un usuario por email
     */
    fun deleteUser(email: String): Boolean {
        val db = writableDatabase

        return try {
            val rowsDeleted = db.delete(TABLE_USERS, "$COLUMN_EMAIL = ?", arrayOf(email))
            if (rowsDeleted > 0) {
                Log.d("DatabaseHelper", "Usuario eliminado: $email")
                true
            } else {
                Log.w("DatabaseHelper", "No se encontró usuario para eliminar: $email")
                false
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al eliminar usuario: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    /**
     * Encripta la contraseña usando SHA-256
     */
    private fun hashPassword(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray())
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al encriptar contraseña: ${e.message}")
            password // Fallback (no recomendado en producción)
        }
    }
}