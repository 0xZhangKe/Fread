package com.zhangke.utopia

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.composable.Toolbar
import com.zhangke.utopia.feeds.FeedsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UtopiaTheme {
                MainPage()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainPage() {
        Scaffold(
            topBar = {
                Toolbar(title = "MainPage")
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp)
            ) {
                Button(
                    onClick = {
                        FeedsActivity.open(this@MainActivity)
                    }
                ) {
                    Text(text = "Feeds Page")
                }
            }
        }
    }
}