package com.example.butterfly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.example.butterfly.database.DatabaseHelper

class LoginActivity : Activity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var cbRememberMe: CheckBox

    private lateinit var dbHelper: DatabaseHelper

    companion object {
        private const val PREFS_NAME = "ButterflyPrefs"
        private const val KEY_USER_EMAIL = "user_email"

        fun getCurrentUserEmail(activity: Activity): String? {
            val prefs = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            return prefs.getString(KEY_USER_EMAIL, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)
        initViews()
        setupClickListeners()
        checkRememberedUser()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        cbRememberMe = findViewById(R.id.cb_remember_me)
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            attemptLogin()
        }

        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun checkRememberedUser() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val rememberedEmail = prefs.getString(KEY_USER_EMAIL, null)

        if (rememberedEmail != null) {
            etEmail.setText(rememberedEmail)
            cbRememberMe.isChecked = true
        }
    }

    private fun attemptLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validaciones
        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu email", Toast.LENGTH_SHORT).show()
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu contraseña", Toast.LENGTH_SHORT).show()
            etPassword.requestFocus()
            return
        }

        // Autenticar
        if (dbHelper.authenticateUser(email, password)) {
            // Guardar email si "recordarme" está marcado
            if (cbRememberMe.isChecked) {
                saveUserEmail(email)
            } else {
                clearSavedEmail()
            }

            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()

            // Ir a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show()
        }
    }

    private fun showForgotPasswordDialog() {
        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.hint = "tu@email.com"

        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Recuperar Contraseña")
            .setMessage("Ingresa tu correo electrónico:")
            .setView(input)
            .setPositiveButton("Enviar") { _, _ ->
                val email = input.text.toString().trim()
                if (email.isEmpty()) {
                    Toast.makeText(this, "Ingresa tu email", Toast.LENGTH_SHORT).show()
                } else if (dbHelper.isEmailExists(email)) {
                    Toast.makeText(
                        this,
                        "Se ha enviado un enlace de recuperación a $email\n(Funcionalidad en desarrollo)",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Email no registrado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun saveUserEmail(email: String) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    private fun clearSavedEmail() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().remove(KEY_USER_EMAIL).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}