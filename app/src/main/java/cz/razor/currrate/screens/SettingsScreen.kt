package cz.razor.currrate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val currencyCodeList by settingsViewModel.currencyCodeList.collectAsState()
    val baseCurrency by settingsViewModel.baseCurrency.collectAsState()


    var selectedBaseCurrency by remember { mutableStateOf( "EUR") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { settingsViewModel.getCurrencyCodeList() }

    LaunchedEffect(baseCurrency) {
        selectedBaseCurrency = baseCurrency
    }

    Column(modifier = Modifier.padding(16.dp)) {
        when (currencyCodeList) {
            is ApiResult.Loading -> {
                Text(text = "Loading Currencies...")
            }

            is ApiResult.Success -> {
                val currencyList = (currencyCodeList as ApiResult.Success<List<String>>).data

                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Base Currency")
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.background(Color.Transparent),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = selectedBaseCurrency)
                    }
                }


                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Select Currency") },
                        modifier = Modifier
                            .padding(16.dp)
                            .sizeIn(maxWidth = 200.dp, maxHeight = 400.dp),
                        text = {
                            LazyColumn {
                                items(currencyList) { currency ->
                                    Text(
                                        text = currency,
                                        modifier = Modifier
                                            .clickable {
                                                selectedBaseCurrency = currency
                                                showDialog = false
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    settingsViewModel.saveBaseCurrency(selectedBaseCurrency)
                }) {
                    Text(text = "Save")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            is ApiResult.Error -> {
                Text(text = "Error Loading Currencies: ${(currencyCodeList as ApiResult.Error).message}")
            }
        }
    }
}