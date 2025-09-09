package com.example.butterfly

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.butterfly.database.DatabaseHelper

class RegisterActivity : Activity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvBackToLogin: TextView

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_register_name)
        etEmail = findViewById(R.id.et_register_email)
        etPassword = findViewById(R.id.et_register_password)
        etConfirmPassword = findViewById(R.id.et_register_confirm_password)
        btnRegister = findViewById(R.id.btn_register_submit)
        tvBackToLogin = findViewById(R.id.tv_back_to_login)
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            performRegistration()
        }

        tvBackToLogin.setOnClickListener {
            finish() // Regresa al LoginActivity
        }
    }

    private fun performRegistration() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validar campos
        if (!validateRegistration(name, email, password, confirmPassword)) {
            return
        }

        // Intentar registrar al usuario
        val isRegistered = dbHelper.registerUser(email, password, name)

        if (isRegistered) {
            Toast.makeText(this, "¡Registro exitoso! Ahora puedes iniciar sesión", Toast.LENGTH_LONG).show()
            finish() // Regresar al login
        } else {
            Toast.makeText(this, "Error: Este email ya está registrado", Toast.LENGTH_LONG).show()
            etEmail.requestFocus()
        }
    }

    private fun validateRegistration(name: String, email: String, password: String, confirmPassword: String): Boolean {
        // Validar nombre
        if (name.isEmpty()) {
            Toast.makeText(this, "Ingresa tu nombre", Toast.LENGTH_SHORT).show()
            etName.requestFocus()
            return false
        }

        if (name.length < 2) {
            Toast.makeText(this, "El nombre debe tener al menos 2 caracteres", Toast.LENGTH_SHORT).show()
            etName.requestFocus()
            return false
        }

        // Validar email
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

        // Validar contraseña
        if (password.isEmpty()) {
            Toast.makeText(this, "Ingresa una contraseña", Toast.LENGTH_SHORT).show()
            etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            etPassword.requestFocus()
            return false
        }

        // Validar confirmación de contraseña
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Confirma tu contraseña", Toast.LENGTH_SHORT).show()
            etConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}