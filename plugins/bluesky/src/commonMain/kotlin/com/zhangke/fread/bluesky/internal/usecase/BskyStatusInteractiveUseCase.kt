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
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.Nsid
import sh.christian.ozone.api.RKey
import sh.christian.ozone.api.model.JsonContent

class BskyStatusInteractiveUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val updateProfileRecord: UpdateProfileRecordUseCase,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        status: Status,
        type: StatusActionType,
    ): Result<Status?> {
        val client = clientManager.getClient(locator)
        val loggedAccount =
            client.loggedAccountProvider() ?: return Result.failure(Exception("No logged account"))
        val repo = Did(loggedAccount.did)
        val blog = status.intrinsicBlog
        val subject = StrongRef(
            uri = AtUri(blog.url),
            cid = Cid(blog.id),
        )
        when (type) {
            StatusActionType.LIKE -> {
                return if (blog.like.liked == true) {
                    client.unlike(status, repo)
                } else {
                    client.like(
                        status = status,
                        repo = repo,
                        subject = subject,
                    )
                }
            }

            StatusActionType.FORWARD -> {
                return if (blog.forward.forward == true) {
                    client.unForward(status, repo)
                } else {
                    client.forward(
                        status = status,
                        repo = repo,
                        subject = subject,
                    )
                }
            }

            StatusActionType.DELETE -> {
                return client.deleteRecord(
                    repo = repo,
                    collection = BskyCollections.feedPost,
                    rkey = status.rkey,
                ).map { null }
            }

            StatusActionType.PIN -> {
                val pinned = blog.pinned
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
                    updateStatus(
                        status = status,
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
        return this.createRecord(
            collection = BskyCollections.feedLike,
            repo = repo,
            record = likeRecord(subject),
        ).map {
            updateStatus(
                status = status,
                updateBlog = { blog ->
                    blog.copy(
                        like = blog.like.copy(
                            liked = true,
                            likedCount = (blog.like.likedCount ?: 0L) + 1L,
                        ),
                    )
                },
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
            updateStatus(
                status = status,
                updateBlog = { blog ->
                    blog.copy(
                        forward = blog.forward.copy(
                            forward = true,
                            forwardCount = (blog.forward.forwardCount ?: 0L) + 1L,
                        ),
                    )
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
            updateStatus(
                status = status,
                updateBlog = { blog ->
                    blog.copy(
                        like = blog.like.copy(
                            liked = false,
                            likedCount = (blog.like.likedCount ?: 1L) - 1L,
                        ),
                    )
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
            updateStatus(
                status = status,
                updateBlog = { blog ->
                    blog.copy(
                        forward = blog.forward.copy(
                            forward = false,
                            forwardCount = (blog.forward.forwardCount ?: 1L) - 1L,
                        ),
                    )
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
        rkey: RKey,
    ): Result<DeleteRecordResponse> {
        return this.deleteRecordCatching(
            DeleteRecordRequest(
                repo = repo,
                collection = collection,
                rkey = rkey,
            ),
        )
    }

    private fun updateStatus(
        status: Status,
        updateBlog: (Blog) -> Blog,
    ): Status {
        return when (status) {
            is Status.NewBlog -> {
                status.copy(
                    blog = updateBlog(status.blog),
                )
            }

            is Status.Reblog -> {
                status.copy(
                    reblog = updateBlog(status.reblog),
                )
            }
        }
    }
}
