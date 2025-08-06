package com.example.finalapplication1


import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.finalapplication1.database.PicSumImage
import kotlinx.coroutines.flow.Flow


@Composable
fun PhotoItem(
    photo: PicSumImage,
    isFavorite: (PicSumImage) -> Flow<Boolean>,
    onToggleFavorite: (PicSumImage) -> Unit,
    onClick: () -> Unit
) {
    var isImageLoading by remember { mutableStateOf(true) }
    var favState = isFavorite(photo).collectAsState(initial = false).value


    Card(
        modifier = Modifier
            .aspectRatio(photo.width.toFloat() / photo.height.toFloat())
            .combinedClickable(
                onClick = onClick,
            ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
            Box(
                modifier = (Modifier
                    .fillMaxSize())

            ) {
                AsyncImage(
                    model = photo.download_url,
                    modifier = Modifier
                        .fillMaxWidth(),

                    contentScale = ContentScale.Crop,

                    contentDescription = photo.id,

                    onSuccess = {
                        isImageLoading = false
                    },
                    onError = {
                        isImageLoading = false
                    },
                    onLoading = {
                        isImageLoading = true
                    })
                if (isImageLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }
                }
                IconButton(
                    onClick = {
                        onToggleFavorite(photo)
                    }, modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (favState) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }


    }
    }






