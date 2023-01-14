package com.zhangke.utopia.activitypubapp

import com.zhangke.utopia.activitypubapp.providers.ActivityPubProviderFactory
import com.zhangke.utopia.blogprovider.BlogSourceInterpreter
import com.zhangke.utopia.blogprovider.StatusProviderClient
import com.zhangke.utopia.blogprovider.StatusProviderFactory

class ActivityPubStatusProviderClient: StatusProviderClient {

    override val statusProviderFactory: StatusProviderFactory = ActivityPubProviderFactory()

    override val sourceInterpreter: BlogSourceInterpreter = ActivityPubSourceInterpreter
}