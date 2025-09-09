package com.example.butterfly

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.butterfly.database.DatabaseHelper
import com.example.butterfly.database.User

class LoginActivity : Activity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var cbRememberMe: CheckBox

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        initViews()
        setupClickListeners()
        loadRememberedData()

        // Verificar si ya hay una sesión activa
        checkExistingSession()
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
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateLogin(email, password)) {
                performLogin(email, password)
            }
        }

        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
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
        Toast.makeText(this, "Validando credenciales...", Toast.LENGTH_SHORT).show()

        // Autenticar con la base de datos
        val isAuthenticated = dbHelper.authenticateUser(email, password)

        if (isAuthenticated) {
            // Obtener información del usuario
            val user = dbHelper.getUserByEmail(email)

            if (user != null) {
                // Guardar sesión
                saveLoginSession(user)

                // Manejar "recordarme"
                if (cbRememberMe.isChecked) {
                    saveRememberedData(email)
                } else {
                    clearRememberedData()
                }

                Toast.makeText(this, "¡Bienvenido ${user.getDisplayName()}!", Toast.LENGTH_SHORT).show()

                // Navegar a MainActivity
                navigateToMainActivity()
            } else {
                Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show()
            etPassword.setText("") // Limpiar contraseña
            etPassword.requestFocus()
        }
    }

    private fun showForgotPasswordDialog() {
        // Por ahora mostrar un mensaje simple
        Toast.makeText(this, "Función de recuperar contraseña próximamente", Toast.LENGTH_LONG).show()

        // TODO: Implementar recuperación de contraseña
    }

    private fun saveLoginSession(user: User) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_EMAIL, user.email)
            apply()
        }
    }

    private fun saveRememberedData(email: String) {
        sharedPreferences.edit().apply {
            putString(KEY_REMEMBER_EMAIL, email)
            apply()
        }
    }

    private fun clearRememberedData() {
        sharedPreferences.edit().apply {
            remove(KEY_REMEMBER_EMAIL)
            apply()
        }
    }

    private fun loadRememberedData() {
        val rememberedEmail = sharedPreferences.getString(KEY_REMEMBER_EMAIL, "")
        if (!rememberedEmail.isNullOrEmpty()) {
            etEmail.setText(rememberedEmail)
            cbRememberMe.isChecked = true
            etPassword.requestFocus()
        }
    }

    private fun checkExistingSession() {
        val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        if (isLoggedIn) {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }

    // Companion object con constantes y métodos estáticos
    companion object {
        private const val PREFS_NAME = "butterfly_prefs"
        private const val KEY_REMEMBER_EMAIL = "remember_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"

        // Método para obtener email del usuario logueado
        fun getCurrentUserEmail(activity: Activity): String? {
            val prefs = activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)
            return prefs.getString(KEY_USER_EMAIL, null)
        }

        // Método para verificar si hay usuario logueado
        fun isUserLoggedIn(activity: Activity): Boolean {
            val prefs = activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)
            return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        }

        // Método para cerrar sesión (llamar desde otras actividades)
        fun performLogout(activity: Activity) {
            val prefs = activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, false)
                remove(KEY_USER_EMAIL)
                apply()
            }

            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
            if (activity is Activity) {
                activity.finish()
            }
        }
    }
}