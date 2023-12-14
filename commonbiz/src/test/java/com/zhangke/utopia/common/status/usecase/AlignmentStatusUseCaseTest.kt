package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.StatusIdGenerator
import com.zhangke.utopia.common.status.adapter.StatusContentEntityAdapter
import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.createStatusContentEntity
import com.zhangke.utopia.common.status.utils.createStatus
import com.zhangke.utopia.common.utils.createActivityPubUserUri
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.StatusResolver
import com.zhangke.utopia.status.status.model.Status
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.util.Date

class AlignmentStatusUseCaseTest {

    private val statusResolver: StatusResolver = mockk()
    private val statusProvider: StatusProvider = mockk {
        every { statusResolver } returns this@AlignmentStatusUseCaseTest.statusResolver
    }
    private val statusContentRepo = mockk<StatusContentRepo>()
    private val saveStatusListToLocal = mockk<SaveStatusListToLocalUseCase>()
    private val entityAdapter = StatusContentEntityAdapter(StatusIdGenerator())
    private val alignmentStatusUseCase = AlignmentStatusUseCase(
        statusProvider = statusProvider,
        statusContentRepo = statusContentRepo,
        statusContentEntityAdapter = entityAdapter,
        saveStatusListToLocal = saveStatusListToLocal,
    )

    private val baselineEntity = createStatusContentEntity(
        id = "100",
        sourceUri = createActivityPubUserUri(userId = "1"),
        createTimestamp = 1000,
    )

    private val sourceUriList = listOf(
        createActivityPubUserUri(userId = "1"),
        createActivityPubUserUri(userId = "2"),
        createActivityPubUserUri(userId = "3"),
    )

    @Test
    fun `should not request status when status is all alignment`() = runTest {
        coEvery { statusContentRepo.queryFirst(any()) } returns createStatusContentEntity(
            id = "101",
            sourceUri = createActivityPubUserUri(userId = "1"),
            createTimestamp = 999,
        )
        coEvery { statusContentRepo.queryLatest(any()) } returns createStatusContentEntity(
            id = "101",
            sourceUri = createActivityPubUserUri(userId = "1"),
            createTimestamp = 1001,
        )
        coVerify(exactly = 0) {
            statusResolver.getStatusList(
                uri = any(),
                limit = any(),
                sinceId = any(),
                minId = any()
            )
        }
        val result = alignmentStatusUseCase(sourceUriList, baselineEntity)
        Assert.assertTrue(result.isSuccess)
    }

    @Test
    fun `should request past status when has source which first is after than baseline`() =
        runTest {
            coEvery { statusContentRepo.queryFirst(any()) } returns createStatusContentEntity(
                id = "101",
                sourceUri = createActivityPubUserUri(userId = "6001"),
                createTimestamp = 1001,
            )
            coEvery { statusContentRepo.queryLatest(any()) } returns createStatusContentEntity(
                id = "102",
                sourceUri = createActivityPubUserUri(userId = "6002"),
                createTimestamp = 1001,
            )
            coEvery {
                statusResolver.getStatusList(
                    uri = any(),
                    limit = any(),
                    sinceId = any(),
                    minId = any()
                )
            } answers {
                val list = mutableListOf<Status>()
                repeat(StatusConfigurationDefault.config.loadFromServerLimit) {
                    list += createStatus(date = Date(2000 - it.toLong()))
                }
                Result.success(list)
            }
            coEvery { saveStatusListToLocal(any(), any(), any()) } returns emptyList()
            val result = alignmentStatusUseCase(sourceUriList, baselineEntity)
            Assert.assertTrue(result.isSuccess)
        }
}
