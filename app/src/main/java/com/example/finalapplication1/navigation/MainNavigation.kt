package com.example.finalapplication1.navigation



import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.finalapplication1.database.PicSumImage
import kotlinx.coroutines.flow.Flow


@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val viewModel: PictureViewModel = viewModel()


    //main screen
    val imageUrl = viewModel.baseImageUrl
    val isFavorite = viewModel.isFavorite.collectAsState()
    val onToggleFavorite = viewModel::toggleFavoriteForCurrentImage
    val refreshImage = viewModel::refreshImage
    val onToggleGrayscale = viewModel::toggleGrayscale
    val onBlurChangeFinished = viewModel::updateBlur
    val sliderPosition = viewModel.sliderPosition
    val onSliderValueChange: (Float) -> Unit = { newValue ->
        viewModel.sliderPosition = newValue
    }
    val isGray = viewModel.isGray.collectAsState()
    val updateFavoriteStatusForCurrent = viewModel::updateFavoriteStatusForCurrent

    //favorite screen
    val photos = viewModel.photos.collectAsState()
    val loadMorePhotos = viewModel::loadMorePhotos
    val isFavoriteFlow: (PicSumImage) -> Flow<Boolean> =
        { image -> viewModel.isImageFavorite(image.download_url) }
    val isLoading = viewModel.isLoading.collectAsState()
    val toggleFavoriteForImage: (PicSumImage) -> Unit =
        { image -> viewModel.toggleFavoriteForImage(image) }

    val favorites = viewModel.favorites.collectAsState(initial = emptyList())
    //navigation lambda
    val onPhotoClick: (String) -> Unit = remember(navController) {
        { download_url -> navController.navigate("detail/$download_url") }
    }


    // details
    val toggleFavoriteForImageDetails = viewModel::toggleFavoriteForDetailsImage
    val onToggleGrayscaleDetails = viewModel::toggleGrayscaleDetail
    val onBlurChangeFinishedDetails = viewModel::updateBlurDetail
    val sliderPositionDetail = viewModel.sliderPositionDetail
    val onSliderValueChangeDetail: (Float) -> Unit = { newValue ->
        viewModel.sliderPositionDetail = newValue
    }
    val updateFavoriteStatusForDetails = viewModel::updateFavoriteStatusForDetails
    val buildImageUrl = viewModel::buildImageUrl
    val isImageFavorite = viewModel::isImageFavorite
    val onBackClick: () -> Unit = { navController.popBackStack() }
    val isGrayDetail = viewModel.isGrayDetail.collectAsState()


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
                    imageUrl,
                    isFavorite,
                    onToggleFavorite,
                    refreshImage,
                    onToggleGrayscale,
                    onBlurChangeFinished,
                    sliderPosition,
                    onSliderValueChange,
                    isGray,
                    updateFavoriteStatusForCurrent,
                )
            }
            composable(BottomNavItem.Search.route) {
                SearchScreen(
                    photos,
                    loadMorePhotos,
                    isFavoriteFlow = isFavoriteFlow,
                    isLoading,
                    onPhotoClick,
                    toggleFavoriteForImage = toggleFavoriteForImage
                )
            }
            composable(BottomNavItem.Favorites.route) {
                FavoriteScreen(
                    onPhotoClick,
                    favorites,
                    isFavorite = isFavoriteFlow,
                    toggleFavoriteForImage = toggleFavoriteForImage
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
                            onBackClick = onBackClick,
                            toggleFavoriteForImage = toggleFavoriteForImageDetails,
                            onToggleGrayscale = onToggleGrayscaleDetails,
                            onBlurChangeFinished = onBlurChangeFinishedDetails,
                            sliderPosition = sliderPositionDetail,
                            onSliderValueChange = onSliderValueChangeDetail,
                            isGray = isGrayDetail,
                            updateFavoriteStatusForDetails,
                            buildImageUrl,
                            isImageFavorite
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