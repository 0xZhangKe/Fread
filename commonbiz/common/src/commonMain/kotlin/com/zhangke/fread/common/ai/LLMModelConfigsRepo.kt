package com.zhangke.fread.common.ai

import com.zhangke.fread.common.ai.model.LLMModelConfig
import com.zhangke.fread.common.db.LLMModelConfigEntity
import com.zhangke.fread.common.db.LLMModelConfigsDatabase
import kotlinx.coroutines.flow.map

class LLMModelConfigsRepo(
    database: LLMModelConfigsDatabase,
) {

    private val dao = database.providerDao()

    fun getAllProviderFlow() = dao.queryAllFlow().map { list -> list.map { it.provider } }

    suspend fun getAllProvider(): List<LLMModelConfig> {
        return dao.queryAll().map { it.provider }
    }

    suspend fun getSelectedModelConfig(): LLMModelConfig? {
        return dao.queryAll().map { it.provider }.firstOrNull { it.selected }
    }

    suspend fun getProvider(providerId: String, versionName: String): LLMModelConfig? {
        return dao.query(providerId, versionName)?.provider
    }

    suspend fun selectProvider(providerId: String, versionName: String) {
        getSelectedModelConfig()
            ?.copy(selected = false)
            ?.let { insertProvider(it) }
        getProvider(providerId, versionName)
            ?.copy(selected = true)
            ?.let { insertProvider(it) }
    }

    suspend fun insertProvider(provider: LLMModelConfig) {
        dao.insert(provider.toEntity())
    }

    suspend fun insertAllProvider(list: List<LLMModelConfig>) {
        dao.insertAll(list.map { it.toEntity() })
    }

    suspend fun delete(providerId: String, versionName: String) {
        dao.delete(providerId, versionName)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    private fun LLMModelConfig.toEntity(): LLMModelConfigEntity {
        return LLMModelConfigEntity(
            providerId = provider.id,
            versionName = versionName,
            provider = this,
        )
    }
}
