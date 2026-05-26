package com.mindshift.mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.mindshift.mobile.api.RetrofitInstance
import com.mindshift.mobile.models.AnxietyPayload
import com.mindshift.mobile.ui.theme.MindShiftAnxietyTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindShiftAnxietyTrackerTheme {
                AnxietyScreen()
            }
        }
    }
}

@Composable
fun AnxietyScreen() {

    // Scope inicial
    val scope = rememberCoroutineScope()

    /*
    * Usuario Random generado
    * Podemos generar uno especifico tipo user_1 y tener persistencia entre pruebas
    * */
    val randomUser = remember {
        "user_${Random.nextInt(1000, 9999)}"
    }

    // Contador de clicks
    var anxietyCount by remember { mutableIntStateOf(0) }
    // Iniciador de temporizador
    var timerStarted by remember { mutableStateOf(false) }

    // Estado global del gráfico y datos de clicks
    var weeklyData by remember {
        mutableStateOf<List<AnxietyPayload>>(emptyList())
    }

    // Refrescar datos del gráfico
    fun loadWeeklyData() {
        scope.launch {
            try {
                val response =
                    RetrofitInstance.api.getAnxietyData(randomUser)

                weeklyData = response

                Log.d("API", "GET success: ${response.size}")

            } catch (e: Exception) {
                Log.e("API", "GET error: ${e.message}")
            }
        }
    }

    // Carga inicial
    LaunchedEffect(Unit) {
        loadWeeklyData()
    }

    LaunchedEffect(timerStarted) {
        if (timerStarted) {

            // Temporizador iniciado, esperamos 1 hora
            delay(60000*60)

            if (anxietyCount > 0) {
                sendAnxietyData(scope, randomUser, anxietyCount)
                anxietyCount = 0

                // Refrescar gráfico después de enviar datos
                scope.launch {
                    /*
                    * Esperamos la creación y update de datos para volver a solicitar
                    * Podría esperar menos, pero para asegurar no tiene problemas
                    * */
                    delay(800)
                    loadWeeklyData()
                }
            }

            timerStarted = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Entregando datos al gráfico
        AnxietyChart(
            weeklyData = weeklyData
        )

        Spacer(modifier = Modifier.height(64.dp))

        AnxietyButton(onClick = {

            anxietyCount++

            if (!timerStarted) {
                // Si temporizador no se ha iniciado, se inicia
                timerStarted = true
            }

            // Si contador llega a 100 o más clicks, envía el post
            if (anxietyCount >= 100) {

                sendAnxietyData(scope, randomUser, anxietyCount)

                anxietyCount = 0
                timerStarted = false

                // Refrescar gráfico después de enviar datos
                scope.launch {
                    /*
                    * Esperamos la creación y update de datos para volver a solicitar
                    * Podría esperar menos, pero para asegurar no tiene problemas
                    * */
                    delay(800)
                    loadWeeklyData()
                }
            }
        })

        Spacer(modifier = Modifier.height(12.dp))

        UpdateButton(onClick = {
            loadWeeklyData()
        })
    }
}

fun sendAnxietyData(
    scope: CoroutineScope,
    user: String,
    count: Int
) {
    scope.launch {
        try {
            val date = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())

            val payload = AnxietyPayload(
                user = user,
                clicksCount = count,
                date = date
            )

            val response =
                RetrofitInstance.api.sendAnxietyData(payload)

            Log.d("API", "POST success: ${response.code()}")

        } catch (e: Exception) {
            Log.e("API", "POST error: ${e.message}")
        }
    }
}

@Composable
fun AnxietyChart(
    weeklyData: List<AnxietyPayload>
) {

    // últimos 7 días terminando en hoy (rolling window)
    val last7Days = (6 downTo 0).map { offset ->
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -offset)
        cal.get(Calendar.DAY_OF_WEEK)
    }

    val dayLabelsMap = mapOf(
        Calendar.MONDAY to "L",
        Calendar.TUESDAY to "M",
        Calendar.WEDNESDAY to "M",
        Calendar.THURSDAY to "J",
        Calendar.FRIDAY to "V",
        Calendar.SATURDAY to "S",
        Calendar.SUNDAY to "D"
    )

    val last7Labels = last7Days.map { day ->
        dayLabelsMap[day] ?: "?"
    }

    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Agrupar backend por día de semana
    val realDataByDay = weeklyData.groupBy { item ->
        val date = sdf.parse(item.date)
        calendar.time = date!!
        calendar.get(Calendar.DAY_OF_WEEK)
    }.mapValues { entry ->
        entry.value.sumOf { it.clicksCount }
    }

    // Semana completa (siempre 7 días)
    val finalWeekData = last7Days.map { day ->
        realDataByDay[day] ?: 0
    }

    // métricas correctas basadas en semana completa
    val total = finalWeekData.sum()

    // promedio por día de semana
    val average = if (finalWeekData.isNotEmpty())
        finalWeekData.average()
    else 0.0

    // Valor máximo y altura máxima
    val maxValue = finalWeekData.maxOrNull()?.toFloat() ?: 1f
    val maxBarHeight = 160.dp

    // Calcula día actual
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    // Calcula indice del día actual
    val todayIndex = last7Days.indexOf(currentDay)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {

        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = "Resumen semanal",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Registros de ansiedad últimos 7 días",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // métricas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = total.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF1565C0)
                    )

                    Text(
                        text = "Total semanal",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", average),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF26A69A)
                    )

                    Text(
                        text = "Promedio diario",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Grafica
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                finalWeekData.forEachIndexed { index, value ->

                    val isToday = index == todayIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        // valor
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isToday) Color(0xFF1565C0) else Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // barra
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(
                                    maxBarHeight * (value / maxValue)
                                )
                                .background(
                                    if (isToday)
                                        Color(0xFF1565C0)
                                    else
                                        Color(0xFF90CAF9)
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // label día
                        Text(
                            text = last7Labels[index],
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isToday) Color(0xFF1565C0) else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnxietyButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
    ) {
        Text("Tengo ansiedad")
    }
}

@Composable
fun UpdateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A))
    ) {
        Text("Actualizar Datos")
    }
}