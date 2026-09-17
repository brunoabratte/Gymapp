package com.bruno.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bruno.gymapp.data.ConfiguracionEntrenamiento
import com.bruno.gymapp.data.local.AppDatabase
import com.bruno.gymapp.data.local.entity.PlanesPreconfigurados
import com.bruno.gymapp.data.repository.GymRepository
import com.bruno.gymapp.ui.screens.exercises.*
import com.bruno.gymapp.ui.screens.history.*
import com.bruno.gymapp.ui.screens.home.*
import com.bruno.gymapp.ui.screens.plans.PlanesScreen
import com.bruno.gymapp.ui.screens.progress.*
import com.bruno.gymapp.ui.screens.session.*
import com.bruno.gymapp.ui.screens.settings.ConfiguracionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(applicationContext)
        val repo = GymRepository(db)
        val config = ConfiguracionEntrenamiento(applicationContext)
        setContent { MaterialTheme { Surface { GymAppNavHost(repo, config) } } }
    }
}

@Composable
fun GymAppNavHost(repo: GymRepository, config: ConfiguracionEntrenamiento) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "inicio") {
        composable("inicio") {
            val vm: HomeViewModel = viewModel(factory = HomeViewModelFactory(repo, config))
            HomeScreen(vm,
                onEntrenar = { planId -> navController.navigate("nueva_sesion/$planId") },
                onContinuar = { id, planId -> navController.navigate("sesion/$id/$planId") },
                onConfig = { navController.navigate("configuracion") },
                onEjercicios = { navController.navigate("ejercicios") },
                onHistorial = { navController.navigate("historial") },
                onProgreso = { }
            )
        }
        composable("configuracion") { ConfiguracionScreen(config) { navController.popBackStack() } }
        composable("ejercicios") {
            val vm: EjerciciosViewModel = viewModel(factory = EjerciciosViewModelFactory(repo))
            EjerciciosScreen(vm, { navController.navigate("progreso/${it.id}") }, { navController.navigate("planes") }, { navController.navigate("historial") })
        }
        composable("planes") { PlanesScreen { navController.navigate("nueva_sesion/$it") } }
        composable("nueva_sesion/{planId}", arguments = listOf(navArgument("planId") { type = NavType.StringType })) { entry ->
            val planId = entry.arguments?.getString("planId") ?: "libre"
            val plan = if (planId == "libre") null else PlanesPreconfigurados.porId(planId)
            androidx.compose.runtime.LaunchedEffect(planId) {
                val id = repo.iniciarSesion(System.currentTimeMillis(), plan?.nombre ?: "Entrenamiento libre", planId)
                navController.navigate("sesion/$id/$planId") { popUpTo("nueva_sesion/$planId") { inclusive = true } }
            }
        }
        composable("sesion/{sesionId}/{planId}", arguments = listOf(navArgument("sesionId") { type = NavType.LongType }, navArgument("planId") { type = NavType.StringType })) { entry ->
            val id = entry.arguments?.getLong("sesionId") ?: return@composable
            val planId = entry.arguments?.getString("planId") ?: "libre"
            val vm: SesionViewModel = viewModel(factory = SesionViewModelFactory(repo, id, planId))
            SesionScreen(vm) { navController.popBackStack("inicio", false) }
        }
        composable("progreso/{ejercicioId}", arguments = listOf(navArgument("ejercicioId") { type = NavType.LongType })) { entry ->
            val id = entry.arguments?.getLong("ejercicioId") ?: return@composable
            val vm: ProgresoViewModel = viewModel(factory = ProgresoViewModelFactory(repo, id))
            ProgresoScreen(vm)
        }
        composable("historial") {
            val vm: HistorialViewModel = viewModel(factory = HistorialViewModelFactory(repo))
            HistorialScreen(vm) { navController.navigate("historial/$it") }
        }
        composable("historial/{sesionId}", arguments = listOf(navArgument("sesionId") { type = NavType.LongType })) { entry ->
            val id = entry.arguments?.getLong("sesionId") ?: return@composable
            val vm: HistorialDetalleViewModel = viewModel(factory = HistorialDetalleViewModelFactory(repo, id))
            HistorialDetalleScreen(vm)
        }
    }
}
