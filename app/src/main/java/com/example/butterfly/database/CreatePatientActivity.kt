package com.example.butterfly

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import com.example.butterfly.database.DatabaseHelper
import com.example.butterfly.database.Patient
import java.text.SimpleDateFormat
import java.util.*

class CreatePatientActivity : Activity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etAddress: EditText
    private lateinit var etEmergencyContact: EditText
    private lateinit var etEmergencyPhone: EditText
    private lateinit var etMedicalHistory: EditText
    private lateinit var etCurrentMedication: EditText
    private lateinit var etInitialReason: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var dbHelper: DatabaseHelper
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_patient)

        dbHelper = DatabaseHelper(this)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_patient_name)
        etEmail = findViewById(R.id.et_patient_email)
        etPhone = findViewById(R.id.et_patient_phone)
        etBirthDate = findViewById(R.id.et_patient_birth_date)
        etAddress = findViewById(R.id.et_patient_address)
        etEmergencyContact = findViewById(R.id.et_emergency_contact)
        etEmergencyPhone = findViewById(R.id.et_emergency_phone)
        etMedicalHistory = findViewById(R.id.et_medical_history)
        etCurrentMedication = findViewById(R.id.et_current_medication)
        etInitialReason = findViewById(R.id.et_initial_reason)
        etNotes = findViewById(R.id.et_notes)
        btnSave = findViewById(R.id.btn_save_patient)
        btnCancel = findViewById(R.id.btn_cancel)
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            savePatient()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        etBirthDate.setOnClickListener {
            showDatePicker()
        }

        // Hacer que el campo de fecha no sea editable directamente
        etBirthDate.isFocusable = false
        etBirthDate.isClickable = true
    }

    private fun showDatePicker() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etBirthDate.setText(sdf.format(selectedDate.time))
            },
            year, month, day
        )

        // Establecer fecha máxima como hoy
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun savePatient() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val emergencyContact = etEmergencyContact.text.toString().trim()
        val emergencyPhone = etEmergencyPhone.text.toString().trim()
        val medicalHistory = etMedicalHistory.text.toString().trim()
        val currentMedication = etCurrentMedication.text.toString().trim()
        val initialReason = etInitialReason.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        // Validar campos obligatorios
        if (!validateRequiredFields(name, phone, email, initialReason)) {
            return
        }

        // Obtener email del psicólogo actual
        val psychologistEmail = LoginActivity.getCurrentUserEmail(this)
        if (psychologistEmail == null) {
            Toast.makeText(this, "Error: No se pudo identificar al usuario", Toast.LENGTH_LONG).show()
            return
        }

        // Convertir fecha de nacimiento al formato de base de datos
        val birthDateForDb = if (etBirthDate.text.toString().isNotEmpty()) {
            formatDateForDatabase(etBirthDate.text.toString())
        } else null

        // Crear objeto Patient
        val patient = Patient(
            name = name,
            email = if (email.isNotEmpty()) email else null,
            phone = if (phone.isNotEmpty()) phone else null,
            birthDate = birthDateForDb,
            address = if (address.isNotEmpty()) address else null,
            emergencyContact = if (emergencyContact.isNotEmpty()) emergencyContact else null,
            emergencyPhone = if (emergencyPhone.isNotEmpty()) emergencyPhone else null,
            medicalHistory = if (medicalHistory.isNotEmpty()) medicalHistory else null,
            currentMedication = if (currentMedication.isNotEmpty()) currentMedication else null,
            initialReason = initialReason,
            notes = if (notes.isNotEmpty()) notes else null,
            createdBy = psychologistEmail
        )

        // Guardar en base de datos
        val patientId = dbHelper.createPatient(patient)

        if (patientId != -1L) {
            Toast.makeText(this, "Paciente '$name' creado exitosamente", Toast.LENGTH_LONG).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al crear el paciente. Inténtalo de nuevo.", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateRequiredFields(name: String, phone: String, email: String, initialReason: String): Boolean {
        // Validar nombre
        if (name.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            etName.requestFocus()
            return false
        }

        if (name.length < 2) {
            Toast.makeText(this, "El nombre debe tener al menos 2 caracteres", Toast.LENGTH_SHORT).show()
            etName.requestFocus()
            return false
        }

        // Validar contacto (al menos teléfono o email)
        if (phone.isEmpty() && email.isEmpty()) {
            Toast.makeText(this, "Debe proporcionar al menos un teléfono o email", Toast.LENGTH_SHORT).show()
            etPhone.requestFocus()
            return false
        }

        // Validar email si se proporciona
        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El email no tiene un formato válido", Toast.LENGTH_SHORT).show()
            etEmail.requestFocus()
            return false
        }

        // Validar teléfono si se proporciona
        if (phone.isNotEmpty() && phone.length < 10) {
            Toast.makeText(this, "El teléfono debe tener al menos 10 dígitos", Toast.LENGTH_SHORT).show()
            etPhone.requestFocus()
            return false
        }

        // Validar motivo inicial
        if (initialReason.isEmpty()) {
            Toast.makeText(this, "El motivo de consulta inicial es obligatorio", Toast.LENGTH_SHORT).show()
            etInitialReason.requestFocus()
            return false
        }

        if (initialReason.length < 10) {
            Toast.makeText(this, "Describe el motivo de consulta con más detalle (mínimo 10 caracteres)", Toast.LENGTH_SHORT).show()
            etInitialReason.requestFocus()
            return false
        }

        return true
    }

    private fun formatDateForDatabase(displayDate: String): String? {
        return try {
            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val databaseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = displayFormat.parse(displayDate)
            databaseFormat.format(date!!)
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}