package com.example.butterfly

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.butterfly.database.DatabaseHelper
import com.example.butterfly.database.User
import com.example.butterfly.ui.theme.ButterflyTheme

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        // Verificar si el usuario está logueado
        if (!LoginActivity.isUserLoggedIn(this)) {
            redirectToLogin()
            return
        }

        // Obtener datos del usuario actual
        val userEmail = LoginActivity.getCurrentUserEmail(this)
        if (userEmail != null) {
            currentUser = dbHelper.getUserByEmail(userEmail)
        }

        enableEdgeToEdge()
        setContent {
            ButterflyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MedicalDashboard(
                        user = currentUser,
                        onLogout = { performLogout() },
                        onMenuItemClick = { menuItem -> handleMenuClick(menuItem) }
                    )
                }
            }
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun performLogout() {
        LoginActivity.performLogout(this)
    }

    private fun handleMenuClick(menuItem: MenuOption) {
        // TODO: Implementar navegación a las diferentes pantallas
        when (menuItem) {
            MenuOption.CREATE_PATIENT -> {
                // Intent intent = new Intent(this, CreatePatientActivity.class);
                // startActivity(intent);
            }
            MenuOption.SEARCH_PATIENT -> {
                // Intent intent = new Intent(this, SearchPatientActivity.class);
                // startActivity(intent);
            }
            MenuOption.SCHEDULE_VIEW -> {
                // Intent intent = new Intent(this, ScheduleActivity.class);
                // startActivity(intent);
            }
            MenuOption.CREATE_SESSION -> {
                // Intent intent = new Intent(this, CreateSessionActivity.class);
                // startActivity(intent);
            }
            MenuOption.SESSION_NOTES -> {
                // Intent intent = new Intent(this, SessionNotesActivity.class);
                // startActivity(intent);
            }
            MenuOption.PSYCHOLOGICAL_TESTS -> {
                // Intent intent = new Intent(this, PsychTestsActivity.class);
                // startActivity(intent);
            }
            MenuOption.PROGRESS_REPORTS -> {
                // Intent intent = new Intent(this, ProgressReportsActivity.class);
                // startActivity(intent);
            }
            MenuOption.BILLING -> {
                // Intent intent = new Intent(this, BillingActivity.class);
                // startActivity(intent);
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
    }
}

// Enum para las opciones del menú psicológico
enum class MenuOption(val title: String, val description: String, val icon: ImageVector) {
    CREATE_PATIENT("Nuevo Paciente", "Registrar nuevo paciente en consulta", Icons.Default.Person),
    SEARCH_PATIENT("Consultar Paciente", "Buscar y ver información de pacientes", Icons.Default.Search),
    SCHEDULE_VIEW("Agenda", "Ver calendario de sesiones programadas", Icons.Default.DateRange),
    CREATE_SESSION("Nueva Sesión", "Programar sesión psicológica", Icons.Default.Add),
    SESSION_NOTES("Notas de Sesión", "Gestionar notas y evolución clínica", Icons.Default.Edit),
    PSYCHOLOGICAL_TESTS("Tests Psicológicos", "Aplicar y gestionar evaluaciones", Icons.Default.Check),
    PROGRESS_REPORTS("Evolución", "Seguimiento del progreso terapéutico", Icons.Default.Info),
    BILLING("Facturación", "Gestión de pagos y honorarios", Icons.Default.Notifications)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDashboard(
    user: User?,
    onLogout: () -> Unit,
    onMenuItemClick: (MenuOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header con información del usuario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sistema Psicológico Butterfly",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bienvenido, ${user?.getDisplayName() ?: "Psicólogo"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        // Grid de opciones principales con diseño 3x3 para 8 opciones
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(MenuOption.values().toList().chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { menuItem ->
                        MenuCard(
                            menuOption = menuItem,
                            onClick = { onMenuItemClick(menuItem) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Si solo hay un elemento en la fila, agregar un spacer
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de cerrar sesión
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Cerrar Sesión",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(
    menuOption: MenuOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = menuOption.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = menuOption.title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = menuOption.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicalDashboardPreview() {
    ButterflyTheme {
        MedicalDashboard(
            user = User(
                id = 1,
                email = "psicologo@consulta.com",
                name = "Psic. María González",
                createdAt = "2024-01-01",
                lastLogin = "Hace unos minutos"
            ),
            onLogout = { },
            onMenuItemClick = { }
        )
    }
}