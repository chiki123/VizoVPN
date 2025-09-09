package com.chikivision.vizovpn.ui.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main_screen")
    data object ServerList : Screen("server_list_screen")
}
