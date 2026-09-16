package com.bruno.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bruno.gymapp.data.local.AppDatabase
import com.bruno.gymapp.data.repository.GymRepository
import com.bruno.gymapp.ui.screens.exercises.EjerciciosScreen
import com.bruno.gymapp.ui.screens.exercises.EjerciciosViewModel
import com.bruno.gymapp.ui.screens.exercises.EjerciciosViewModelFactory
import com.bruno.gymapp.ui.screens.session.SesionScreen
import com.bruno.gymapp.ui.screens.session.SesionViewModel
import com.bruno.gymapp.ui.screens.session.SesionViewModelFactory

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
            val viewModel: EjerciciosViewModel =
                viewModel(factory = EjerciciosViewModelFactory(repo))
            EjerciciosScreen(
                viewModel = viewModel,
                onEjercicioClick = { ejercicio ->
                    // TODO(Bruno): navegar a pantalla de detalle/progreso de este ejercicio
                    // navController.navigate("progreso/${ejercicio.id}")
                },
                onIniciarSesion = {
                    navController.navigate("nueva_sesion")
                }
            )
        }

        // Ruta "puente": crea la sesión en la base y navega a la pantalla real con su id ya generado.
        composable("nueva_sesion") {
            LaunchedEffect(Unit) {
                val sesionId = repo.iniciarSesion(System.currentTimeMillis())
                navController.navigate("sesion/$sesionId") {
                    popUpTo("nueva_sesion") { inclusive = true }
                }
            }
        }

        composable(
            route = "sesion/{sesionId}",
            arguments = listOf(navArgument("sesionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sesionId = backStackEntry.arguments?.getLong("sesionId") ?: return@composable
            val viewModel: SesionViewModel =
                viewModel(factory = SesionViewModelFactory(repo, sesionId))
            SesionScreen(
                viewModel = viewModel,
                onFinalizar = {
                    navController.popBackStack("ejercicios", inclusive = false)
                }
            )
        }

        // TODO(Bruno): agregar acá la ruta "historial" de sesiones pasadas
    }
}
