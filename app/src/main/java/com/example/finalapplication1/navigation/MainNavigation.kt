package com.example.finalapplication1.navigation



import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.finalapplication1.PictureViewModel
import com.example.finalapplication1.Screens.DetailsScreen
import com.example.finalapplication1.Screens.FavoriteScreen
import com.example.finalapplication1.Screens.MainScreen
import com.example.finalapplication1.Screens.SearchScreen


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val viewModel: PictureViewModel = viewModel()



    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,

            modifier = Modifier.padding(innerPadding)
        ) {

        composable(BottomNavItem.Home.route) {
                MainScreen(
                    imageUrl = viewModel.baseImageUrl,
                    isFavorite = viewModel.isFavorite.collectAsState(),
                    onToggleFavorite = { viewModel.toggleFavoriteForCurrentImage() },
                    refreshImage = { viewModel.refreshImage() },
                    onToggleGrayscale = { viewModel.toggleGrayscale() },
                    onBlurChangeFinished = { viewModel.updateBlur(it) },
                    sliderPosition = viewModel.sliderPosition,
                    onSliderValueChange = { viewModel.sliderPosition = it },
                    isGray = viewModel.isGray.collectAsState(),
                    updateFavoriteStatusForCurrent = { viewModel::updateFavoriteStatusForCurrent },
                )
            }
            composable(BottomNavItem.Search.route) {
                SearchScreen(
                    photos = viewModel.photos.collectAsState(),
                    loadMorePhotos = { viewModel.loadMorePhotos() },
                    isFavorite = { image -> viewModel.isImageFavorite(image.download_url) },
                    isLoading = viewModel.isLoading.collectAsState(),
                    navController = navController,
                    toggleFavoriteForImage = { image -> viewModel.toggleFavoriteForImage(image) },
                )
            }
            composable(BottomNavItem.Favorites.route) {
                FavoriteScreen(
                    navController = navController,
                    favorites = viewModel.favorites.collectAsState(initial = emptyList()),
                    isFavorite = { image -> viewModel.isImageFavorite(image.download_url) },
                    toggleFavoriteForImage = { image -> viewModel.toggleFavoriteForImage(image) }
                )
            }
            composable(
                route = DetailRoute.route,
                arguments = listOf(navArgument("download_url") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->

                val encodedUrl = backStackEntry.arguments?.getString("download_url")
                val downloadUrl = encodedUrl?.let { Uri.decode(it) }

                if (downloadUrl != null) {
                    val image by viewModel.getImageByUrl(downloadUrl).collectAsState(initial = null)
                    if (image != null) {
                        DetailsScreen(
                            photo = image!!,
                            navController = navController,
                            toggleFavoriteForImage = { viewModel.toggleFavoriteForDetailsImage(it) },
                            onToggleGrayscale = { viewModel.toggleGrayscaleDetail() },
                            onBlurChangeFinished = { viewModel.updateBlurDetail(it) },
                            sliderPosition = viewModel.sliderPositionDetail,
                            onSliderValueChange = { viewModel.sliderPositionDetail = it },
                            isGray = viewModel.isGrayDetail.collectAsState(),
                            updateFavoriteStatusForDetails = {
                                viewModel.updateFavoriteStatusForDetails(
                                    it
                                )
                            },
                            buildImageUrl = viewModel::buildImageUrl,
                            isImageFavorite = viewModel::isImageFavorite
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Image not found")
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Invalid image URL")
                    }
                }
            }
        }
    }
}
