package com.example.finalapplication1
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalapplication1.database.PicsumImage
import com.example.finalapplication1.database.PicsumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
open class PictureViewModel @Inject constructor(
    private val repository: PicsumRepository
) : ViewModel() {


    val favorites: Flow<List<PicsumImage>> = repository.getFavoriteImages()

    private val _photos = MutableStateFlow<List<PicsumImage>>(emptyList())
    val photos: StateFlow<List<PicsumImage>> = _photos

    // Tracks loading state for the progress indicator
    private val _isLoading = MutableStateFlow(false)
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite
    val isLoading: StateFlow<Boolean> = _isLoading


    // Tracks the current page for pagination
    private var currentPage = 1
    var isGray by mutableStateOf(false)

    var blur by mutableIntStateOf(0)
    var sliderPosition by mutableFloatStateOf(0f)

    var baseImageUrl by mutableStateOf<String?>(null)
    var imageUrl by mutableStateOf<String?>(null)

    init {
        loadInitialImage()
        loadMorePhotos()
    }

    private fun loadInitialImage() {
        val url = repository.getRandomImageUrlWithSeed(800, 600)
        baseImageUrl = url
        imageUrl = buildImageUrl(url, isGray, blur)
    }

    fun refreshImage() {
        val newUrl = repository.getRandomImageUrlWithSeed(800, 600)
        baseImageUrl = newUrl
        imageUrl = buildImageUrl(newUrl, isGray, blur)
    }

    fun toggleGrayscale() {
        isGray = !isGray
        imageUrl = buildImageUrl(baseImageUrl, isGray, blur)
        Log.d("ImageURL", "baseUrl=$baseImageUrl, gray=$isGray, blur=$blur")
    }


    fun updateBlur(value: Float) {
        sliderPosition = value
        blur = value.toInt()
        imageUrl = buildImageUrl(baseImageUrl, isGray, blur)
        Log.d("BlurUrl", "baseUrl=$baseImageUrl, gray=$isGray, blur=$blur")
    }

    private fun buildImageUrl(baseUrl: String?, isGray: Boolean, blur: Int): String {
        if (baseUrl == null) return ""
        return buildString {
            append(baseUrl)
            var hasQuery = baseUrl.contains("?")

            if (isGray) {
                append(if (!hasQuery) "?grayscale" else "&grayscale")
                hasQuery = true
            }

            if (blur > 0) {
                append(if (!hasQuery) "?blur=$blur" else "&blur=$blur")
            }
        }
    }

    fun loadMorePhotos() {

        if (_isLoading.value) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val newPhotos = repository.getImageList(currentPage)
                _photos.value += newPhotos

                currentPage++
            } finally {
                _isLoading.value = false
            }
        }

    }

    fun toggleFavoriteForCurrentImage() {
        val url = imageUrl ?: return
        val seed = url.split("/").getOrNull(4) ?: return

        viewModelScope.launch {
            val isFav = isImageFavorite(seed).first()
            if (isFav) {
                _isFavorite.value = false
                repository.deleteImageById(seed)
            } else {
                _isFavorite.value = true
                val image = repository.getImageInfoBySeed(seed)
                    val finalImage = image.copy(
                        id = seed,
                        isFavorite = true,
                        source = "main",
                        download_url = buildImageUrl(url, isGray, blur)
                    )
                    repository.insertImage(finalImage)

            }
        }
    }


    fun toggleFavoriteForImage(photo: PicsumImage) {
        viewModelScope.launch {
            val isFav = isImageFavorite(photo.id).first()
                if (isFav) {
                    repository.deleteImageById(photo.id)
                } else {
                    val updated = photo.copy(isFavorite = true, source = "favorites")
                    repository.insertImage(updated)
                }
            }
        }


    fun isCurrentImageFavoriteFlow(): Flow<Boolean> {
        val id = baseImageUrl?.split("/")?.getOrNull(4) ?: return MutableStateFlow(false)
        return isImageFavorite(id)
    }

    fun isImageFavorite(id: String): Flow<Boolean> {
        return favorites.map { list -> list.any { it.id == id } }
    }

}