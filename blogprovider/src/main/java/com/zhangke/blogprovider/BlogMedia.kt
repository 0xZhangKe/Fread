package com.zhangke.blogprovider

/**
 * Created by ZhangKe on 2022/12/4.
 */
sealed class BlogMedia {

    class Image(val url: String) : BlogMedia()

    class Video(val url: String) : BlogMedia()
}