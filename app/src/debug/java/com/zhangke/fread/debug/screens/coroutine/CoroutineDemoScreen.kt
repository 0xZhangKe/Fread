package com.zhangke.fread.debug.screens.coroutine

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.ktx.launchInScreenModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope
import kotlin.random.Random

class CoroutineDemoScreen : Screen {

    @Composable
    override fun Content() {
        val viewmodel = remember {
            CoroutineDemoViewModel()
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = { viewmodel.test() },
            ) {
                Text(text = "Test")
            }
        }
    }
}

class CoroutineDemoViewModel : ScreenModel {

    fun test() {
        launchInScreenModel {
            CoroutineDemo().load()
        }
    }
}

private class CoroutineDemo {

    suspend fun load() {
        Log.d("F_TEST", "start loading...")
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d("F_TEST", "catch error: $throwable")
        }
        val allJob = supervisorScope {
            val list = List(10) { it }
            list.map {
                async(exceptionHandler) {
                    Log.d("F_TEST", "start load $it")
                    doJob(it).also {
                        Log.d("F_TEST", "load $it finish")
                    }
                }
            }
        }
        try {
            allJob.awaitAll()
        } catch (e: Throwable) {
            Log.d("F_TEST", "await error(${e.localizedMessage}): ${e.stackTraceToString()}")
        }
        Log.d("F_TEST", "load finish")
    }

    private suspend fun doJob(index: Int): Int {
        if (index == 5) throw RuntimeException("custom exception")
        val delayDuration = Random(System.currentTimeMillis() + index)
            .nextInt(1000, 5000)
        delay(delayDuration.toLong())
        return index
    }
}
