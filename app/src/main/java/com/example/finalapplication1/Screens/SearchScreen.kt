package com.example.finalapplication1.Screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalapplication1.PhotoItem
import com.example.finalapplication1.PictureViewModel


@Composable
fun SearchScreen(photoViewModel: PictureViewModel) {

    val photos by photoViewModel.photos.collectAsState()
    val isLoading by photoViewModel.isLoading.collectAsState()

    Column(modifier = Modifier
        .padding(16.dp)) {

        if (photos.isEmpty() && isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        } else {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                items(
                    count = photos.size,
                    key = { index -> photos[index].id }
                ) { index ->
                    // Show photo item
                    PhotoItem(photo = photos[index], viewModel = photoViewModel)


                    if (index == photos.size - 1) {
                        LaunchedEffect(key1 = photos.size) {
                            photoViewModel.loadMorePhotos()
                        }
                    }
                }
                // Add footer loader
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
            }
        }
    }
}
