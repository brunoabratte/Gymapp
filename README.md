# GymApp

App de gym personal — Android nativo (Kotlin + Jetpack Compose + Room). Actualmente funciona localmente con SQLite en el celular y está pensada como arquitectura local-first, para poder sumar sincronización con Firebase y cuenta de Google más adelante.

## Cómo abrirlo

1. Abrí Android Studio (versión reciente, Ladybug o superior).
2. `File > Open` y seleccioná esta carpeta (`GymApp/`).
3. Dejá que sincronice Gradle (puede tardar la primera vez, descarga dependencias).
4. Corré la app en un emulador o en tu celular (`Run > Run 'app'`).

Puede que Android Studio te pida actualizar el Gradle Wrapper la primera vez — aceptá, se genera solo.

## Qué hay armado

- **Modelo de datos (Room)**: `Ejercicio`, `Sesion`, `SerieRegistrada` — con relaciones entre sesión/ejercicio/serie, y una consulta (`observarProgreso`) que te trae la evolución de peso y repeticiones de un ejercicio en el tiempo.
- **Precarga**: al abrir la app por primera vez se cargan ejercicios comunes con grupo muscular, tipo de ejercicio y descanso base. También podés agregar los tuyos.
- **Repositorio**: `GymRepository` — capa que conecta la base de datos con la UI, para no acoplar los ViewModels directo a Room.
- **Pantalla armada**: lista de ejercicios (`EjerciciosScreen`), muestra todo lo que hay en la base, con click preparado para ir al detalle.

## Lo que falta (próximos pasos sugeridos)

1. Recuperar una sesión que quedó en curso.
2. Mostrar el último rendimiento al cargar una serie.
3. Mejorar el progreso y los gráficos.
4. Notificaciones y modo pantalla-en-uso.
5. Configuración del entrenamiento.
6. Ampliar ejercicios y permitir crear ejercicios personalizados con tipo y descanso.
7. Firebase Auth + Firestore para cuenta de Google y sincronización entre dispositivos.

## Decisiones tomadas (y por qué)

- **Room como almacenamiento local**: la app debe seguir funcionando sin conexión. Más adelante se puede agregar Firebase Auth + Firestore para sincronizar configuraciones, rutinas e historial entre dispositivos.
- **Compose en vez de XML/Views**: es el estándar actual de Google para UI en Android.
- **Sin DI (Hilt/Koin)**: para mantenerlo simple al inicio armé una Factory manual para el ViewModel. Si el proyecto crece podemos sumar Hilt.
- **Planes preconfigurados**: la sesión empieza seleccionando una rutina como Full Body, Hipertrofia, Fuerza o Torso/Pierna. Cada plan define descansos diferentes para ejercicios compuestos, básicos y de aislamiento.
- **Timer inteligente**: el descanso se calcula según el ejercicio y el plan seleccionado, en lugar de usar un único valor fijo.
