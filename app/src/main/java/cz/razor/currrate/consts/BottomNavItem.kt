package cz.razor.currrate.consts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val screenRoute: String) {
    object CurrencyList : BottomNavItem("Currency Rates", Icons.Filled.Home, Routes.CurrencyList)
    object FavouriteCurrency : BottomNavItem("Favourite", Icons.Filled.FavoriteBorder, Routes.FavouriteCurrencyList)
}