package com.example.finalapplication1.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.finalapplication1.database.PicsumImage
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    photo: PicsumImage, navController: NavController,
    isFavorite: (PicsumImage) -> Flow<Boolean>,
    toggleFavoriteForImage: (PicsumImage) -> Unit,
    onToggleGrayscale: () -> Unit,
    onBlurChangeFinished: (Float) -> Unit,
    sliderPosition: Float,
    onSliderValueChange: (Float) -> Unit,
    isGray: State<Boolean>
) {


    var showSheet by remember { mutableStateOf(false) }
    LaunchedEffect(photo.id) {
        onSliderValueChange(0f)
        onBlurChangeFinished(0f)
        if (isGray.value) onToggleGrayscale()
    }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(photo.width.toFloat() / photo.height.toFloat())
                .clip(RoundedCornerShape(10.dp))
        ) {
            AsyncImage(
                model = photo.download_url,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        sliderPosition.dp,
                        edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp))
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(1f, 4f)
                            scale = newScale

                            if (newScale > 1f) {
                                val maxX = ((newScale - 1f) * size.width / 2)
                                val maxY = ((newScale - 1f) * size.height / 2)
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
                    ),
                contentDescription = photo.source,
                contentScale = ContentScale.Crop,
                colorFilter = if (isGray.value)
                    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                else null,
            )


            if (showSheet) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Image Details", color = Color.White, fontSize = 24.sp)
                    Text("Id: ${photo.id}", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        "Captured By: ${photo.author}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Width: ${photo.width}", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        "Height: ${photo.height}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(50.dp)
                    .background(
                        Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.6f),
                )
            }
            IconButton(
                onClick = { toggleFavoriteForImage(photo) },
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
                    imageVector = if (isFavorite(photo).collectAsState(initial = false).value) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = "favorite",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.6f),
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showSheet = !showSheet },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    if (showSheet) Text("Hide Details") else Text("Show Details")
                }
                Button(
                    onClick = { onToggleGrayscale() },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer
                    )
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
                    onValueChangeFinished = { onBlurChangeFinished(sliderPosition) },
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

//        if (showSheet) {
//            ModalBottomSheet(
//                onDismissRequest = { showSheet = false },
//                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Image Details", fontSize = 24.sp)
//                    Text("Id: ${photo.id}")
//                    Text("Captured By: ${photo.author}")
//                    Text("Width: ${photo.width}")
//                    Text("Height: ${photo.height}")
//                }
//            }
//        }
