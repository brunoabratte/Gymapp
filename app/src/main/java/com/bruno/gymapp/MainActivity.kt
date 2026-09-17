package com.bruno.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.bruno.gymapp.data.ConfiguracionEntrenamiento
import com.bruno.gymapp.data.local.AppDatabase
import com.bruno.gymapp.data.local.entity.Ejercicio
import com.bruno.gymapp.data.local.entity.PlanesPreconfigurados
import com.bruno.gymapp.data.repository.GymRepository

import com.bruno.gymapp.ui.screens.exercises.EjerciciosScreen
import com.bruno.gymapp.ui.screens.exercises.EjerciciosViewModel
import com.bruno.gymapp.ui.screens.exercises.EjerciciosViewModelFactory

import com.bruno.gymapp.ui.screens.history.HistorialDetalleScreen
import com.bruno.gymapp.ui.screens.history.HistorialDetalleViewModel
import com.bruno.gymapp.ui.screens.history.HistorialDetalleViewModelFactory
import com.bruno.gymapp.ui.screens.history.HistorialScreen
import com.bruno.gymapp.ui.screens.history.HistorialViewModel
import com.bruno.gymapp.ui.screens.history.HistorialViewModelFactory

import com.bruno.gymapp.ui.screens.home.HomeScreen
import com.bruno.gymapp.ui.screens.home.HomeViewModel
import com.bruno.gymapp.ui.screens.home.HomeViewModelFactory

import com.bruno.gymapp.ui.screens.plans.PlanesScreen

import com.bruno.gymapp.ui.screens.progress.ProgresoScreen
import com.bruno.gymapp.ui.screens.progress.ProgresoViewModel
import com.bruno.gymapp.ui.screens.progress.ProgresoViewModelFactory

import com.bruno.gymapp.ui.screens.session.SesionScreen
import com.bruno.gymapp.ui.screens.session.SesionViewModel
import com.bruno.gymapp.ui.screens.session.SesionViewModelFactory

import com.bruno.gymapp.ui.screens.settings.ConfiguracionScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val repo = GymRepository(db)
        val config = ConfiguracionEntrenamiento(applicationContext)

        setContent {
            MaterialTheme {
                Surface {
                    GymAppNavHost(repo, config)
                }
            }
        }
    }
}


@Composable
fun GymAppNavHost(
    repo: GymRepository,
    config: ConfiguracionEntrenamiento
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        // ------------------------------------------------------------
        // INICIO
        // ------------------------------------------------------------

        composable("inicio") {

            val vm: HomeViewModel =
                viewModel(
                    factory = HomeViewModelFactory(repo, config)
                )

            HomeScreen(
                vm,

                onEntrenar = { planId ->
                    navController.navigate("nueva_sesion/$planId")
                },

                onContinuar = { id, planId ->
                    navController.navigate("sesion/$id/$planId")
                },

                onConfig = {
                    navController.navigate("configuracion")
                },

                onEjercicios = {
                    navController.navigate("ejercicios")
                },

                onHistorial = {
                    navController.navigate("historial")
                },

                onProgreso = {
                    // Por ahora no hace nada
                }
            )
        }


        // ------------------------------------------------------------
        // CONFIGURACION
        // ------------------------------------------------------------

        composable("configuracion") {

            ConfiguracionScreen(
                config = config,
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // ------------------------------------------------------------
        // EJERCICIOS
        // ------------------------------------------------------------

        composable("ejercicios") {

            val vm: EjerciciosViewModel =
                viewModel(
                    factory = EjerciciosViewModelFactory(repo)
                )

            EjerciciosScreen(
                viewModel = vm,

                onEjercicioClick = { ejercicio: Ejercicio ->
                    navController.navigate("progreso/${ejercicio.id}")
                },

                onIniciarSesion = {
                    navController.navigate("planes")
                },

                onVerHistorial = {
                    navController.navigate("historial")
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }


        // ------------------------------------------------------------
        // PLANES
        // ------------------------------------------------------------

        composable("planes") {

            PlanesScreen(
                onPlanClick = { planId ->

                    navController.navigate(
                        "nueva_sesion/$planId"
                    )
                },

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // ------------------------------------------------------------
        // NUEVA SESION
        // ------------------------------------------------------------

        composable(
            route = "nueva_sesion/{planId}",

            arguments = listOf(
                navArgument("planId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->

            val planId =
                entry.arguments?.getString("planId")
                    ?: "libre"

            val plan =
                if (planId == "libre") {
                    null
                } else {
                    PlanesPreconfigurados.porId(planId)
                }


            LaunchedEffect(planId) {

                val id = repo.iniciarSesion(
                    System.currentTimeMillis(),
                    plan?.nombre ?: "Entrenamiento libre",
                    planId
                )

                navController.navigate(
                    "sesion/$id/$planId"
                ) {

                    popUpTo(
                        "nueva_sesion/$planId"
                    ) {
                        inclusive = true
                    }
                }
            }
        }


        // ------------------------------------------------------------
        // SESION
        // ------------------------------------------------------------

        composable(
            route = "sesion/{sesionId}/{planId}",

            arguments = listOf(

                navArgument("sesionId") {
                    type = NavType.LongType
                },

                navArgument("planId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->

            val id =
                entry.arguments?.getLong("sesionId")
                    ?: return@composable

            val planId =
                entry.arguments?.getString("planId")
                    ?: "libre"


            val vm: SesionViewModel =
                viewModel(
                    factory = SesionViewModelFactory(
                        repo,
                        id,
                        planId
                    )
                )


            SesionScreen(

                viewModel = vm,

                onFinalizar = {

                    navController.popBackStack(
                        "inicio",
                        false
                    )
                },

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // ------------------------------------------------------------
        // PROGRESO
        // ------------------------------------------------------------

        composable(
            route = "progreso/{ejercicioId}",

            arguments = listOf(

                navArgument("ejercicioId") {
                    type = NavType.LongType
                }
            )
        ) { entry ->

            val id =
                entry.arguments?.getLong("ejercicioId")
                    ?: return@composable


            val vm: ProgresoViewModel =
                viewModel(
                    factory = ProgresoViewModelFactory(
                        repo,
                        id
                    )
                )


            ProgresoScreen(

                vm,

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // ------------------------------------------------------------
        // HISTORIAL
        // ------------------------------------------------------------

        composable("historial") {

            val vm: HistorialViewModel =
                viewModel(
                    factory = HistorialViewModelFactory(repo)
                )


            HistorialScreen(

                vm,

                onSesionClick = { sesionId ->

                    navController.navigate(
                        "historial/$sesionId"
                    )
                },

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // ------------------------------------------------------------
        // DETALLE HISTORIAL
        // ------------------------------------------------------------

        composable(
            route = "historial/{sesionId}",

            arguments = listOf(

                navArgument("sesionId") {
                    type = NavType.LongType
                }
            )
        ) { entry ->

            val id =
                entry.arguments?.getLong("sesionId")
                    ?: return@composable


            val vm: HistorialDetalleViewModel =
                viewModel(
                    factory =
                        HistorialDetalleViewModelFactory(
                            repo,
                            id
                        )
                )


            HistorialDetalleScreen(

                vm,

                onBack = {

                    navController.popBackStack()
                }
            )
        }
    }
}