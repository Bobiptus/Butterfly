package com.example.butterfly

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.butterfly.database.DatabaseHelper
import com.example.butterfly.database.Patient
import java.text.NumberFormat
import java.util.*

class PatientDetailActivity : Activity() {

    private lateinit var tvPatientName: TextView
    private lateinit var tvPatientAge: TextView
    private lateinit var tvPatientContact: TextView
    private lateinit var tvPatientAddress: TextView
    private lateinit var tvEmergencyContact: TextView
    private lateinit var tvMedicalHistory: TextView
    private lateinit var tvCurrentMedication: TextView
    private lateinit var tvInitialReason: TextView
    private lateinit var tvNotes: TextView

    // Sección de evolución
    private lateinit var tvTotalSessions: TextView
    private lateinit var tvLastSession: TextView
    private lateinit var tvProgressNotes: TextView

    // Sección de pagos
    private lateinit var tvTotalDebt: TextView
    private lateinit var tvLastPayment: TextView
    private lateinit var tvPaymentStatus: TextView

    // Botones de acción
    private lateinit var btnNewSession: Button
    private lateinit var btnCall: Button
    private lateinit var btnWhatsApp: Button
    private lateinit var btnEditPatient: Button
    private lateinit var btnAddPayment: Button

    // RecyclerView para sesiones recientes
    private lateinit var rvRecentSessions: RecyclerView
    private lateinit var tvNoSessions: TextView

    private lateinit var dbHelper: DatabaseHelper
    private var patient: Patient? = null
    private var patientId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        // Obtener ID del paciente del intent
        patientId = intent.getLongExtra(SearchPatientActivity.EXTRA_PATIENT_ID, -1)
        if (patientId == -1L) {
            Toast.makeText(this, "Error: Paciente no encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)

        initViews()
        loadPatientData()
        setupClickListeners()
    }

    private fun initViews() {
        // Información básica
        tvPatientName = findViewById(R.id.tv_patient_name)
        tvPatientAge = findViewById(R.id.tv_patient_age)
        tvPatientContact = findViewById(R.id.tv_patient_contact)
        tvPatientAddress = findViewById(R.id.tv_patient_address)
        tvEmergencyContact = findViewById(R.id.tv_emergency_contact)
        tvMedicalHistory = findViewById(R.id.tv_medical_history)
        tvCurrentMedication = findViewById(R.id.tv_current_medication)
        tvInitialReason = findViewById(R.id.tv_initial_reason)
        tvNotes = findViewById(R.id.tv_notes)

        // Evolución
        tvTotalSessions = findViewById(R.id.tv_total_sessions)
        tvLastSession = findViewById(R.id.tv_last_session)
        tvProgressNotes = findViewById(R.id.tv_progress_notes)

        // Pagos
        tvTotalDebt = findViewById(R.id.tv_total_debt)
        tvLastPayment = findViewById(R.id.tv_last_payment)
        tvPaymentStatus = findViewById(R.id.tv_payment_status)

        // Botones
        btnNewSession = findViewById(R.id.btn_new_session)
        btnCall = findViewById(R.id.btn_call)
        btnWhatsApp = findViewById(R.id.btn_whatsapp)
        btnEditPatient = findViewById(R.id.btn_edit_patient)
        btnAddPayment = findViewById(R.id.btn_add_payment)

        // RecyclerView
        rvRecentSessions = findViewById(R.id.rv_recent_sessions)
        tvNoSessions = findViewById(R.id.tv_no_sessions)

        // Configurar RecyclerView
        rvRecentSessions.layoutManager = LinearLayoutManager(this)
    }

    private fun loadPatientData() {
        patient = dbHelper.getPatientById(patientId)

        if (patient == null) {
            Toast.makeText(this, "Error: No se pudo cargar la información del paciente", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        displayPatientInfo()
        loadEvolutionData()
        loadPaymentData()
        loadRecentSessions()
    }

    private fun displayPatientInfo() {
        patient?.let { p ->
            tvPatientName.text = p.name

            // Edad
            val ageText = p.getAge()?.let { "${it} años" } ?: "Edad no especificada"
            val birthDateText = p.getFormattedBirthDate()?.let { " (${it})" } ?: ""
            tvPatientAge.text = "$ageText$birthDateText"

            // Contacto
            val contactInfo = buildString {
                if (!p.phone.isNullOrBlank()) {
                    append("📱 ${p.phone}")
                    if (!p.email.isNullOrBlank()) append("\n")
                }
                if (!p.email.isNullOrBlank()) {
                    append("📧 ${p.email}")
                }
            }
            tvPatientContact.text = if (contactInfo.isNotEmpty()) contactInfo else "Sin información de contacto"

            // Dirección
            tvPatientAddress.text = if (!p.address.isNullOrBlank()) {
                "🏠 ${p.address}"
            } else {
                "Dirección no especificada"
            }

            // Contacto de emergencia
            val emergencyInfo = buildString {
                if (!p.emergencyContact.isNullOrBlank()) {
                    append("👤 ${p.emergencyContact}")
                    if (!p.emergencyPhone.isNullOrBlank()) {
                        append("\n📞 ${p.emergencyPhone}")
                    }
                }
            }
            tvEmergencyContact.text = if (emergencyInfo.isNotEmpty()) {
                emergencyInfo
            } else {
                "Sin contacto de emergencia"
            }

            // Historial médico
            tvMedicalHistory.text = if (!p.medicalHistory.isNullOrBlank()) {
                p.medicalHistory
            } else {
                "Sin historial médico registrado"
            }

            // Medicación actual
            tvCurrentMedication.text = if (!p.currentMedication.isNullOrBlank()) {
                p.currentMedication
            } else {
                "Sin medicación actual"
            }

            // Motivo inicial
            tvInitialReason.text = p.initialReason

            // Notas
            tvNotes.text = if (!p.notes.isNullOrBlank()) {
                p.notes
            } else {
                "Sin notas adicionales"
            }

            // Habilitar/deshabilitar botones según información disponible
            btnCall.isEnabled = !p.phone.isNullOrBlank()
            btnWhatsApp.isEnabled = !p.phone.isNullOrBlank()
        }
    }

    private fun loadEvolutionData() {
        // TODO: Implementar cuando tengamos la tabla de sesiones
        // Por ahora, datos de ejemplo
        tvTotalSessions.text = "0 sesiones"
        tvLastSession.text = "Sin sesiones registradas"
        tvProgressNotes.text = "Aún no hay seguimiento de evolución"
    }

    private fun loadPaymentData() {
        // TODO: Implementar cuando tengamos la tabla de pagos
        // Por ahora, datos de ejemplo
        val currency = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        tvTotalDebt.text = currency.format(0.0)
        tvLastPayment.text = "Sin pagos registrados"
        tvPaymentStatus.text = "Al corriente"

        // Color del estado de pago
        tvPaymentStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
    }

    private fun loadRecentSessions() {
        // TODO: Implementar cuando tengamos la tabla de sesiones
        // Por ahora mostrar estado vacío
        rvRecentSessions.visibility = View.GONE
        tvNoSessions.visibility = View.VISIBLE
    }

    private fun setupClickListeners() {
        btnNewSession.setOnClickListener {
            // TODO: Implementar CreateSessionActivity
            Toast.makeText(this, "Funcionalidad de nueva sesión próximamente", Toast.LENGTH_SHORT).show()
        }

        btnCall.setOnClickListener {
            patient?.phone?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                startActivity(intent)
            }
        }

        btnWhatsApp.setOnClickListener {
            patient?.phone?.let { phone ->
                try {
                    val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/52$cleanPhone"))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al abrir WhatsApp", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnEditPatient.setOnClickListener {
            // TODO: Implementar EditPatientActivity
            Toast.makeText(this, "Funcionalidad de editar paciente próximamente", Toast.LENGTH_SHORT).show()
        }

        btnAddPayment.setOnClickListener {
            showAddPaymentDialog()
        }

        // Botón de atrás
        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun showAddPaymentDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_payment, null)

        val etAmount = view.findViewById<EditText>(R.id.et_payment_amount)
        val etConcept = view.findViewById<EditText>(R.id.et_payment_concept)
        val spPaymentMethod = view.findViewById<Spinner>(R.id.sp_payment_method)

        // Configurar spinner de método de pago
        val paymentMethods = arrayOf("Efectivo", "Tarjeta", "Transferencia", "Otro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paymentMethods)
        spPaymentMethod.adapter = adapter

        builder.setView(view)
            .setTitle("Registrar Pago")
            .setPositiveButton("Guardar") { _, _ ->
                val amount = etAmount.text.toString().trim()
                val concept = etConcept.text.toString().trim()
                val method = spPaymentMethod.selectedItem.toString()

                if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
                    // TODO: Guardar pago en base de datos
                    Toast.makeText(this, "Pago de \$${amount} registrado", Toast.LENGTH_SHORT).show()
                    loadPaymentData() // Recargar datos de pago
                } else {
                    Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}