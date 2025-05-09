package com.zhangke.fread.common.di

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

typealias ViewModelKey = KClass<out ViewModel>

typealias ViewModelCreator = () -> ViewModel

interface ViewModelFactory
