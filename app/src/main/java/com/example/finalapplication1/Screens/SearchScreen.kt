package com.example.finalapplication1.Screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalapplication1.PhotoItem
import com.example.finalapplication1.database.PicsumImage
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    photos: State<List<PicsumImage>>,
    loadMorePhotos: () -> Unit,
    isFavorite: (PicsumImage) -> Flow<Boolean>,
    isLoading: State<Boolean>,
    navController: NavController,
    toggleFavoriteForImage: (PicsumImage) -> Unit
) {


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceDim
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp)
        ) {
            LazyVerticalStaggeredGrid(
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalItemSpacing = 8.dp,
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                content = {
                    items(
                        count = photos.value.size,
                        key = { index -> photos.value[index].id }
                    ) { index ->
                        val photo = photos.value[index]
                        PhotoItem(
                            photo = photo,
                            isFavorite,
                            onToggleFavorite = toggleFavoriteForImage,
                            onClick = { navController.navigate("detail/${photo.id}") }
                        )


                        if (index == photos.value.size - 1) {
                            LaunchedEffect(key1 = photos.value.size) {
                                loadMorePhotos()
                            }
                        }
                    }


                    if (isLoading.value && photos.value.isNotEmpty()) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            )


            if (isLoading.value && photos.value.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}