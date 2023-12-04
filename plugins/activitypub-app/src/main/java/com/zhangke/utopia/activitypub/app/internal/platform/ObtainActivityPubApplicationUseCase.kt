package com.zhangke.utopia.activitypub.app.internal.platform

//import com.zhangke.activitypub.ActivityPubApplication
//import javax.inject.Inject
//
//private val androidApplication = ActivityPubApplication(
//    host = "androiddev.social",
//    id = "21938",
//    name = "utopia",
//    website = "https://0xzhangke.github.io/",
//    redirectUri = "utopia://oauth.utopia",
//    clientId = "EtpLdCu8ooobmOfChhR5EsFTjqGwexSi2SB7MFDAkF4",
//    clientSecret = "FL0vivmiP2JQHKPfnn8Ejybh71eosd2mj1AiJGNkj94",
//    vapidKey = "BEfliSoCdIG4Le28IdcZjZxOR1e7LkelSNfMVP9CA7MWy0PrDmYY47inEdK4MpNf2XwvpO_bZfmq9gFCE1W6ClM=",
//)
//
//private val cmxApplication = ActivityPubApplication(
//    host = "m.cmx.im",
//    id = "100075",
//    name = "utopia",
//    website = "https://0xzhangke.github.io/",
//    redirectUri = "utopia://oauth.utopia",
//    clientId = "KHGSFM7oZY2_ZhaQRo25DfBRNwERZy7_iqZ_HjA5Sp8",
//    clientSecret = "S0KKhfTQjEN-OZhJCHJYVl2OMYvz1wQiBoy8VDtfSzI",
//    vapidKey = "BOtsUyc0M779KJ0j-AAsv_wANoDuLkh6tTh6l1muA8OY3xVs34iGsUichN7VXNxcMoGKdSbyIEZsnsbJf6e46DA",
//)

//private val hostToApplication = mapOf(
//    cmxApplication.host to cmxApplication,
//    androidApplication.host to androidApplication,
//)
//
//class ObtainActivityPubApplicationUseCase @Inject constructor() {
//
//    suspend operator fun invoke(host: String): ActivityPubApplication {
//        return obtainActivityPubApplication(host)
//    }
//
//    private fun obtainActivityPubApplication(host: String): ActivityPubApplication {
//        return hostToApplication[host] ?: cmxApplication
//    }
//val mastodonUtopiaApplication = ActivityPubApplication(
//    id = "100075",
//    name = "utopia",
//    website = "https://0xzhangke.github.io/",
//    redirectUri = "utopia://oauth.utopia",
//    clientId = "KHGSFM7oZY2_ZhaQRo25DfBRNwERZy7_iqZ_HjA5Sp8",
//    clientSecret = "S0KKhfTQjEN-OZhJCHJYVl2OMYvz1wQiBoy8VDtfSzI",
//    vapidKey = "BOtsUyc0M779KJ0j-AAsv_wANoDuLkh6tTh6l1muA8OY3xVs34iGsUichN7VXNxcMoGKdSbyIEZsnsbJf6e46DA",
//)
//
////m.cmx.im
//val cmimUtopiaApplication = ActivityPubApplication(
//    id = "96874",
//    name = "utopia",
//    website = "https://0xzhangke.github.io/",
//    redirectUri = "https://0xzhangke.github.io/",
//    clientId = "SgKN04a7lufOplcl_VEp41oTJfHlAr0k1vc6NVdss0g",
//    clientSecret = "EM2bIEBGmOMRG4QERTRBx5FfUa496lJ9lJ30kmbZRfs",
//    vapidKey = "BOtsUyc0M779KJ0j-AAsv_wANoDuLkh6tTh6l1muA8OY3xVs34iGsUichN7VXNxcMoGKdSbyIEZsnsbJf6e46DA",
//)
//}