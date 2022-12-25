package com.zhangke.utopia

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Created by ZhangKe on 2022/12/13.
 */

val Context.configDataStore: DataStore<Preferences> by preferencesDataStore(name = "config")