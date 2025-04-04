package com.example.weatherapp.alarms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.weatherapp.data.models.WeatherAlert
import com.example.weatherapp.utils.toFormattedTime
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RequestNotificationPermission(context: Context) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Permission denied! Alerts may not work properly.", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(innerPadding: PaddingValues, viewModel: AlertViewModel) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    val alerts by viewModel.alarms.collectAsState(initial = emptyList())


    RequestNotificationPermission(context)

    Scaffold(
        modifier = Modifier.padding(
            top = 24.dp, start = 24.dp, end = 24.dp, bottom = innerPadding.calculateBottomPadding()
        ),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Alert",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Weather Alerts",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No alerts set. Tap + to add a new alert",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alerts) { alert ->
                        AlertItem(
                            alert = alert,
                            onToggleActive = { toggledAlert ->
                                viewModel.addAlert(toggledAlert.copy(isActive = !toggledAlert.isActive))
                                if (!toggledAlert.isActive) {
                                    scheduleNotificationWithWorkManager(context, toggledAlert.copy(isActive = true))
                                } else {
                                    cancelWeatherAlert(context, toggledAlert.id)
                                }
                            },
                            onDelete = {
                                cancelWeatherAlert(context, alert.id)
                                viewModel.removeAlert(alert)

                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddWeatherAlertDialog(
            onDismiss = { showAddDialog = false },
            onAddAlert = { alert ->
                viewModel.addAlert(alert)
                //viewModel.sendWeatherAlert(alert)
                scheduleNotificationWithWorkManager(context, alert)
                showAddDialog = false
                Toast.makeText(context, "Weather alert set successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AlertItem(
    alert: WeatherAlert,
    onToggleActive: (WeatherAlert) -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alert: ${alert.type}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Alert",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start: ${dateFormat.format(Date(alert.startTime))}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "End: ${dateFormat.format(Date(alert.endTime))}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (alert.isNotification) "Notification" else "Alarm Sound",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (alert.isActive) "Active" else "Inactive",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (alert.isActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = alert.isActive,
                        onCheckedChange = { onToggleActive(alert) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWeatherAlertDialog(
    onDismiss: () -> Unit,
    onAddAlert: (WeatherAlert) -> Unit
) {
    var alertType by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var useNotification by remember { mutableStateOf(true) }
    var selectedDuration by remember { mutableStateOf("1 hour") }


    val calendar = remember { Calendar.getInstance() }
    var selectedStartDate by remember { mutableStateOf(calendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    val durations = listOf("1 minutes","15 minutes", "30 minutes", "1 hour", "3 hours", "6 hours", "12 hours", "24 hours")
    var showDurationMenu by remember { mutableStateOf(false) }

    val durationMillis = when (selectedDuration) {
        "1 minutes" -> 1 * 60 * 1000L
        "15 minutes" -> 15 * 60 * 1000L
        "30 minutes" -> 30 * 60 * 1000L
        "1 hour" -> 60 * 60 * 1000L
        "3 hours" -> 3 * 60 * 60 * 1000L
        "6 hours" -> 6 * 60 * 60 * 1000L
        "12 hours" -> 12 * 60 * 60 * 1000L
        "24 hours" -> 24 * 60 * 60 * 1000L
        else -> 60 * 60 * 1000L
    }


    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedStartDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val newCalendar = Calendar.getInstance().apply {
                                timeInMillis = selectedStartDate
                                set(Calendar.YEAR, Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.YEAR))
                                set(Calendar.MONTH, Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.DAY_OF_MONTH))
                            }
                            selectedStartDate = newCalendar.timeInMillis
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = Calendar.getInstance().apply { timeInMillis = selectedStartDate }.get(Calendar.HOUR_OF_DAY),
            initialMinute = Calendar.getInstance().apply { timeInMillis = selectedStartDate }.get(Calendar.MINUTE)
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newCalendar = Calendar.getInstance().apply {
                            timeInMillis = selectedStartDate
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        selectedStartDate = newCalendar.timeInMillis
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Weather Alert") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                TextField(
                    value = alertType,
                    onValueChange = { alertType = it },
                    label = { Text("Alert Type (e.g., Rain, Storm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("set desc") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Start Date and Time:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(dateFormatter.format(Date(selectedStartDate)), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(timeFormatter.format(Date(selectedStartDate)))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Alert Duration:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box {
                    OutlinedTextField(
                        value = selectedDuration,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showDurationMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Select Duration"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = showDurationMenu,
                        onDismissRequest = { showDurationMenu = false }
                    ) {
                        durations.forEach { duration ->
                            DropdownMenuItem(
                                text = { Text(duration) },
                                onClick = {
                                    selectedDuration = duration
                                    showDurationMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Alert Type:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = useNotification,
                        onClick = { useNotification = true }
                    )
                    Text(text = "Notification")

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = !useNotification,
                        onClick = { useNotification = false }
                    )
                    Text(text = "Alarm Sound")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (alertType.isNotBlank()) {
                        val alert = WeatherAlert(

                            type = alertType,
                            startTime = selectedStartDate,
                            endTime = selectedStartDate + durationMillis,
                            isNotification = useNotification,
                            isActive = true,
                            desc = desc
                        )
                        onAddAlert(alert)
                    }
                },
                enabled = alertType.isNotBlank()
            ) {
                Text("Set Alert")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }
        }
    )
}