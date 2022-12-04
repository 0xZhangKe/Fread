package com.zhangke.activitypub

/**
 * Created by ZhangKe on 2022/12/4.
 */

// id=1563275,
// name=utopia,
// website=https://0xzhangke.github.io/,
// redirectUri=https://0xzhangke.github.io/,
// clientId=_RUAjDpEsX2r-XIGPdx6matQNpPqb53vhP3e_3GnPQY,
// clientSecret=nnoiysicDthQkFo_oKeD7-TTGqqmI679b7gndJPwQnc,
// vapidKey=BCk-QqERU0q-CfYZjcuB6lnyyOYfJ2AifKqfeGIm7Z-HiTU5T9eTG5GxVA0_OH5mMlI4UkkDTpaZwozy0TzdZ2M=

data class ActivityPubApplication(
    val id: String,
    val name: String,
    val website: String,
    val redirectUri: String,
    val clientId: String,
    val clientSecret: String,
    val vapidKey: String,
)