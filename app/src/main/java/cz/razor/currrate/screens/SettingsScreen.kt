package cz.razor.currrate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.razor.currrate.R
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.consts.SettingsKeys
import cz.razor.currrate.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val currencyCodeList by settingsViewModel.currencyCodeList.collectAsState()
    val baseCurrency by settingsViewModel.baseCurrency.collectAsState()

    var selectedBaseCurrency by remember { mutableStateOf(SettingsKeys.DEFAULT_BASE_CURRENCY_CODE) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { settingsViewModel.getCurrencyCodeList() }
    LaunchedEffect(baseCurrency) { selectedBaseCurrency = baseCurrency }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (currencyCodeList) {
            is ApiResult.Loading -> {
                Text(text = stringResource(R.string.loading_currencies))
            }

            is ApiResult.Success -> {
                val currencyList = (currencyCodeList as ApiResult.Success<List<String>>).data

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.base_currency))

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.background(Color.Transparent),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(text = selectedBaseCurrency)
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = {
                                Text(
                                    stringResource(R.string.select_currency),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            modifier = Modifier
                                .padding(16.dp)
                                .sizeIn(maxWidth = 300.dp, maxHeight = 500.dp),
                            text = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 300.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        items(currencyList) { currency ->
                                            Text(
                                                text = currency,
                                                modifier = Modifier
                                                    .clickable {
                                                        selectedBaseCurrency = currency
                                                        showDialog = false
                                                    }
                                                    .padding(8.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(onClick = { showDialog = false }) {
                                        Text(stringResource(R.string.close))
                                    }
                                }
                            },
                            confirmButton = {}
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        settingsViewModel.saveBaseCurrency(selectedBaseCurrency)
                    }) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            }

            is ApiResult.Error -> {
                val errorMessage = (currencyCodeList as ApiResult.Error).message
                Text(text = stringResource(R.string.error, errorMessage), color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}