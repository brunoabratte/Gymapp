package com.bruno.gymapp.data

import android.content.Context
import com.bruno.gymapp.data.local.entity.PlanesPreconfigurados
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConfiguracionEntrenamiento(context: Context) {
    private val prefs = context.getSharedPreferences("gymapp_config", Context.MODE_PRIVATE)
    private val _cambios = MutableStateFlow(0)
    val cambios: StateFlow<Int> = _cambios

    fun estaConfigurado(): Boolean = prefs.getBoolean("configurado", false)
    fun planDelDia(): String = prefs.getString("dia_${diaActual()}", "") ?: ""
    fun planParaDia(dia: Int): String = prefs.getString("dia_$dia", "") ?: ""
    fun guardarPlanDia(dia: Int, planId: String) { prefs.edit().putString("dia_$dia", planId).apply() }
    fun inicializarPlanHoy(planId: String) {
        guardarPlanDia(diaActual(), planId)
        guardarConfigurado()
    }
    fun guardarConfigurado(valor: Boolean = true) {
        prefs.edit().putBoolean("configurado", valor).apply()
        _cambios.value += 1
    }
    fun descansoVibracion(): Boolean = prefs.getBoolean("vibracion", true)
    fun setVibracion(valor: Boolean) { prefs.edit().putBoolean("vibracion", valor).apply() }

    private fun diaActual(): Int {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (day) {
            Calendar.MONDAY -> 1; Calendar.TUESDAY -> 2; Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4; Calendar.FRIDAY -> 5; Calendar.SATURDAY -> 6
            else -> 7
        }
    }

    companion object {
        val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        fun nombrePlan(id: String): String = if (id.isBlank() || id == "libre") "Descanso / libre" else PlanesPreconfigurados.porId(id).nombre
    }
}
