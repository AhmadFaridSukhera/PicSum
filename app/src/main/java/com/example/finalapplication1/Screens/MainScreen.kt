package com.example.finalapplication1.Screens


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun MainScreen(
    imageUrl: String?,
    isFavorite: State<Boolean>,
    onToggleFavorite: () -> Unit,
    refreshImage: () -> Unit,
    onToggleGrayscale: () -> Unit,
    onBlurChangeFinished: (Float) -> Unit,
    sliderPosition: Float,
    onSliderValueChange: (Float) -> Unit,
    isGray: Boolean
) {

    var isImageLoading by remember { mutableStateOf(true) }



    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceDim),

        ) {
        Column(
            modifier = Modifier.scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally,


            ) {
            Box(modifier = Modifier
                .height(600.dp)
                .clip(RoundedCornerShape(10.dp))) {
                    AsyncImage(
                        model = imageUrl ?: "",
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
                        onClick = { refreshImage() },
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


            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceDim)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {

                            onToggleFavorite()
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)

                    ) {
                        Icon(
                            imageVector = if (isFavorite.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite.value) "Unfavorite" else "Favorite",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(0.6f)
                        )
                    }
                    Button(

                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onErrorContainer),
                        onClick = { onToggleGrayscale() }
                    ) {
                        Text(if (isGray) "Colored" else "Grayscale")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surfaceDim)
            ) {
                    Text(text = "Add Blur")

                    Slider(
                        value = sliderPosition,
                        onValueChange = { value -> onSliderValueChange(value) },
                        onValueChangeFinished = { onBlurChangeFinished(sliderPosition) },
                        steps = 9,
                        valueRange = 0f..10f,
                        modifier = Modifier.width(300.dp),
                        colors = SliderDefaults.colors(MaterialTheme.colorScheme.onErrorContainer),
                    )
                }
            }


        }
    }
        }





