# GymApp

App de gym personal — Android nativo (Kotlin + Jetpack Compose + Room). 100% offline: no usa internet, todo se guarda en una base SQLite local en el celular.

## Cómo abrirlo

1. Abrí Android Studio (versión reciente, Ladybug o superior).
2. `File > Open` y seleccioná esta carpeta (`GymApp/`).
3. Dejá que sincronice Gradle (puede tardar la primera vez, descarga dependencias).
4. Corré la app en un emulador o en tu celular (`Run > Run 'app'`).

Puede que Android Studio te pida actualizar el Gradle Wrapper la primera vez — aceptá, se genera solo.

## Qué hay armado

- **Modelo de datos (Room)**: `Ejercicio`, `Sesion`, `SerieRegistrada` — con relaciones entre sesión/ejercicio/serie, y una consulta (`observarProgreso`) que te trae la evolución de peso y repeticiones de un ejercicio en el tiempo.
- **Precarga**: al abrir la app por primera vez se cargan 8 ejercicios comunes (banco, sentadilla, peso muerto, etc.), y podés agregar los tuyos.
- **Repositorio**: `GymRepository` — capa que conecta la base de datos con la UI, para no acoplar los ViewModels directo a Room.
- **Pantalla armada**: lista de ejercicios (`EjerciciosScreen`), muestra todo lo que hay en la base, con click preparado para ir al detalle.

## Lo que falta (próximos pasos sugeridos)

1. Pantalla para **iniciar una sesión** y cargar series (peso/reps) por ejercicio.
2. Pantalla de **detalle/progreso** de un ejercicio (gráfico usando `observarProgreso`).
3. Pantalla de **historial** de sesiones pasadas.
4. Ícono de la app y `strings.xml` (por ahora los textos están hardcodeados).
5. Lo de "mantenerte presente en el gimnasio": temporizador de descanso entre series, notificaciones, modo pantalla-en-uso, o lo que se te ocurra — lo vemos cuando lleguemos ahí.

## Decisiones tomadas (y por qué)

- **Room en vez de un backend**: pediste offline, así que no hay servidor ni login. Todo vive en el celular.
- **Compose en vez de XML/Views**: es el estándar actual de Google para UI en Android.
- **Sin DI (Hilt/Koin)**: para mantenerlo simple al inicio armé una Factory manual para el ViewModel. Si el proyecto crece podemos sumar Hilt.
