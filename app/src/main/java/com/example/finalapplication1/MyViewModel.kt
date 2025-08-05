package com.example.finalapplication1

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalapplication1.database.PicSumImage
import com.example.finalapplication1.database.PicSumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PictureViewModel @Inject constructor(
    private val repository: PicSumRepository
) : ViewModel() {


    val favorites: Flow<List<PicSumImage>> = repository.getFavoriteImages()
    private val _photos = MutableStateFlow<List<PicSumImage>>(emptyList())
    val photos: StateFlow<List<PicSumImage>> = _photos
    private val _isLoading = MutableStateFlow(false)
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite
    val isLoading: StateFlow<Boolean> = _isLoading
    private var currentPage = 1
    private val _isGray = MutableStateFlow(false)
    var isGray: StateFlow<Boolean> = _isGray
    private val _isGrayDetail = MutableStateFlow(false)
    var isGrayDetail: StateFlow<Boolean> = _isGrayDetail
    var blur by mutableIntStateOf(0)
    var blurDetail by mutableIntStateOf(0)
    var sliderPosition by mutableFloatStateOf(0f)
    var sliderPositionDetail by mutableFloatStateOf(0f)
    var baseImageUrl by mutableStateOf<String?>(null)
    var imageUrl by mutableStateOf<String?>(null)
    val _photo = mutableStateOf<PicSumImage?>(null)
    val detailPhoto = mutableStateOf<PicSumImage?>(null)


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
            isFavorite = false,
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
                isFavorite = false,
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
        imageUrl = buildImageUrl(baseImageUrl, _isGray.value, blur)
        _photo.value = _photo.value?.copy(download_url = imageUrl ?: _photo.value!!.download_url)
        updateFavoriteStatusForCurrent(imageUrl ?: "")
    }
    fun toggleGrayscaleDetail() {
        detailPhoto.value?.let { updateFavoriteStatusForDetails(it) }
        _isGrayDetail.value = !_isGrayDetail.value

        Log.d("ImageURL", "baseUrl=$baseImageUrl, gray=$isGrayDetail, blur=$blur")
    }

    fun getImageByUrl(downloadUrl: String): Flow<PicSumImage?> {
        return combine(photos, favorites) { photosList, favList ->
            favList.find { it.download_url == downloadUrl }
                ?: photosList.find { it.download_url == downloadUrl }
        }
    }


    fun updateBlur(value: Float) {
        sliderPosition = value
        blur = value.toInt()
        imageUrl = buildImageUrl(baseImageUrl, _isGray.value, blur)
        _photo.value?.let { updateFavoriteStatusForCurrent(imageUrl ?: "") }
    }

    fun updateBlurDetail(value: Float) {
        sliderPositionDetail = value
        blurDetail = value.toInt()
        detailPhoto.value?.let { updateFavoriteStatusForDetails(it) }

    }

    fun buildImageUrl(baseUrl: String?, isGray: Boolean, blur: Int): String {
        if (baseUrl == null) return ""
        Log.d("BlurUrl", "baseUrl=$baseUrl, gray=$isGray, blur=$blur")

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
            val updatedUrl = buildImageUrl(currentImage.download_url, _isGray.value, blur)
            val updatedImage = currentImage.copy(
                isFavorite = true,
                source = "main",
                download_url = updatedUrl
            )
            val isFav = isImageFavorite(updatedUrl).first()

            if (isFav) {
                _isFavorite.value = false
                repository.deleteImageByUrl(currentImage.download_url)
            } else {
                _isFavorite.value = true
                repository.insertImage(updatedImage)
            }
        }
    }


    fun toggleFavoriteForImage(photo: PicSumImage) {
        viewModelScope.launch {
            val isFav = isImageFavorite(photo.download_url).first()
                if (isFav) {
                    _isFavorite.value = false
                    repository.deleteImageByUrl(photo.download_url)
                } else {
                    val updated = photo.copy(isFavorite = true, source = "favorites")
                    _isFavorite.value = true
                    repository.insertImage(updated)
                }
            }


    }

    fun toggleFavoriteForDetailsImage(photo: PicSumImage) {
        viewModelScope.launch {
            val updatedUrl = buildImageUrl(photo.download_url, _isGrayDetail.value, blurDetail)
            val updatedImage = photo.copy(
                isFavorite = true,
                source = "main",
                download_url = updatedUrl
            )

            val isFav = isImageFavorite(updatedUrl).first()
            if (isFav) {
                _isFavorite.value = false
                repository.deleteImageByUrl(updatedImage.download_url)
            } else {
                _isFavorite.value = true
                Log.d(
                    "Blurping",
                    "baseUrl=${updatedImage.download_url}, gray=${_isGrayDetail.value}, blur=$blurDetail"
                )
                repository.insertImage(updatedImage)
            }
        }
    }

    fun updateFavoriteStatusForDetails(photo: PicSumImage) {
        viewModelScope.launch {
            val updatedUrl = buildImageUrl(photo.download_url, _isGrayDetail.value, blurDetail)
            val isFav = isImageFavorite(updatedUrl).first()
            _isFavorite.value = isFav
        }
    }

    fun updateFavoriteStatusForCurrent(imageUrl: String) {
        viewModelScope.launch {
            val updatedUrl = buildImageUrl(imageUrl, _isGray.value, blur)
            val isFav = isImageFavorite(updatedUrl).first()
            _isFavorite.value = isFav
        }
    }

    fun isImageFavorite(downloadUrl: String): Flow<Boolean> {
        return favorites.map { list -> list.any { it.download_url == downloadUrl } }
    }



}