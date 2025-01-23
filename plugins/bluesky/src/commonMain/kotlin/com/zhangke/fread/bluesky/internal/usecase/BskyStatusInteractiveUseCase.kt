package com.zhangke.fread.bluesky.internal.usecase

import com.atproto.repo.CreateRecordRequest
import com.atproto.repo.DeleteRecordRequest
import com.atproto.repo.StrongRef
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyLoggedAccountRepo
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Nsid

class BskyStatusInteractiveUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val loggedAccountRepo: BlueskyLoggedAccountRepo,

    ) {

    suspend operator fun invoke(
        role: IdentityRole,
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status?> {
        val client = clientManager.getClient(role)
        val loggedAccount =
            client.loggedAccountProvider() ?: return Result.failure(Exception("No logged account"))
        val repo = Did(loggedAccount.did)
        val blog = status.intrinsicBlog
        val subject = StrongRef(
            uri = AtUri(blog.url),
            cid = Cid(blog.id),
        )
        when (interaction) {
            is StatusInteraction.Like -> {
                return if (interaction.liked) {
                    unlike(status)
                } else {
                    like(
                        status = status,
                        client = client,
                        repo = repo,
                        subject = subject,
                    )
                }
            }

            is StatusInteraction.Forward -> {
                return if (interaction.forwarded) {
                    unForward(status)
                } else {
                    forward(
                        status = status,
                        client = client,
                        repo = repo,
                        subject = subject,
                    )
                }
            }

            is StatusInteraction.Delete -> {
                client.deleteRecordCatching(
                    DeleteRecordRequest(
                        repo = repo,
                        collection = Nsid("app.bsky.feed.post"),
                        rkey = status.id,
                    ),
                )
            }

            is StatusInteraction.Pin -> {

            }

            else -> return Result.failure(Exception("Bookmark not supported"))
        }
        return Result.success(status)
    }

    private suspend fun like(
        status: Status,
        client: BlueskyClient,
        repo: Did,
        subject: StrongRef,
    ): Result<Status> {
        val blog = status.intrinsicBlog
        return client.createRecordCatching(
            params = CreateRecordRequest(
                collection = Nsid("app.bsky.feed.like"),
                repo = repo,
                record = app.bsky.feed.Like(
                    subject = subject,
                    createdAt = Clock.System.now(),
                ).bskyJson(),
            ),
        ).map {
            updateStatusInteraction(
                status = status,
                updateInteraction = { action ->
                    if (action is StatusInteraction.Like) {
                        action.copy(liked = true, likeCount = action.likeCount + 1)
                    } else {
                        action
                    }
                },
                updateBlog = { blog.copy(liked = true, likeCount = (blog.likeCount ?: 0L) + 1L) },
            )
        }
    }

    private suspend fun unlike(
        status: Status,
    ): Result<Status> {

    }

    private suspend fun forward(
        status: Status,
        client: BlueskyClient,
        repo: Did,
        subject: StrongRef,
    ): Result<Status> {
        return client.createRecordCatching(
            CreateRecordRequest(
                collection = Nsid("app.bsky.feed.repost"),
                repo = repo,
                record = app.bsky.feed.Repost(
                    subject = subject,
                    createdAt = Clock.System.now(),
                ).bskyJson(),
            ),
        ).map {
            updateStatusInteraction(
                status = status,
                updateInteraction = { action ->
                    if (action is StatusInteraction.Forward) {
                        action.copy(forwarded = true, forwardCount = action.forwardCount + 1)
                    } else {
                        action
                    }
                },
                updateBlog = { blog ->
                    blog.copy(forward = true, forwardCount = (blog.forwardCount ?: 0L) + 1L)
                },
            )
        }
    }

    private fun unForward(
        status: Status,
    ): Result<Status> {


    }

    private fun updateStatusInteraction(
        status: Status,
        updateInteraction: (StatusInteraction) -> StatusInteraction,
        updateBlog: (Blog) -> Blog,
    ): Status {
        return when (status) {
            is Status.NewBlog -> {
                status.copy(
                    supportInteraction = status.supportInteraction.map(updateInteraction),
                    blog = updateBlog(status.blog),
                )
            }

            is Status.Reblog -> {
                status.copy(
                    supportInteraction = status.supportInteraction.map(updateInteraction),
                    reblog = updateBlog(status.reblog),
                )
            }
        }
    }
}
