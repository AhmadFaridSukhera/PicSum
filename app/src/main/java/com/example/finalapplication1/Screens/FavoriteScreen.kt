package com.example.finalapplication1.Screens


import android.net.Uri
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
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalapplication1.PhotoItem
import com.example.finalapplication1.database.PicSumImage
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteScreen(
    onPhotoClick: (String) -> Unit,
    favorites: State<List<PicSumImage>>,
    isFavorite: (PicSumImage) -> Flow<Boolean>,
    toggleFavoriteForImage: (PicSumImage) -> Unit,

    ) {

    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
    when {
        favorites.value.isEmpty() -> {
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
                    items(favorites.value) { photo ->
                        val encodedUrl = Uri.encode(photo.download_url)
                        PhotoItem(photo = photo, isFavorite, toggleFavoriteForImage, onClick = {
                            onPhotoClick(encodedUrl)

                        })
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    }
}