package com.example.finalapplication1.navigation



import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
                )
            }
            composable(BottomNavItem.Search.route) {
                SearchScreen(
                    photos = viewModel.photos.collectAsState(),
                    loadMorePhotos = { viewModel.loadMorePhotos() },
                    isFavorite = { image -> viewModel.isImageFavorite(image.id) },
                    isLoading = viewModel.isLoading.collectAsState(),
                    navController = navController,
                    toggleFavoriteForImage = { image -> viewModel.toggleFavoriteForImage(image) },
                )
            }
            composable(BottomNavItem.Favorites.route) {
                FavoriteScreen(
                    navController = navController,
                    favorites = viewModel.favorites.collectAsState(initial = emptyList()),
                    isFavorite = { image -> viewModel.isImageFavorite(image.id) },
                    toggleFavoriteForImage = { image -> viewModel.toggleFavoriteForImage(image) }
                )
            }
            composable(
                route = DetailRoute.route,
                arguments = listOf(navArgument("photoId") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val photoId = backStackEntry.arguments?.getString("photoId")
                val allPhotos =
                    viewModel.photos.collectAsState().value + viewModel.favorites.collectAsState(
                        initial = (emptyList())
                    ).value
                val image = allPhotos.find { it.id == photoId }

                if (image != null) {
                    DetailsScreen(
                        photo = image,
                        navController,
                        isFavorite = { image -> viewModel.isImageFavorite(image.id) },
                        toggleFavoriteForImage = { image ->
                            viewModel.toggleFavoriteForDetailsImage(
                                image
                            )
                        },
                        onToggleGrayscale = { viewModel.toggleGrayscaleDetail() },
                        onBlurChangeFinished = { viewModel.updateBlurDetail(it) },
                        sliderPosition = viewModel.sliderPositionDetail,
                        onSliderValueChange = { viewModel.sliderPositionDetail = it },
                        isGray = viewModel.isGrayDetail.collectAsState(),

                        )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Image not found")
                    }
                }
            }

        }
    }
}
