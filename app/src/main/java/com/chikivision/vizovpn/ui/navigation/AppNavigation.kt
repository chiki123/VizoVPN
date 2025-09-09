package com.chikivision.vizovpn.ui.navigation



import androidx.compose.runtime.Composable

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.compose.NavHost

import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController

import com.chikivision.vizovpn.ui.screens.MainScreen

import com.chikivision.vizovpn.ui.screens.ServerListScreen



@Composable

fun AppNavigation() {

    val navController = rememberNavController()



    NavHost(

        navController = navController,

        startDestination = Screen.Main.route

    ) {

        composable(route = Screen.Main.route) {

            MainScreen(

                viewModel = hiltViewModel(),

                onNavigateToServerList = {

                    navController.navigate(Screen.ServerList.route)

                }

            )

        }

        composable(route = Screen.ServerList.route) {

            ServerListScreen(

                navController = navController,

                viewModel = hiltViewModel()

            )

        }

    }

}