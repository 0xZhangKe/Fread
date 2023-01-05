package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.ActivityPubApplication
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.oauth.ActivityPubOAuthor

//fun newActivityPubClient(
//    application: ActivityPubApplication,
//    url: String,
//): ActivityPubClient {
//    return ActivityPubClient(application = application,
//        retrofit = newRetrofit(url),
//        gson = globalGson,
//        onAuthorizeFailed = {
//            ActivityPubOAuthor.startOauth(application)
//        })
//}