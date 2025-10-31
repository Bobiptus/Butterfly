package com.example.butterfly

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import com.example.butterfly.database.DatabaseHelper
import java.text.DecimalFormat

class UserSettingsActivity : Activity() {

    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvDatabaseSize: TextView
    private lateinit var tvTotalPatients: TextView

    private lateinit var btnChangePassword: Button
    private lateinit var btnEditProfile: Button
    private lateinit var btnExportData: Button
    private lateinit var btnBackupDatabase: Button
    private lateinit var btnAbout: Button
    private lateinit var btnLogout: Button

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)

        dbHelper = DatabaseHelper(this)

        initViews()
        loadUserInfo()
        setupClickListeners()
    }

    private fun initViews() {
        tvUserEmail = findViewById(R.id.tv_user_email)
        tvUserName = findViewById(R.id.tv_user_name)
        tvDatabaseSize = findViewById(R.id.tv_database_size)
        tvTotalPatients = findViewById(R.id.tv_total_patients)

        btnChangePassword = findViewById(R.id.btn_change_password)
        btnEditProfile = findViewById(R.id.btn_edit_profile)
        btnExportData = findViewById(R.id.btn_export_data)
        btnBackupDatabase = findViewById(R.id.btn_backup_database)
        btnAbout = findViewById(R.id.btn_about)
        btnLogout = findViewById(R.id.btn_logout)
    }

    private fun loadUserInfo() {
        val userEmail = LoginActivity.getCurrentUserEmail(this)
        if (userEmail != null) {
            tvUserEmail.text = userEmail

            val user = dbHelper.getUserByEmail(userEmail)
            if (user != null) {
                tvUserName.text = user.getDisplayName()
            }
        }

        updateDatabaseInfo()
    }

    private fun updateDatabaseInfo() {
        val sizeInBytes = dbHelper.getDatabaseSize()
        val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
        val df = DecimalFormat("#.##")
        tvDatabaseSize.text = "${df.format(sizeInMB)} MB"

        val userEmail = LoginActivity.getCurrentUserEmail(this)
        if (userEmail != null) {
            val patients = dbHelper.getPatientsByPsychologist(userEmail)
            tvTotalPatients.text = "${patients.size} pacientes"
        } else {
            tvTotalPatients.text = "0 pacientes"
        }
    }

    private fun setupClickListeners() {
        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        btnExportData.setOnClickListener {
            showExportDialog()
        }

        btnBackupDatabase.setOnClickListener {
            performBackup()
        }

        btnAbout.setOnClickListener {
            showAboutDialog()
        }

        btnLogout.setOnClickListener {
            confirmLogout()
        }
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_change_password, null)

        val etOldPassword = view.findViewById<EditText>(R.id.et_old_password)
        val etNewPassword = view.findViewById<EditText>(R.id.et_new_password)
        val etConfirmPassword = view.findViewById<EditText>(R.id.et_confirm_password)

        builder.setView(view)
            .setTitle("Cambiar Contraseña")
            .setPositiveButton("Guardar", DialogInterface.OnClickListener { dialog, which ->
                val oldPassword = etOldPassword.text.toString().trim()
                val newPassword = etNewPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()

                if (validatePasswordChange(oldPassword, newPassword, confirmPassword)) {
                    val userEmail = LoginActivity.getCurrentUserEmail(this)
                    if (userEmail != null) {
                        if (dbHelper.authenticateUser(userEmail, oldPassword)) {
                            if (dbHelper.changePassword(userEmail, newPassword)) {
                                Toast.makeText(this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Contraseña antigua incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
            .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .show()
    }

    private fun validatePasswordChange(oldPassword: String, newPassword: String, confirmPassword: String): Boolean {
        if (oldPassword.isEmpty()) {
            Toast.makeText(this, "Ingresa tu contraseña actual", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Ingresa tu nueva contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword == oldPassword) {
            Toast.makeText(this, "La nueva contraseña debe ser diferente a la actual", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun showEditProfileDialog() {
        val userEmail = LoginActivity.getCurrentUserEmail(this)
        if (userEmail == null) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        val user = dbHelper.getUserByEmail(userEmail)
        if (user == null) {
            Toast.makeText(this, "Error: No se pudo cargar el perfil", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_edit_profile, null)

        val etName = view.findViewById<EditText>(R.id.et_profile_name)
        etName.setText(user.name ?: "")

        builder.setView(view)
            .setTitle("Editar Perfil")
            .setPositiveButton("Guardar", DialogInterface.OnClickListener { dialog, which ->
                val newName = etName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    Toast.makeText(this, "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            })
            .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .show()
    }

    private fun showExportDialog() {
        val builder = AlertDialog.Builder(this)
        val options = arrayOf("Exportar como JSON", "Exportar como PDF")

        builder.setTitle("Selecciona formato de exportación")
            .setItems(options, DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> exportDataAsJson()
                    1 -> Toast.makeText(this, "Exportación a PDF en desarrollo", Toast.LENGTH_SHORT).show()
                }
            })
            .show()
    }

    private fun exportDataAsJson() {
        val userEmail = LoginActivity.getCurrentUserEmail(this)
        if (userEmail == null) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Exportación en desarrollo", Toast.LENGTH_SHORT).show()
    }

    private fun performBackup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Realizar Respaldo")
            .setMessage("¿Deseas crear un respaldo de todos tus datos?\n\nEsto incluye pacientes, sesiones y pagos.")
            .setPositiveButton("Sí, crear respaldo", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(this, "Respaldo en desarrollo", Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .show()
    }

    private fun showAboutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Acerca de Butterfly")
            .setMessage("Butterfly v1.0.0\n\n" +
                    "Sistema de Gestión Psicológica\n\n" +
                    "Diseñado para psicólogos profesionales para facilitar la gestión de pacientes, sesiones y seguimiento clínico.\n\n" +
                    "© 2024 Butterfly. Todos los derechos reservados.\n\n" +
                    "Desarrollado con Kotlin y Android Studio.")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .show()
    }

    private fun confirmLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí, cerrar sesión", DialogInterface.OnClickListener { dialog, which ->
                return@OnClickListener
            })
            .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}