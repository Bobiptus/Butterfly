package com.example.butterfly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvCurrentDate: TextView
    private lateinit var cardAddPatient: LinearLayout
    private lateinit var cardCalendar: LinearLayout
    private lateinit var cardDailyActivities: LinearLayout
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
        loadUserInfo()
        updateCurrentDate()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tv_welcome)
        tvCurrentDate = findViewById(R.id.tv_current_date)
        cardAddPatient = findViewById(R.id.card_add_patient)
        cardCalendar = findViewById(R.id.card_calendar)
        cardDailyActivities = findViewById(R.id.card_daily_activities)
        btnLogout = findViewById(R.id.btn_logout)
    }

    private fun setupClickListeners() {
        cardAddPatient.setOnClickListener {
            val intent = Intent(this, CreatePatientActivity::class.java)
            startActivity(intent)
        }

        cardCalendar.setOnClickListener {
            Toast.makeText(this, "Calendario - Próximamente", Toast.LENGTH_SHORT).show()
        }

        cardDailyActivities.setOnClickListener {
            Toast.makeText(this, "Actividades del día - Próximamente", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadUserInfo() {
        val userEmail = LoginActivity.getCurrentUserEmail(this)
        if (userEmail != null) {
            val userName = userEmail.substringBefore("@")
            tvWelcome.text = "¡Bienvenido, ${userName.capitalize()}!"
        } else {
            tvWelcome.text = "¡Bienvenido!"
        }
    }

    private fun updateCurrentDate() {
        val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        tvCurrentDate.text = sdf.format(Date())
    }

    private fun showLogoutDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()
    }

    private fun logout() {
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        updateCurrentDate()
    }
}