package com.example.butterfly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.butterfly.database.DatabaseHelper
import com.example.butterfly.database.Patient

class SearchPatientActivity : Activity() {

    private lateinit var etSearch: EditText
    private lateinit var rvPatients: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var btnAddPatient: Button

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var patientsAdapter: PatientsAdapter
    private var allPatients: List<Patient> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_patient)

        dbHelper = DatabaseHelper(this)

        initViews()
        setupRecyclerView()
        setupSearchFunctionality()
        loadPatients()
    }

    private fun initViews() {
        etSearch = findViewById(R.id.et_search_patient)
        rvPatients = findViewById(R.id.rv_patients)
        tvEmptyState = findViewById(R.id.tv_empty_state)
        btnAddPatient = findViewById(R.id.btn_add_patient)

        btnAddPatient.setOnClickListener {
            val intent = Intent(this, CreatePatientActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_CREATE_PATIENT)
        }
    }

    private fun showPatientDetail(patient: Patient) {
        val intent = Intent(this, PatientDetailActivity::class.java)
        intent.putExtra(EXTRA_PATIENT_ID, patient.id)
        startActivityForResult(intent, REQUEST_CODE_PATIENT_DETAIL)
    }

    private fun setupRecyclerView() {
        patientsAdapter = PatientsAdapter(emptyList()) { patient ->
            // Cuando se hace clic en un paciente, mostrar su detalle
            showPatientDetail(patient)
        }

        rvPatients.apply {
            layoutManager = LinearLayoutManager(this@SearchPatientActivity)
            adapter = patientsAdapter
        }
    }

    private fun setupSearchFunctionality() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    // Mostrar todos los pacientes
                    updatePatientsList(allPatients)
                } else {
                    // Buscar pacientes
                    searchPatients(query)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadPatients() {
        val psychologistEmail = LoginActivity.getCurrentUserEmail(this)
        if (psychologistEmail != null) {
            allPatients = dbHelper.getPatientsByPsychologist(psychologistEmail)
            updatePatientsList(allPatients)
        } else {
            Toast.makeText(this, "Error: No se pudo identificar al usuario", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun searchPatients(query: String) {
        val psychologistEmail = LoginActivity.getCurrentUserEmail(this)
        if (psychologistEmail != null) {
            val searchResults = dbHelper.searchPatients(query, psychologistEmail)
            updatePatientsList(searchResults)
        }
    }

    private fun updatePatientsList(patients: List<Patient>) {
        patientsAdapter.updatePatients(patients)

        if (patients.isEmpty()) {
            rvPatients.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = if (etSearch.text.toString().trim().isEmpty()) {
                "No tienes pacientes registrados.\nToca 'Agregar Paciente' para comenzar."
            } else {
                "No se encontraron pacientes con el término:\n\"${etSearch.text}\""
            }
        } else {
            rvPatients.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CREATE_PATIENT -> {
                    // Recargar la lista después de crear un paciente
                    loadPatients()
                    Toast.makeText(this, "Paciente creado exitosamente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }

    companion object {
        private const val REQUEST_CODE_CREATE_PATIENT = 1001
        private const val REQUEST_CODE_PATIENT_DETAIL = 1002
        const val EXTRA_PATIENT_ID = "extra_patient_id"
    }
}

// Adapter para RecyclerView de pacientes
class PatientsAdapter(
    private var patients: List<Patient>,
    private val onPatientClick: (Patient) -> Unit
) : RecyclerView.Adapter<PatientsAdapter.PatientViewHolder>() {

    fun updatePatients(newPatients: List<Patient>) {
        patients = newPatients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        holder.bind(patients[position], onPatientClick)
    }

    override fun getItemCount(): Int = patients.size

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_patient_name)
        private val tvSummary: TextView = itemView.findViewById(R.id.tv_patient_summary)
        private val tvLastUpdate: TextView = itemView.findViewById(R.id.tv_last_update)
        private val tvInitialReason: TextView = itemView.findViewById(R.id.tv_initial_reason)

        fun bind(patient: Patient, onPatientClick: (Patient) -> Unit) {
            tvName.text = patient.name
            tvSummary.text = patient.getSummary()

            // Mostrar fecha de última actualización o creación
            val lastUpdate = patient.updatedAt ?: patient.createdAt ?: "Sin fecha"
            tvLastUpdate.text = "Actualizado: $lastUpdate"

            // Mostrar motivo inicial truncado
            val reason = if (patient.initialReason.length > 80) {
                patient.initialReason.substring(0, 80) + "..."
            } else {
                patient.initialReason
            }
            tvInitialReason.text = reason

            itemView.setOnClickListener {
                onPatientClick(patient)
            }
        }
    }
}