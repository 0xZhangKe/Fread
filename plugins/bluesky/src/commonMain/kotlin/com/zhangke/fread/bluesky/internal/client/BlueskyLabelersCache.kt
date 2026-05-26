package com.zhangke.fread.bluesky.internal.client

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds the list of labeler DIDs the current user is subscribed to, so we can
 * inject them into the `atproto-accept-labelers` HTTP header on every Bluesky
 * AppView request. Without this header, custom labelers (skywatch.blue,
 * Aegis, etc.) won't return any labels — only Bluesky's own moderation
 * labeler does.
 *
 * Populated at startup (and after login) from `app.bsky.actor.getPreferences`.
 * Single-account today; multi-account refinement can layer on later.
 */
class BlueskyLabelersCache {

    private val cached = MutableStateFlow<List<String>>(emptyList())

    /** DID of Bluesky's official moderation labeler, always included. */
    private val defaultLabelerDid = "did:plc:ar7c4by46qjdydhdevvrndac"

    fun update(labelerDids: List<String>) {
        cached.update { labelerDids }
    }

    /** DIDs that should be sent on outgoing requests (default labeler + subscribed). */
    fun activeLabelers(): List<String> {
        val subscribed = cached.value
        if (defaultLabelerDid in subscribed) return subscribed
        return listOf(defaultLabelerDid) + subscribed
    }
}
