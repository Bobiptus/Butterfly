package com.example.butterfly.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "butterfly_users.db"
        private const val DATABASE_VERSION = 3

        // Tabla de usuarios
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_LAST_LOGIN = "last_login"

        // Tabla de pacientes
        private const val TABLE_PATIENTS = "patients"
        private const val COLUMN_PATIENT_ID = "id"
        private const val COLUMN_PATIENT_NAME = "name"
        private const val COLUMN_PATIENT_EMAIL = "email"
        private const val COLUMN_PATIENT_PHONE = "phone"
        private const val COLUMN_PATIENT_BIRTH_DATE = "birth_date"
        private const val COLUMN_PATIENT_ADDRESS = "address"
        private const val COLUMN_PATIENT_EMERGENCY_CONTACT = "emergency_contact"
        private const val COLUMN_PATIENT_EMERGENCY_PHONE = "emergency_phone"
        private const val COLUMN_PATIENT_MEDICAL_HISTORY = "medical_history"
        private const val COLUMN_PATIENT_CURRENT_MEDICATION = "current_medication"
        private const val COLUMN_PATIENT_INITIAL_REASON = "initial_reason"
        private const val COLUMN_PATIENT_NOTES = "notes"
        private const val COLUMN_PATIENT_IS_ACTIVE = "is_active"
        private const val COLUMN_PATIENT_CREATED_AT = "created_at"
        private const val COLUMN_PATIENT_UPDATED_AT = "updated_at"
        private const val COLUMN_PATIENT_CREATED_BY = "created_by"

        // Tabla de sesiones
        private const val TABLE_SESSIONS = "sessions"
        private const val COLUMN_SESSION_ID = "id"
        private const val COLUMN_SESSION_PATIENT_ID = "patient_id"
        private const val COLUMN_SESSION_DATE = "session_date"
        private const val COLUMN_SESSION_DURATION = "duration"
        private const val COLUMN_SESSION_TYPE = "session_type"
        private const val COLUMN_SESSION_NOTES = "notes"
        private const val COLUMN_SESSION_OBSERVATIONS = "observations"
        private const val COLUMN_SESSION_HOMEWORK = "homework"
        private const val COLUMN_SESSION_NEXT_DATE = "next_session_date"
        private const val COLUMN_SESSION_PSYCHOLOGIST = "psychologist_email"
        private const val COLUMN_SESSION_COST = "cost"
        private const val COLUMN_SESSION_IS_PAID = "is_paid"
        private const val COLUMN_SESSION_CREATED_AT = "created_at"
        private const val COLUMN_SESSION_UPDATED_AT = "updated_at"

        // Tabla de pagos
        private const val TABLE_PAYMENTS = "payments"
        private const val COLUMN_PAYMENT_ID = "id"
        private const val COLUMN_PAYMENT_PATIENT_ID = "patient_id"
        private const val COLUMN_PAYMENT_AMOUNT = "amount"
        private const val COLUMN_PAYMENT_CONCEPT = "concept"
        private const val COLUMN_PAYMENT_METHOD = "payment_method"
        private const val COLUMN_PAYMENT_DATE = "payment_date"
        private const val COLUMN_PAYMENT_PSYCHOLOGIST = "psychologist_email"
        private const val COLUMN_PAYMENT_NOTES = "notes"
        private const val COLUMN_PAYMENT_CREATED_AT = "created_at"

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

        private const val CREATE_TABLE_PATIENTS = """
            CREATE TABLE $TABLE_PATIENTS (
                $COLUMN_PATIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PATIENT_NAME TEXT NOT NULL,
                $COLUMN_PATIENT_EMAIL TEXT,
                $COLUMN_PATIENT_PHONE TEXT,
                $COLUMN_PATIENT_BIRTH_DATE TEXT,
                $COLUMN_PATIENT_ADDRESS TEXT,
                $COLUMN_PATIENT_EMERGENCY_CONTACT TEXT,
                $COLUMN_PATIENT_EMERGENCY_PHONE TEXT,
                $COLUMN_PATIENT_MEDICAL_HISTORY TEXT,
                $COLUMN_PATIENT_CURRENT_MEDICATION TEXT,
                $COLUMN_PATIENT_INITIAL_REASON TEXT NOT NULL,
                $COLUMN_PATIENT_NOTES TEXT,
                $COLUMN_PATIENT_IS_ACTIVE INTEGER DEFAULT 1,
                $COLUMN_PATIENT_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_PATIENT_UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_PATIENT_CREATED_BY TEXT NOT NULL
            )
        """

        private const val CREATE_TABLE_SESSIONS = """
            CREATE TABLE $TABLE_SESSIONS (
                $COLUMN_SESSION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SESSION_PATIENT_ID INTEGER NOT NULL,
                $COLUMN_SESSION_DATE TEXT NOT NULL,
                $COLUMN_SESSION_DURATION INTEGER NOT NULL,
                $COLUMN_SESSION_TYPE TEXT NOT NULL,
                $COLUMN_SESSION_NOTES TEXT NOT NULL,
                $COLUMN_SESSION_OBSERVATIONS TEXT,
                $COLUMN_SESSION_HOMEWORK TEXT,
                $COLUMN_SESSION_NEXT_DATE TEXT,
                $COLUMN_SESSION_PSYCHOLOGIST TEXT NOT NULL,
                $COLUMN_SESSION_COST REAL DEFAULT 0.0,
                $COLUMN_SESSION_IS_PAID INTEGER DEFAULT 0,
                $COLUMN_SESSION_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_SESSION_UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_SESSION_PATIENT_ID) REFERENCES $TABLE_PATIENTS($COLUMN_PATIENT_ID)
            )
        """

        private const val CREATE_TABLE_PAYMENTS = """
            CREATE TABLE $TABLE_PAYMENTS (
                $COLUMN_PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PAYMENT_PATIENT_ID INTEGER NOT NULL,
                $COLUMN_PAYMENT_AMOUNT REAL NOT NULL,
                $COLUMN_PAYMENT_CONCEPT TEXT,
                $COLUMN_PAYMENT_METHOD TEXT,
                $COLUMN_PAYMENT_DATE TEXT NOT NULL,
                $COLUMN_PAYMENT_PSYCHOLOGIST TEXT NOT NULL,
                $COLUMN_PAYMENT_NOTES TEXT,
                $COLUMN_PAYMENT_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_PAYMENT_PATIENT_ID) REFERENCES $TABLE_PATIENTS($COLUMN_PATIENT_ID)
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_USERS)
        db?.execSQL(CREATE_TABLE_PATIENTS)
        db?.execSQL(CREATE_TABLE_SESSIONS)
        db?.execSQL(CREATE_TABLE_PAYMENTS)
        Log.d("DatabaseHelper", "Tablas creadas")
        insertTestUser(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Actualizando base de datos de v$oldVersion a v$newVersion")

        if (oldVersion < 2) {
            db?.execSQL(CREATE_TABLE_PATIENTS)
        }
        if (oldVersion < 3) {
            db?.execSQL(CREATE_TABLE_SESSIONS)
            db?.execSQL(CREATE_TABLE_PAYMENTS)
        }
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

    // ============= MÉTODOS DE USUARIOS =============

    fun registerUser(email: String, password: String, name: String? = null): Boolean {
        val db = writableDatabase

        return try {
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
        }
    }

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
                updateLastLogin(email)
                Log.d("DatabaseHelper", "Autenticación exitosa para: $email")
            } else {
                Log.w("DatabaseHelper", "Autenticación fallida para: $email")
            }

            isAuthenticated
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en authenticateUser: ${e.message}")
            false
        }
    }

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
        }
    }

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
        }
    }

    private fun updateLastLogin(email: String) {
        val db = writableDatabase

        try {
            val values = ContentValues().apply {
                put(COLUMN_LAST_LOGIN, System.currentTimeMillis())
            }

            db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al actualizar último login: ${e.message}")
        }
    }

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
        }
    }

    // ============= MÉTODOS DE PACIENTES =============

    fun createPatient(patient: Patient): Long {
        val db = writableDatabase

        return try {
            val values = ContentValues().apply {
                put(COLUMN_PATIENT_NAME, patient.name)
                put(COLUMN_PATIENT_EMAIL, patient.email)
                put(COLUMN_PATIENT_PHONE, patient.phone)
                put(COLUMN_PATIENT_BIRTH_DATE, patient.birthDate)
                put(COLUMN_PATIENT_ADDRESS, patient.address)
                put(COLUMN_PATIENT_EMERGENCY_CONTACT, patient.emergencyContact)
                put(COLUMN_PATIENT_EMERGENCY_PHONE, patient.emergencyPhone)
                put(COLUMN_PATIENT_MEDICAL_HISTORY, patient.medicalHistory)
                put(COLUMN_PATIENT_CURRENT_MEDICATION, patient.currentMedication)
                put(COLUMN_PATIENT_INITIAL_REASON, patient.initialReason)
                put(COLUMN_PATIENT_NOTES, patient.notes)
                put(COLUMN_PATIENT_IS_ACTIVE, if (patient.isActive) 1 else 0)
                put(COLUMN_PATIENT_CREATED_BY, patient.createdBy)
            }

            val result = db.insert(TABLE_PATIENTS, null, values)
            if (result != -1L) {
                Log.d("DatabaseHelper", "Paciente creado exitosamente: ${patient.name}")
            } else {
                Log.e("DatabaseHelper", "Error al crear paciente: ${patient.name}")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en createPatient: ${e.message}")
            -1L
        }
    }

    fun getPatientsByPsychologist(psychologistEmail: String): List<Patient> {
        val patients = mutableListOf<Patient>()
        val db = readableDatabase

        try {
            val cursor = db.query(
                TABLE_PATIENTS,
                null,
                "$COLUMN_PATIENT_CREATED_BY = ? AND $COLUMN_PATIENT_IS_ACTIVE = ?",
                arrayOf(psychologistEmail, "1"),
                null, null,
                "$COLUMN_PATIENT_NAME ASC"
            )

            while (cursor.moveToNext()) {
                patients.add(cursorToPatient(cursor))
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al obtener pacientes: ${e.message}")
        }

        return patients
    }

    fun searchPatients(query: String, psychologistEmail: String): List<Patient> {
        val patients = mutableListOf<Patient>()
        val db = readableDatabase

        try {
            val cursor = db.query(
                TABLE_PATIENTS,
                null,
                "$COLUMN_PATIENT_CREATED_BY = ? AND $COLUMN_PATIENT_IS_ACTIVE = ? AND " +
                        "($COLUMN_PATIENT_NAME LIKE ? OR $COLUMN_PATIENT_PHONE LIKE ? OR $COLUMN_PATIENT_EMAIL LIKE ?)",
                arrayOf(psychologistEmail, "1", "%$query%", "%$query%", "%$query%"),
                null, null,
                "$COLUMN_PATIENT_NAME ASC"
            )

            while (cursor.moveToNext()) {
                patients.add(cursorToPatient(cursor))
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al buscar pacientes: ${e.message}")
        }

        return patients
    }

    fun getPatientById(patientId: Long): Patient? {
        val db = readableDatabase

        return try {
            val cursor = db.query(
                TABLE_PATIENTS,
                null,
                "$COLUMN_PATIENT_ID = ?",
                arrayOf(patientId.toString()),
                null, null, null
            )

            val patient = if (cursor.moveToFirst()) {
                cursorToPatient(cursor)
            } else null

            cursor.close()
            patient
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en getPatientById: ${e.message}")
            null
        }
    }

    fun updatePatient(patient: Patient): Boolean {
        val db = writableDatabase

        return try {
            val values = ContentValues().apply {
                put(COLUMN_PATIENT_NAME, patient.name)
                put(COLUMN_PATIENT_EMAIL, patient.email)
                put(COLUMN_PATIENT_PHONE, patient.phone)
                put(COLUMN_PATIENT_BIRTH_DATE, patient.birthDate)
                put(COLUMN_PATIENT_ADDRESS, patient.address)
                put(COLUMN_PATIENT_EMERGENCY_CONTACT, patient.emergencyContact)
                put(COLUMN_PATIENT_EMERGENCY_PHONE, patient.emergencyPhone)
                put(COLUMN_PATIENT_MEDICAL_HISTORY, patient.medicalHistory)
                put(COLUMN_PATIENT_CURRENT_MEDICATION, patient.currentMedication)
                put(COLUMN_PATIENT_INITIAL_REASON, patient.initialReason)
                put(COLUMN_PATIENT_NOTES, patient.notes)
                put(COLUMN_PATIENT_IS_ACTIVE, if (patient.isActive) 1 else 0)
                put(COLUMN_PATIENT_UPDATED_AT, getCurrentTimestamp())
            }

            val rowsAffected = db.update(
                TABLE_PATIENTS,
                values,
                "$COLUMN_PATIENT_ID = ?",
                arrayOf(patient.id.toString())
            )

            rowsAffected > 0
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al actualizar paciente: ${e.message}")
            false
        }
    }

    private fun cursorToPatient(cursor: android.database.Cursor): Patient {
        return Patient(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NAME)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_EMAIL)),
            phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_PHONE)),
            birthDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_BIRTH_DATE)),
            address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ADDRESS)),
            emergencyContact = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_EMERGENCY_CONTACT)),
            emergencyPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_EMERGENCY_PHONE)),
            medicalHistory = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_MEDICAL_HISTORY)),
            currentMedication = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_CURRENT_MEDICATION)),
            initialReason = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_INITIAL_REASON)),
            notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_NOTES)),
            isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_IS_ACTIVE)) == 1,
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_CREATED_AT)),
            updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_UPDATED_AT)),
            createdBy = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_CREATED_BY))
        )
    }

    // ============= MÉTODOS DE SESIONES =============

    fun createSession(session: Session): Long {
        val db = writableDatabase

        return try {
            val values = ContentValues().apply {
                put(COLUMN_SESSION_PATIENT_ID, session.patientId)
                put(COLUMN_SESSION_DATE, session.sessionDate)
                put(COLUMN_SESSION_DURATION, session.duration)
                put(COLUMN_SESSION_TYPE, session.sessionType)
                put(COLUMN_SESSION_NOTES, session.notes)
                put(COLUMN_SESSION_OBSERVATIONS, session.observations)
                put(COLUMN_SESSION_HOMEWORK, session.homework)
                put(COLUMN_SESSION_NEXT_DATE, session.nextSessionDate)
                put(COLUMN_SESSION_PSYCHOLOGIST, session.psychologistEmail)
                put(COLUMN_SESSION_COST, session.cost)
                put(COLUMN_SESSION_IS_PAID, if (session.isPaid) 1 else 0)
            }

            val result = db.insert(TABLE_SESSIONS, null, values)
            if (result != -1L) {
                Log.d("DatabaseHelper", "Sesión creada exitosamente para paciente: ${session.patientId}")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en createSession: ${e.message}")
            -1L
        }
    }

    fun getSessionsByPatient(patientId: Long): List<Session> {
        val sessions = mutableListOf<Session>()
        val db = readableDatabase

        try {
            val cursor = db.query(
                TABLE_SESSIONS,
                null,
                "$COLUMN_SESSION_PATIENT_ID = ?",
                arrayOf(patientId.toString()),
                null, null,
                "$COLUMN_SESSION_DATE DESC"
            )

            while (cursor.moveToNext()) {
                sessions.add(cursorToSession(cursor))
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al obtener sesiones: ${e.message}")
        }

        return sessions
    }

    fun getSessionById(sessionId: Long): Session? {
        val db = readableDatabase

        return try {
            val cursor = db.query(
                TABLE_SESSIONS,
                null,
                "$COLUMN_SESSION_ID = ?",
                arrayOf(sessionId.toString()),
                null, null, null
            )

            val session = if (cursor.moveToFirst()) {
                cursorToSession(cursor)
            } else null

            cursor.close()
            session
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en getSessionById: ${e.message}")
            null
        }
    }

    fun updateSession(session: Session): Boolean {
        val db = writableDatabase

        return try {
            val values = ContentValues().apply {
                put(COLUMN_SESSION_DATE, session.sessionDate)
                put(COLUMN_SESSION_DURATION, session.duration)
                put(COLUMN_SESSION_TYPE, session.sessionType)
                put(COLUMN_SESSION_NOTES, session.notes)
                put(COLUMN_SESSION_OBSERVATIONS, session.observations)
                put(COLUMN_SESSION_HOMEWORK, session.homework)
                put(COLUMN_SESSION_NEXT_DATE, session.nextSessionDate)
                put(COLUMN_SESSION_COST, session.cost)
                put(COLUMN_SESSION_IS_PAID, if (session.isPaid) 1 else 0)
                put(COLUMN_SESSION_UPDATED_AT, getCurrentTimestamp())
            }

            val rowsAffected = db.update(
                TABLE_SESSIONS,
                values,
                "$COLUMN_SESSION_ID = ?",
                arrayOf(session.id.toString())
            )

            rowsAffected > 0
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al actualizar sesión: ${e.message}")
            false
        }
    }

    private fun cursorToSession(cursor: android.database.Cursor): Session {
        return Session(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SESSION_ID)),
            patientId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SESSION_PATIENT_ID)),
            sessionDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_DATE)),
            duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SESSION_DURATION)),
            sessionType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_TYPE)),
            notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_NOTES)),
            observations = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_OBSERVATIONS)),
            homework = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_HOMEWORK)),
            nextSessionDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_NEXT_DATE)),
            psychologistEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_PSYCHOLOGIST)),
            cost = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SESSION_COST)),
            isPaid = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SESSION_IS_PAID)) == 1,
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_CREATED_AT)),
            updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_UPDATED_AT))
        )
    }

    // ============= MÉTODOS DE PAGOS =============

    fun recordPayment(payment: Payment): Long {
        val db = writableDatabase

        return try {
            val values = ContentValues().apply {
                put(COLUMN_PAYMENT_PATIENT_ID, payment.patientId)
                put(COLUMN_PAYMENT_AMOUNT, payment.amount)
                put(COLUMN_PAYMENT_CONCEPT, payment.concept)
                put(COLUMN_PAYMENT_METHOD, payment.paymentMethod)
                put(COLUMN_PAYMENT_DATE, payment.paymentDate)
                put(COLUMN_PAYMENT_PSYCHOLOGIST, payment.psychologistEmail)
                put(COLUMN_PAYMENT_NOTES, payment.notes)
            }

            val result = db.insert(TABLE_PAYMENTS, null, values)
            if (result != -1L) {
                Log.d("DatabaseHelper", "Pago registrado exitosamente")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en recordPayment: ${e.message}")
            -1L
        }
    }

    fun getPaymentsByPatient(patientId: Long): List<Payment> {
        val payments = mutableListOf<Payment>()
        val db = readableDatabase

        try {
            val cursor = db.query(
                TABLE_PAYMENTS,
                null,
                "$COLUMN_PAYMENT_PATIENT_ID = ?",
                arrayOf(patientId.toString()),
                null, null,
                "$COLUMN_PAYMENT_DATE DESC"
            )

            while (cursor.moveToNext()) {
                payments.add(cursorToPayment(cursor))
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al obtener pagos: ${e.message}")
        }

        return payments
    }

    fun getTotalDebtByPatient(patientId: Long): Double {
        val db = readableDatabase

        return try {
            val cursor = db.rawQuery(
                "SELECT COALESCE(SUM($COLUMN_SESSION_COST), 0) - COALESCE((SELECT SUM($COLUMN_PAYMENT_AMOUNT) FROM $TABLE_PAYMENTS WHERE $COLUMN_PAYMENT_PATIENT_ID = ?), 0) FROM $TABLE_SESSIONS WHERE $COLUMN_SESSION_PATIENT_ID = ?",
                arrayOf(patientId.toString(), patientId.toString())
            )

            val debt = if (cursor.moveToFirst()) {
                cursor.getDouble(0)
            } else {
                0.0
            }
            cursor.close()
            debt
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al calcular deuda: ${e.message}")
            0.0
        }
    }

    private fun cursorToPayment(cursor: android.database.Cursor): Payment {
        return Payment(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_ID)),
            patientId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_PATIENT_ID)),
            amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_AMOUNT)),
            concept = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_CONCEPT)),
            paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)),
            paymentDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_DATE)),
            psychologistEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_PSYCHOLOGIST)),
            notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_NOTES)),
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_CREATED_AT))
        )
    }

    // ============= MÉTODOS DE UTILIDAD =============

    fun getDatabaseSize(): Long {
        return try {
            val file = context.getDatabasePath(DATABASE_NAME)
            if (file.exists()) file.length() else 0
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al obtener tamaño de BD: ${e.message}")
            0
        }
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun hashPassword(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray())
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al encriptar contraseña: ${e.message}")
            password
        }
    }
}