package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.status.uri.StatusProviderUri

val StatusProviderUri.isActivityPubUri: Boolean get() = host == ACTIVITY_PUB_HOST
