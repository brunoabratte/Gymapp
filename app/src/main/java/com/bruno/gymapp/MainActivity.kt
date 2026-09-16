package com.bruno.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bruno.gymapp.data.local.AppDatabase
import com.bruno.gymapp.data.repository.GymRepository
import com.bruno.gymapp.ui.screens.exercises.EjerciciosScreen
import com.bruno.gymapp.ui.screens.exercises.EjerciciosViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val repo = GymRepository(db)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    GymAppNavHost(repo)
                }
            }
        }
    }
}

@Composable
fun GymAppNavHost(repo: GymRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "ejercicios") {
        composable("ejercicios") {
            val viewModel = viewModel(factory = EjerciciosViewModelFactory(repo)) as com.bruno.gymapp.ui.screens.exercises.EjerciciosViewModel
            EjerciciosScreen(
                viewModel = viewModel,
                onEjercicioClick = { ejercicio ->
                    // TODO(Bruno): navegar a pantalla de detalle/progreso de este ejercicio
                    // navController.navigate("progreso/${ejercicio.id}")
                }
            )
        }
        // TODO(Bruno): agregar acá las rutas "sesion" (registrar serie) e "historial"
    }
}
