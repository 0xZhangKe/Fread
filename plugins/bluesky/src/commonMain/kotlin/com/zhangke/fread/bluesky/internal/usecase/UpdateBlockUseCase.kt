package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.adjustToRkey
import com.zhangke.fread.bluesky.internal.client.blockRecord
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri

class UpdateBlockUseCase @Inject constructor(
    private val createRecord: CreateRecordUseCase,
    private val deleteRecord: DeleteRecordUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        did: String,
        block: Boolean,
        blockUri: String?,
    ): Result<AtUri?> {
        return if (block) {
            createRecord(
                role = role,
                collection = BskyCollections.block,
                record = blockRecord(did),
            ).map { it.uri }
        } else {
            if (blockUri.isNullOrEmpty()) {
                Result.success(null)
            } else {
                deleteRecord(
                    role = role,
                    collection = BskyCollections.block,
                    rkey = blockUri.adjustToRkey(),
                ).map { null }
            }
        }
    }
}
