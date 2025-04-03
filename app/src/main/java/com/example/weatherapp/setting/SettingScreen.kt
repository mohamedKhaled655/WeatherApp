package com.example.weatherapp.setting


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.utils.Lang
import com.example.weatherapp.utils.LocaleHelper
import com.example.weatherapp.utils.LocationType
import com.example.weatherapp.utils.TempUnit
import com.example.weatherapp.utils.WindSpeedUnit


@Composable
fun SettingScreen(innerPadding: PaddingValues, viewModel: SettingsViewModel) {
    val locationType by viewModel.locationType.collectAsState()
    val tempUnit by viewModel.tempUnit.collectAsState()
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsState()
    val language by viewModel.language.collectAsState()

    val context=LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))


        Text(text = "Location Type", style = MaterialTheme.typography.bodyMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LocationType.entries.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.updateLocationType(type) }
                        .padding(8.dp)
                ) {
                    RadioButton(selected = type == locationType, onClick = { viewModel.updateLocationType(type) })
                    Text(text = type.name, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text(text = "Temperature Unit", style = MaterialTheme.typography.bodyMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TempUnit.entries.forEach { unit ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.updateTemperatureUnit(unit) }
                        .padding(8.dp)
                ) {
                    RadioButton(selected = unit == tempUnit, onClick = { viewModel.updateTemperatureUnit(unit) })
                    Text(text = unit.name, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text(text = "Wind Speed Unit", style = MaterialTheme.typography.bodyMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            WindSpeedUnit.entries.forEach { unit ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.updateWindSpeedUnit(unit) }
                        .padding(8.dp)
                ) {
                    RadioButton(selected = unit == windSpeedUnit, onClick = { viewModel.updateWindSpeedUnit(unit) })
                    Text(text = unit.name, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text(text = "Language", style = MaterialTheme.typography.bodyMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Lang.entries.forEach { lang ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            LocaleHelper.setLocale(context,lang.name.lowercase())
                            viewModel.updateLanguage(lang)
                        }
                        .padding(8.dp)
                ) {
                    RadioButton(selected = lang == language, onClick = {
                        LocaleHelper.setLocale(context,lang.name.lowercase())
                        viewModel.updateLanguage(lang)
                    })
                    Text(text = lang.name, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}