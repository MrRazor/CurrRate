package cz.razor.currrate.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.viewmodels.CurrencyViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate


@Composable
fun CurrencyDetailScreen(
    base: String,
    to: String,
    date: LocalDate,
    viewModel: CurrencyViewModel = koinViewModel(),
) {
    val currency by viewModel.currency.collectAsState()
    val currencyDetail by viewModel.currencyDetail.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSingleCurrency(base, to, date)
        viewModel.getSingleCurrencyDetail(to)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        when (currency) {
            is ApiResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is ApiResult.Success -> {
                when (currencyDetail) {
                    is ApiResult.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    is ApiResult.Success -> {
                        val currency = (currency as ApiResult.Success).data
                        val currencyDetail = (currencyDetail as ApiResult.Success).data
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = currencyDetail.name, fontWeight = FontWeight.Bold)
                                Text(text = "From: 1 ${currency.baseCurrency}")
                                Text(text = "Rate: ${currency.rate} ${currency.toCurrency}")
                            }
                        }
                    }
                    is ApiResult.Error -> {
                        val errorMessage = (currency as ApiResult.Error).message
                        Text(text = "Error: $errorMessage")
                    }
                }
            }

            is ApiResult.Error -> {
                val errorMessage = (currency as ApiResult.Error).message
                Text(text = "Error: $errorMessage")
            }
        }
    }
}
