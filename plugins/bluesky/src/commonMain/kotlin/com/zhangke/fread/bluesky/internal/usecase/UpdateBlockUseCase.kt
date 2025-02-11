package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.blockRecord
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class UpdateBlockUseCase @Inject constructor(
    private val createRecord: CreateRecordUseCase,
    private val deleteRecord: DeleteRecordUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        did: String,
        block: Boolean,
        rkey: String?,
    ): Result<Unit> {
        val atDid = Did(did)
        return if (block) {
            createRecord(
                role = role,
                repo = atDid,
                collection = BskyCollections.block,
                record = blockRecord(did),
            )
        } else {
            if (rkey.isNullOrEmpty()) {
                Result.success(Unit)
            } else {
                deleteRecord(
                    role = role,
                    repo = atDid,
                    collection = BskyCollections.block,
                    rkey = rkey,
                )
            }
        }
    }
}
