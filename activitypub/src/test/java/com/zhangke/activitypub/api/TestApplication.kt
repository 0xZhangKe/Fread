package com.zhangke.activitypub.api

import com.zhangke.activitypub.ActivityPubApplication

/**
 * Created by ZhangKe on 2022/12/4.
 */
//{"id":"100075",
// "name":"utopia",
// "website":"https://0xzhangke.github.io/",
// "redirect_uri":"utopia://oauth.utopia",
// "client_id":"KHGSFM7oZY2_ZhaQRo25DfBRNwERZy7_iqZ_HjA5Sp8",
// "client_secret":"S0KKhfTQjEN-OZhJCHJYVl2OMYvz1wQiBoy8VDtfSzI",
// "vapid_key":"BOtsUyc0M779KJ0j-AAsv_wANoDuLkh6tTh6l1muA8OY3xVs34iGsUichN7VXNxcMoGKdSbyIEZsnsbJf6e46DA"}
val mastodonUtopiaApplication = ActivityPubApplication(
    id = "100075",
    name = "utopia",
    website = "https://0xzhangke.github.io/",
    redirectUri = "utopia://oauth.utopia",
    clientId = "KHGSFM7oZY2_ZhaQRo25DfBRNwERZy7_iqZ_HjA5Sp8",
    clientSecret = "S0KKhfTQjEN-OZhJCHJYVl2OMYvz1wQiBoy8VDtfSzI",
    vapidKey = "BOtsUyc0M779KJ0j-AAsv_wANoDuLkh6tTh6l1muA8OY3xVs34iGsUichN7VXNxcMoGKdSbyIEZsnsbJf6e46DA",
)

//m.cmx.im
val cmimUtopiaApplication = ActivityPubApplication(
    id = "96874",
    name = "utopia",
    website = "https://0xzhangke.github.io/",
    redirectUri = "https://0xzhangke.github.io/",
    clientId = "SgKN04a7lufOplcl_VEp41oTJfHlAr0k1vc6NVdss0g",
    clientSecret = "EM2bIEBGmOMRG4QERTRBx5FfUa496lJ9lJ30kmbZRfs",
    vapidKey = "BOtsUyc0M779KJ0j-AAsv_wANoDuLkh6tTh6l1muA8OY3xVs34iGsUichN7VXNxcMoGKdSbyIEZsnsbJf6e46DA",
)