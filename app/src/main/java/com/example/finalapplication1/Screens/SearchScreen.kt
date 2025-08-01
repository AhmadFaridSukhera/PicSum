package com.example.finalapplication1.Screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalapplication1.PhotoItem
import com.example.finalapplication1.PictureViewModel


@Composable
fun SearchScreen(photoViewModel: PictureViewModel, navController: NavController) {

    val photos by photoViewModel.photos.collectAsState()
    val isLoading by photoViewModel.isLoading.collectAsState()

    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceDim),

        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp)
    ) {
            LazyVerticalStaggeredGrid(


                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalItemSpacing = 8.dp,
                columns = StaggeredGridCells.Adaptive(200.dp),
                content = {
                items(
                    count = photos.size,
                    key = { index -> photos[index].id }
                ) { index ->
                    val photo = photos[index]
                    PhotoItem(
                        photo = photo,
                        viewModel = photoViewModel,
                        onClick = { navController.navigate("detail/${photo.id}") })

                    if (index == photos.size - 1) {
                        LaunchedEffect(key1 = photos.size) {
                            photoViewModel.loadMorePhotos()
                        }
                    }
                }
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                }
                },
                modifier = Modifier.fillMaxSize()
            )
            if (photos.isEmpty() && isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(200.dp)
                )
        }
        }
    }
}

