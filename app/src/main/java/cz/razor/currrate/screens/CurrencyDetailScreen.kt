package cz.razor.currrate.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.razor.currrate.R
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.consts.Routes
import cz.razor.currrate.viewmodels.CurrencyDetailViewModel
import cz.razor.currrate.dialogs.DeleteCurrencyConfirmationDialog
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.time.LocalDate


@Composable
fun CurrencyDetailScreen(
    navController: NavController,
    to: String,
    date: LocalDate,
    viewModel: CurrencyDetailViewModel = koinViewModel(),
) {
    val currency by viewModel.currency.collectAsState()
    val currencyYesterday by viewModel.currencyYesterday.collectAsState()
    val currencyDetail by viewModel.currencyDetail.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getSingleCurrency(to, date)
        viewModel.getSingleCurrencyDetail(to)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        when (currency) {
            is ApiResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is ApiResult.Success -> {
                val currency = (currency as ApiResult.Success).data
                when (currencyYesterday) {
                    is ApiResult.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    is ApiResult.Success -> {
                        val currencyYesterday = (currencyYesterday as ApiResult.Success).data
                        when (currencyDetail) {
                            is ApiResult.Loading -> {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                            }
                            is ApiResult.Success -> {
                                val currencyDetail = (currencyDetail as ApiResult.Success).data
                                val percentChange = BigDecimal.valueOf(currency.rate).minus(BigDecimal.valueOf(currencyYesterday.rate)).divide(BigDecimal("100"));
                                var color = MaterialTheme.colorScheme.onBackground
                                if(percentChange > BigDecimal.ZERO) {
                                    color = colorResource(R.color.success_green)
                                }
                                if(percentChange < BigDecimal.ZERO) {
                                    color = colorResource(R.color.epic_fail_red)
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = currencyDetail.name,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = stringResource(
                                            R.string.from_1,
                                            currency.baseCurrency
                                        ))
                                        Text(text = stringResource(
                                            R.string.rate,
                                            currency.date,
                                            currency.rate,
                                            currency.toCurrency
                                        ))
                                        Text(color = color, text = stringResource(
                                            R.string.change,
                                            percentChange.toPlainString()
                                        ))
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(text = stringResource(
                                            R.string.previous,
                                            currencyYesterday.date,
                                            currencyYesterday.rate,
                                            currencyYesterday.toCurrency
                                        ))
                                    }
                                    IconButton(onClick = {
                                        if (!currencyDetail.isToCurrencyFavourite) {
                                            viewModel.addFavoriteCurrency(currencyDetail)
                                        } else {
                                            showDeleteConfirmDialog = true
                                        }
                                    }) {
                                        if (currencyDetail.isToCurrencyFavourite) {
                                            Icon(
                                                Icons.Filled.Favorite,
                                                contentDescription = stringResource(R.string.remove_from_favorites)
                                            )
                                        } else {
                                            Icon(
                                                Icons.Filled.FavoriteBorder,
                                                contentDescription = stringResource(R.string.add_to_favorites)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            navController.navigate(Routes.currencyGraph(currency.toCurrency, currency.date))
                                        }
                                    ) {
                                        Text(stringResource(R.string.show_graph))
                                    }
                                }

                                if (showDeleteConfirmDialog) {
                                    DeleteCurrencyConfirmationDialog(
                                        currencyDetail.code,
                                        onConfirmDelete = {
                                            viewModel.removeFavoriteCurrency(currencyDetail)
                                            showDeleteConfirmDialog = false
                                        },
                                        onDismiss = {
                                            showDeleteConfirmDialog = false
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.user_canceled_the_deletion),
                                                Toast.LENGTH_SHORT
                                            ).show();
                                        })
                                }
                            }
                            is ApiResult.Error -> {
                                val errorMessage = (currencyDetail as ApiResult.Error).message
                                Text(text = stringResource(R.string.error, errorMessage), color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                    is ApiResult.Error -> {
                        val errorMessage = (currencyYesterday as ApiResult.Error).message
                        Text(text = stringResource(R.string.error, errorMessage), color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
            is ApiResult.Error -> {
                val errorMessage = (currency as ApiResult.Error).message
                Text(text = stringResource(R.string.error, errorMessage), color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}
