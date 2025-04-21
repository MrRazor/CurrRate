package cz.razor.currrate.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.items.CurrencyItem
import cz.razor.currrate.viewmodels.CurrencyListViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun FavouriteCurrencyListScreen(
    navController: NavController,
    viewModel: CurrencyListViewModel = koinViewModel()
) {
    val listState = rememberLazyListState()
    val currencyList by viewModel.currencyList.collectAsState()
    val currencyDetailList by viewModel.currencyDetailList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCurrencyList()
        viewModel.getCurrencyDetailList()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        when (currencyList) {
            is ApiResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is ApiResult.Success -> {
                when (currencyDetailList) {
                    is ApiResult.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    is ApiResult.Success -> {
                        val currencyDetailList = (currencyDetailList as ApiResult.Success).data
                        val currencyList = (currencyList as ApiResult.Success).data.filter { currency -> currencyDetailList.find { currencyInfo -> (currencyInfo.code == currency.toCurrency) && currencyInfo.isToCurrencyFavourite } != null }
                        LazyColumn(state = listState) {
                            items(currencyList) { currency ->
                                CurrencyItem(
                                    navController,
                                    currency,
                                    currencyDetailList.find { currencyInfo -> currencyInfo.code == currency.toCurrency }!!
                                )
                            }
                        }
                    }
                    is ApiResult.Error -> {
                        val errorMessage = (currencyDetailList as ApiResult.Error).message
                        Text(text = "Error: $errorMessage")
                    }
                }
            }

            is ApiResult.Error -> {
                val errorMessage = (currencyList as ApiResult.Error).message
                Text(text = "Error: $errorMessage")
            }
        }
    }
}
