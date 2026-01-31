package com.example.remeducp2.ui.view.uicontroller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remeducp2.ui.view.HalamanHome
import com.example.remeducp2.ui.view.HalamanEntryBuku
import com.example.remeducp2.ui.view.HalamanEntryKategori
import com.example.remeducp2.ui.view.route.DestinasiHome
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.remeducp2.ui.view.HalamanDetailBuku
import com.example.remeducp2.ui.view.HalamanEditBuku
import com.example.remeducp2.ui.view.route.DestinasiDetailBuku
import com.example.remeducp2.ui.view.route.DestinasiEditBuku
import com.example.remeducp2.ui.view.route.DestinasiEntryBuku
import com.example.remeducp2.ui.view.route.DestinasiEntryKategori

@Composable
fun PetaNavigasi(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = modifier
    ) {
        composable(route = DestinasiHome.route) {
            HalamanHome(
                navigateToItemEntry = { navController.navigate(DestinasiEntryBuku.route) },
                navigateToCategoryEntry = { navController.navigate(DestinasiEntryKategori.route) },
                navigateToDetail = { id ->
                    navController.navigate("${DestinasiDetailBuku.route}/$id")
                }
            )
        }
        composable(route = DestinasiEntryBuku.route) {
            HalamanEntryBuku(navigateBack = { navController.popBackStack() })
        }
        composable(route = DestinasiEntryKategori.route) {
            HalamanEntryKategori(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = DestinasiDetailBuku.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailBuku.idBukuArg) { type = NavType.IntType })
        ) {
            HalamanDetailBuku(
                navigateToEditItem = { navController.navigate("${DestinasiEditBuku.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = DestinasiEditBuku.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditBuku.idBukuArg) { type = NavType.IntType })
        ) {
            HalamanEditBuku(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
