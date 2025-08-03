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

    private val _isGray = MutableStateFlow(false)
    var isGray: StateFlow<Boolean> = _isGray

    var blur by mutableIntStateOf(0)
    var sliderPosition by mutableFloatStateOf(0f)

    var baseImageUrl by mutableStateOf<String?>(null)
    var imageUrl by mutableStateOf<String?>(null)
    val _photo = mutableStateOf<PicsumImage?>(null)


    init {
        viewModelScope.launch {
        loadInitialImage()
        }
        loadMorePhotos()
    }

    suspend fun loadInitialImage() {
        val photo = repository.getImageBySeed()

        baseImageUrl = photo.download_url
        imageUrl = buildImageUrl(baseImageUrl, _isGray.value, blur)

        val finalImage = photo.copy(
            isFavorite = true,
            source = "main",
            download_url = imageUrl ?: photo.download_url
        )

        _photo.value = finalImage
    }


    fun refreshImage() {
        viewModelScope.launch {
            val photo = repository.getImageBySeed()

            baseImageUrl = photo.download_url
            imageUrl = buildImageUrl(baseImageUrl, _isGray.value, blur)

            val finalImage = photo.copy(
                isFavorite = true,
                source = "main",
                download_url = imageUrl ?: photo.download_url
            )
            _isFavorite.value = false
            sliderPosition = 0f
            _isGray.value = false
            _photo.value = finalImage

        }
    }

    fun toggleGrayscale() {
        _isGray.value = !_isGray.value
        //imageUrl = buildImageUrl(baseImageUrl, isGray, blur)
        Log.d("ImageURL", "baseUrl=$baseImageUrl, gray=$isGray, blur=$blur")
    }


    fun updateBlur(value: Float) {
        sliderPosition = value
        blur = value.toInt()
        imageUrl = buildImageUrl(baseImageUrl, _isGray.value, blur)
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
        viewModelScope.launch {
            val currentImage = _photo.value ?: return@launch
            val isFav = isImageFavorite(currentImage.id).first()

            if (isFav) {
                repository.deleteImageById(currentImage.id)
                _isFavorite.value = false
            } else {
                _isFavorite.value = true
                val updatedImage = currentImage.copy(
                    isFavorite = true,
                    source = "main",
                    download_url = buildImageUrl(currentImage.download_url, _isGray.value, blur)
                )
                repository.insertImage(updatedImage)

            }
        }
    }

    fun toggleFavoriteForImage(photo: PicsumImage) {
        viewModelScope.launch {
            val isFav = isImageFavorite(photo.id).first()
                if (isFav) {
                    _isFavorite.value = false
                    repository.deleteImageById(photo.id)
                } else {
                    val updated = photo.copy(isFavorite = true, source = "favorites")
                    _isFavorite.value = true
                    repository.insertImage(updated)
                }
            }
        }


    fun isImageFavorite(id: String): Flow<Boolean> {
        return favorites.map { list -> list.any { it.id == id } }
    }

}