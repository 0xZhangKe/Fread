package com.zhangke.utopia.activitypub

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Created by ZhangKe on 2022/12/4.
 */
class ActivityPubOAuthPerformer {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    fun performOauth(activity: Activity ,oauthUrl: String){
        val packageName = "com.android.chrome"
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.intent.setPackage(packageName)
        customTabsIntent.launchUrl(activity, Uri.parse(oauthUrl))
    }
}