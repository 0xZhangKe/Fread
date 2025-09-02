package com.zhangke.fread.commonbiz.shared.screen.image

import androidx.lifecycle.ViewModel
import com.zhangke.fread.common.ai.image.ImageDescriptionAiGenerator
import com.zhangke.fread.common.di.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class GenerateImageAltViewModel @Inject constructor(
    private val imageDescriptionAiGenerator: ImageDescriptionAiGenerator,
    @Assisted private val imageUri: String,
) : ViewModel() {


    fun interface Factory : ViewModelFactory {
        fun create(imageUri: String): GenerateImageAltViewModel
    }

    private val _uiState = MutableStateFlow(GenerateImageAltUiState.default(imageUri))
    val uiState = _uiState

}
