package com.example.finalapplication1.Screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.example.finalapplication1.PhotoItem
import com.example.finalapplication1.PictureViewModel


@Composable
fun FavoriteScreen(viewModel: PictureViewModel) {
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

    when {
        favorites.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "You haven't added any items.")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(favorites!!) { photo ->
                    PhotoItem(photo = photo, viewModel = viewModel)
                }
            }
        }
    }
}
