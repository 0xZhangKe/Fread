package com.zhangke.fread.commonbiz.shared.screen.publish.alt

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.common.alttext.AltTextGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
class AltGeneratorState(
    private val altTextGenerator: AltTextGenerator,
    private val coroutineScope: CoroutineScope,
) {

    val generateState = mutableStateOf<GenerateState>(GenerateState.Idle)

    val available = mutableStateOf(false)

    val currentModel = mutableStateOf<String?>(null)

    init {
        coroutineScope.launch {
            available.value = altTextGenerator.available()
            currentModel.value = altTextGenerator.getCurrentModelName()
        }
    }

    private var generationJob: Job? = null

    fun generate(imageUri: String) {
        if (generateState.value is GenerateState.Generating) return
        generateState.value = GenerateState.Generating
        generationJob?.cancel()
        generationJob = coroutineScope.launch {
            if (altTextGenerator.currentModelSupportImage()) {
                altTextGenerator.generate(imageUri.toPlatformUri())
                    .onSuccess {
                        generateState.value = GenerateState.Success(it.text)
                    }.onFailure {
                        generateState.value = GenerateState.Failure(it)
                    }
            } else {
                generateState.value =
                    GenerateState.Failure(CurrentModelNotSupportImageException())
            }
        }
    }
}

sealed interface GenerateState {

    data object Idle : GenerateState

    data object Generating : GenerateState

    data class Success(val alt: String) : GenerateState

    data class Failure(val throwable: Throwable) : GenerateState
}

class CurrentModelNotSupportImageException : Exception("Current model does not support image input")
