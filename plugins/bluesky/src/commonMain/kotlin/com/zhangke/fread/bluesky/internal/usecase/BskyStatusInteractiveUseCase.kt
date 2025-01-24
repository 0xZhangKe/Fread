package com.zhangke.fread.bluesky.internal.usecase

import com.atproto.repo.CreateRecordRequest
import com.atproto.repo.CreateRecordResponse
import com.atproto.repo.DeleteRecordRequest
import com.atproto.repo.DeleteRecordResponse
import com.atproto.repo.StrongRef
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.likeRecord
import com.zhangke.fread.bluesky.internal.client.repostRecord
import com.zhangke.fread.bluesky.internal.client.rkey
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusInteraction
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Nsid
import sh.christian.ozone.api.model.JsonContent

class BskyStatusInteractiveUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val updateProfileRecord: UpdateProfileRecordUseCase,
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
                    client.unlike(status, repo)
                } else {
                    client.like(
                        status = status,
                        repo = repo,
                        subject = subject,
                    )
                }
            }

            is StatusInteraction.Forward -> {
                return if (interaction.forwarded) {
                    client.unForward(status, repo)
                } else {
                    client.forward(
                        status = status,
                        repo = repo,
                        subject = subject,
                    )
                }
            }

            is StatusInteraction.Delete -> {
                return client.deleteRecord(
                    repo = repo,
                    collection = BskyCollections.feedPost,
                    rkey = status.rkey,
                ).map { null }
            }

            is StatusInteraction.Pin -> {
                val pinned = interaction.pinned
                return updateProfileRecord(
                    client = client,
                    updater = { profile ->
                        profile.copy(
                            pinnedPost = if (pinned) {
                                null
                            } else {
                                StrongRef(
                                    uri = AtUri(blog.url),
                                    cid = Cid(blog.id),
                                )
                            },
                        )
                    },
                ).map {
                    updateStatusInteraction(
                        status = status,
                        updateInteraction = { action ->
                            if (action is StatusInteraction.Pin) {
                                action.copy(pinned = !pinned)
                            } else {
                                action
                            }
                        },
                        updateBlog = { blog ->
                            blog.copy(pinned = !pinned)
                        },
                    )
                }
            }

            else -> return Result.failure(Exception("Bookmark not supported"))
        }
    }

    private suspend fun BlueskyClient.like(
        status: Status,
        repo: Did,
        subject: StrongRef,
    ): Result<Status> {
        val blog = status.intrinsicBlog
        return this.createRecord(
            collection = BskyCollections.feedLike,
            repo = repo,
            record = likeRecord(subject),
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
                updateBlog = { blog.copy(liked = true, likeCount = (blog.likeCount ?: 1L) + 1L) },
            )
        }
    }

    private suspend fun BlueskyClient.forward(
        status: Status,
        repo: Did,
        subject: StrongRef,
    ): Result<Status> {
        return this.createRecord(
            collection = BskyCollections.feedRepost,
            repo = repo,
            record = repostRecord(subject),
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
                    blog.copy(forward = true, forwardCount = (blog.forwardCount ?: 1L) + 1L)
                },
            )
        }
    }

    private suspend fun BlueskyClient.unlike(
        status: Status,
        repo: Did,
    ): Result<Status> {
        return this.deleteRecord(
            repo = repo,
            collection = BskyCollections.feedLike,
            rkey = status.rkey,
        ).map {
            updateStatusInteraction(
                status = status,
                updateInteraction = { action ->
                    if (action is StatusInteraction.Like) {
                        action.copy(liked = false, likeCount = action.likeCount - 1)
                    } else {
                        action
                    }
                },
                updateBlog = { blog ->
                    blog.copy(liked = false, likeCount = (blog.likeCount ?: 1L) - 1L)
                },
            )
        }
    }

    private suspend fun BlueskyClient.unForward(
        status: Status,
        repo: Did,
    ): Result<Status> {
        return this.deleteRecord(
            repo = repo,
            collection = BskyCollections.feedRepost,
            rkey = status.rkey,
        ).map {
            updateStatusInteraction(
                status = status,
                updateInteraction = { action ->
                    if (action is StatusInteraction.Forward) {
                        action.copy(forwarded = false, forwardCount = action.forwardCount - 1)
                    } else {
                        action
                    }
                },
                updateBlog = { blog ->
                    blog.copy(forward = false, likeCount = (blog.forwardCount ?: 1L) - 1L)
                },
            )
        }
    }

    private suspend fun BlueskyClient.createRecord(
        repo: Did,
        collection: Nsid,
        record: JsonContent,
    ): Result<CreateRecordResponse> {
        return this.createRecordCatching(
            CreateRecordRequest(
                repo = repo,
                collection = collection,
                record = record,
            ),
        )
    }

    private suspend fun BlueskyClient.deleteRecord(
        repo: Did,
        collection: Nsid,
        rkey: String,
    ): Result<DeleteRecordResponse> {
        return this.deleteRecordCatching(
            DeleteRecordRequest(
                repo = repo,
                collection = collection,
                rkey = rkey,
            ),
        )
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
