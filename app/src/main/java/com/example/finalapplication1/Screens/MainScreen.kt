package com.example.finalapplication1.Screens



import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
    isGray: State<Boolean>
) {

    var isImageLoading by remember { mutableStateOf(true) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),

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
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    val newScale = (zoom * scale).coerceIn(1f, 4f)
                                    scale = newScale
                                    if (newScale > 1f) {
                                        val maxX = (newScale - 1f) * size.width / 2
                                        val maxY = (newScale - 1f) * size.height / 2
                                        offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                                        offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                                    } else {
                                        offsetX = 0f
                                        offsetY = 0f

                                    }

                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = { _: Offset ->
                                        if (scale > 1f) {
                                            scale = 1f
                                            offsetY = 0f
                                            offsetX = 0f
                                        } else {
                                            scale = 2f
                                        }
                                    }
                                )
                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY,
                                transformOrigin = TransformOrigin.Center
                            )
                            .fillMaxSize()
                            .blur(
                                sliderPosition.dp,
                                edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp))
                            ),
                        colorFilter = if (isGray.value)
                            ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                        else null,
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
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.fillMaxSize(0.6f)
                        )
                    }
                    Button(

                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onErrorContainer),
                        onClick = {

                            onToggleGrayscale()
                        }
                    ) {
                        Text(if (isGray.value) "Colored" else "Grayscale")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                    Text(text = "Add Blur")

                    Slider(
                        value = sliderPosition,
                        onValueChange = { value -> onSliderValueChange(value) },
                        onValueChangeFinished = {
                            onBlurChangeFinished(sliderPosition)
                        },
                        steps = 9,
                        valueRange = 0f..10f,
                        modifier = Modifier.width(300.dp),
                        colors = SliderDefaults.colors(
                            MaterialTheme.colorScheme.onErrorContainer,
                            activeTrackColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                    )
                }
            }


        }
    }
        }






