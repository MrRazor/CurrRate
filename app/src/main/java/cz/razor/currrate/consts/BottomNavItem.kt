package cz.razor.currrate.consts

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import cz.razor.currrate.R

sealed class BottomNavItem(@StringRes val titleRes: Int, val icon: ImageVector, val screenRoute: String) {
    object CurrencyList :
        BottomNavItem(R.string.currency_rates, Icons.Filled.Home, Routes.CurrencyList)

    object FavouriteCurrency :
        BottomNavItem(R.string.favourites, Icons.Filled.FavoriteBorder, Routes.FavouriteCurrencyList)
}