package com.zhangke.utopia.feeds

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.commit
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.composable.Toolbar

class FeedsActivity : AppCompatActivity() {

    companion object{

        fun open(activity: Activity){
            activity.startActivity(Intent(activity, FeedsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UtopiaTheme {
                FeedsContainerPage(
                    navigationBackClick = ::finish
                )
            }
        }
    }

    @Composable
    fun FeedsContainerPage(
        navigationBackClick: () -> Unit
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = "Title",
                    navigationBackClick = navigationBackClick
                )
            }
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    FrameLayout(context).apply {
                        id = ViewCompat.generateViewId()
                    }
                },
                update = {
                    val tag = "feeds_fragment"
                    val fragmentAlreadyAdded = supportFragmentManager.findFragmentByTag(tag) != null
                    if (!fragmentAlreadyAdded) {
                        supportFragmentManager.commit {
                            add(it.id, FeedsFragment.newInstance(), tag)
                        }
                    }
                }
            )
        }
    }
}