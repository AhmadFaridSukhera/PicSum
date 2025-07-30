package com.example.finalapplication1.Screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.finalapplication1.PictureViewModel


@Composable
fun MainScreen(viewModel: PictureViewModel) {

    val isFavorite by viewModel.isCurrentImageFavoriteFlow().collectAsState(initial = false)
    var imageUrl = viewModel.imageUrl
    var isImageLoading by remember { mutableStateOf(true) }


    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Hello, Ahmad", fontSize = 24.sp,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text("Welcome to PictureView")
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly

        ) {
            Text(
                text = "Photo Preview", fontSize = 24.sp,
                style = MaterialTheme.typography.labelLarge
            )


            Surface(
                modifier = Modifier.height(300.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Box {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Random picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        onLoading = {
                            isImageLoading = true
                        },
                        onSuccess = {
                            isImageLoading = false
                        },
                        onError = {
                            isImageLoading = false
                        }
                    )

                    if (isImageLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    IconButton(
                        onClick = { viewModel.refreshImage() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(50.dp)
                            .background(
                                Color.Black.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.6f),

                            )
                    }

                    IconButton(
                        onClick = {

                            Log.d(
                                "DEBUG",
                                "url: ${viewModel.imageUrl}\n fav: $isFavorite"
                            )

                            viewModel.toggleFavoriteForCurrentImage()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(50.dp)
                            .background(
                                Color.Black.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.6f)
                        )
                    }
                }
            }



            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BlurControl"
                    )
                    Slider(
                        value = viewModel.sliderPosition,
                        onValueChange = {value -> viewModel.sliderPosition=value },
                        onValueChangeFinished = {viewModel.updateBlur(viewModel.sliderPosition)},
                        steps = 9,
                        valueRange = 0f..10f,
                        modifier = Modifier.width(200.dp)
                    )
                }
                Button(onClick = viewModel::toggleGrayscale) {
                    Text(if (viewModel.isGray) "Colored" else "Grayscale")
                }
            }
        }


    }
}