package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.actor.ProfileView
import com.zhangke.fread.bluesky.internal.model.BlueskyProfile
import me.tatarka.inject.annotations.Inject

class BlueskyProfileAdapter @Inject constructor() {

    fun convertToProfile(profile: ProfileView): BlueskyProfile {
        return BlueskyProfile(
            did = profile.did.did,
            handle = profile.handle.handle,
            displayName = profile.displayName,
            description = profile.description,
            avatar = profile.avatar?.uri,
        )
    }
}
