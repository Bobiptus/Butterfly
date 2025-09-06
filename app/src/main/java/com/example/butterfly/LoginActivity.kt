package com.example.butterfly  // ← Cambia por tu paquete real

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : Activity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_register)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
    }

    private fun setupClickListeners() {
        // Botón de login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateLogin(email, password)) {
                performLogin(email, password)
            }
        }

        // Olvidé contraseña
        tvForgotPassword.setOnClickListener {
            // Por ahora solo mostrar un Toast
            Toast.makeText(this, "Función de recuperar contraseña", Toast.LENGTH_SHORT).show()

            // Cuando tengas la pantalla de recuperación:
            // startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Crear cuenta
        btnRegister.setOnClickListener {
            Toast.makeText(this, "Función de registro", Toast.LENGTH_SHORT).show()

            // Cuando tengas la pantalla de registro:
            // startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu email", Toast.LENGTH_SHORT).show()
            etEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingresa un email válido", Toast.LENGTH_SHORT).show()
            etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
            etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        // Mostrar mensaje de carga
        Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()

        // Simulación de login (reemplazar con tu lógica real)
        // Por ejemplo: validación con base de datos, API, etc.

        // Login de prueba (QUITAR EN PRODUCCIÓN)
        if (email == "test@test.com" && password == "123456") {
            Toast.makeText(this, "¡Login exitoso!", Toast.LENGTH_SHORT).show()

            // Navegar a la pantalla principal
            // startActivity(Intent(this, MainActivity::class.java))
            // finish()

        } else {
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }

        // Aquí implementarías tu lógica real de autenticación:
        /*
        // Ejemplo con una API
        val loginRequest = LoginRequest(email, password)
        apiService.login(loginRequest) { response ->
            if (response.success) {
                // Guardar token, navegar a MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }
        */
    }
}