package com.example.finalapplication1.Screens


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalapplication1.PhotoItem
import com.example.finalapplication1.PictureViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteScreen(viewModel: PictureViewModel, navController: NavController) {
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
    when {
        favorites.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "You haven't added any items.")
            }
        }

        else -> {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalItemSpacing = 8.dp,
                content = {
                    items(favorites) { photo ->

                        PhotoItem(photo = photo, viewModel = viewModel, onClick = {
                            navController.navigate("detail/${photo.id}")

                        })
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    }
}