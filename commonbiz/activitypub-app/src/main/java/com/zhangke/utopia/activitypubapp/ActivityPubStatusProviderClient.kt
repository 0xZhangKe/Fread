package com.zhangke.utopia.activitypubapp

import com.zhangke.utopia.activitypubapp.oauth.ActivityPubAuthorizer
import com.zhangke.utopia.activitypubapp.providers.ActivityPubProviderFactory
import com.zhangke.utopia.activitypubapp.source.ActivityPubSourceRestorer
import com.zhangke.utopia.status_provider.*

class ActivityPubStatusProviderClient : StatusProviderClient {

    override val statusProviderFactory: StatusProviderFactory = ActivityPubProviderFactory()

    override val sourceResolver: BlogSourceResolver = ActivityPubSourceResolver()

    override val sourceRestorer: BlogSourceRestorer = ActivityPubSourceRestorer()

    override val authorizer: StatusProviderAuthorizer = ActivityPubAuthorizer()
}