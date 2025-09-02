package com.zhangke.fread.commonbiz.shared.screen.image

data class GenerateImageAltUiState(
    val imageUri: String,
) {

    companion object {

        fun default(
            imageUri: String,
        ) = GenerateImageAltUiState(
            imageUri = imageUri,
        )
    }
}
