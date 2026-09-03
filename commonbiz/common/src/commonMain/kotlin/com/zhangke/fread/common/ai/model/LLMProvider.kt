package com.zhangke.fread.common.ai.model

import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.llm.LLMProvider as KoogLLMProvider
import kotlinx.serialization.Serializable

@Serializable
data class LLMProvider(
    val id: String,
    val displayName: String,
    val baseUrl: String,
) {

    companion object {

        val allSupportedProvider = listOf(
            KoogLLMProvider.OpenAI.toFreadProvider("https://api.openai.com"),
            KoogLLMProvider.Anthropic.toFreadProvider("https://api.anthropic.com"),
            KoogLLMProvider.Google.toFreadProvider("https://generativelanguage.googleapis.com/v1beta/openai"),
            KoogLLMProvider.Meta.toFreadProvider("https://api.llama.com/compat"),
            KoogLLMProvider.Alibaba.toFreadProvider("https://dashscope.aliyuncs.com/compatible-mode"),
            KoogLLMProvider.OpenRouter.toFreadProvider("https://openrouter.ai/api"),
            KoogLLMProvider.Ollama.toFreadProvider("http://localhost:11434"),
            KoogLLMProvider.DeepSeek.toFreadProvider("https://api.deepseek.com"),
            KoogLLMProvider.MistralAI.toFreadProvider("https://api.mistral.ai"),
            KoogLLMProvider.MiniMax.toFreadProvider("https://api.minimax.chat"),
            KoogLLMProvider.ZhipuAI.toFreadProvider("https://open.bigmodel.cn/api/paas/v4"),
            KoogLLMProvider.HuggingFace.toFreadProvider("https://router.huggingface.co"),
        )
    }
}

private fun KoogLLMProvider.toFreadProvider(baseUrl: String): LLMProvider {
    return LLMProvider(
        id = id,
        displayName = display,
        baseUrl = baseUrl,
    )
}

val LLMProvider.versions: List<String>
    get() = koogModels.map(LLModel::id)

internal val LLMProvider.koogModels: List<LLModel>
    get() = when (id) {
        "openai" -> OpenAIModels.models.filter { model ->
            model.supports(LLMCapability.Completion) &&
                !model.supports(LLMCapability.Audio)
        }

        "anthropic" -> AnthropicModels.models.filter { model ->
            model.supports(LLMCapability.Completion) &&
                model.supports(LLMCapability.Tools)
        }

        "google" -> googleModels
        "meta" -> metaModels
        "alibaba" -> alibabaModels
        "openrouter" -> openRouterModels
        "ollama" -> OllamaModels.models.filterNot { model ->
            model.supports(LLMCapability.Embed) ||
                model.supports(LLMCapability.Moderation)
        }

        "deepseek" -> deepSeekModels
        "mistralai" -> mistralAIModels
        "minimax" -> miniMaxModels
        "zhipuai" -> zhipuAIModels
        "huggingface" -> huggingFaceModels

        else -> emptyList()
    }

internal fun LLMProvider.resolveKoogModel(modelId: String): LLModel {
    return koogModels.firstOrNull { model -> model.id == modelId }
        ?: LLModel(
            provider = koogModels.firstOrNull()?.provider
                ?: KoogLLMProvider(id = id, display = displayName),
            id = modelId,
            capabilities = listOf(
                LLMCapability.Completion,
                LLMCapability.Tools,
                LLMCapability.Vision.Image,
                LLMCapability.OpenAIEndpoint.Completions,
            ),
        )
}

private fun openAICompatibleModels(
    provider: KoogLLMProvider,
    modelIds: List<String>,
    visionModelIds: Set<String> = emptySet(),
): List<LLModel> = modelIds.map { modelId ->
    LLModel(
        provider = provider,
        id = modelId,
        capabilities = buildList {
            add(LLMCapability.Completion)
            add(LLMCapability.OpenAIEndpoint.Completions)
            if (modelId in visionModelIds) {
                add(LLMCapability.Vision.Image)
            }
        },
    )
}

private val googleModels = openAICompatibleModels(
    provider = KoogLLMProvider.Google,
    modelIds = listOf(
        "gemini-2.5-pro",
        "gemini-2.5-flash",
        "gemini-2.5-flash-lite",
        "gemini-2.0-flash",
        "gemini-2.0-flash-lite",
    ),
    visionModelIds = setOf(
        "gemini-2.5-pro",
        "gemini-2.5-flash",
        "gemini-2.5-flash-lite",
        "gemini-2.0-flash",
        "gemini-2.0-flash-lite",
    ),
)

private val metaModels = openAICompatibleModels(
    provider = KoogLLMProvider.Meta,
    modelIds = listOf(
        "llama-4-maverick",
        "llama-4-scout",
        "llama-3.3-70b-instruct",
        "llama-3.2-90b-vision-instruct",
        "llama-3.2-11b-vision-instruct",
        "llama-3.1-405b-instruct",
        "llama-3.1-70b-instruct",
        "llama-3.1-8b-instruct",
    ),
    visionModelIds = setOf(
        "llama-4-maverick",
        "llama-4-scout",
        "llama-3.2-90b-vision-instruct",
        "llama-3.2-11b-vision-instruct",
    ),
)

private val alibabaModels = openAICompatibleModels(
    provider = KoogLLMProvider.Alibaba,
    modelIds = listOf(
        "qwen3.7-plus",
        "qwen3.6-plus",
        "qwen3.6-flash",
        "qwen3.6-35b-a3b",
        "qwen3.6-27b",
        "qwen3-max",
        "qwen3-plus",
        "qwen3-turbo",
        "qwen3-vl-plus",
        "qwen3-vl-max",
        "qwen2.5-vl-72b-instruct",
        "qwen2.5-max",
    ),
    visionModelIds = setOf(
        "qwen3-vl-plus",
        "qwen3-vl-max",
        "qwen2.5-vl-72b-instruct",
    ),
)

private val openRouterVisionModelIds = setOf(
    "qwen/qwen3-vl-235b-a22b-instruct",
    "qwen/qwen3-vl-32b-instruct",
    "qwen/qwen3-vl-30b-a3b-thinking",
    "qwen/qwen3-vl-30b-a3b-instruct",
    "qwen/qwen3-vl-8b-thinking",
    "qwen/qwen3-vl-8b-instruct",
    "z-ai/glm-5v-turbo",
    "z-ai/glm-4.6v",
    "z-ai/glm-4.5v",
    "minimax/minimax-01",
    "openai/gpt-5.1",
    "openai/gpt-5-mini",
    "openai/gpt-4.1",
    "openai/gpt-4o-mini",
    "anthropic/claude-sonnet-4.5",
    "google/gemini-2.5-pro",
    "google/gemini-2.5-flash",
    "moonshotai/kimi-k2.5",
    "qwen/qwen3-vl-235b-a22b-thinking",
    "meta-llama/llama-4-maverick",
    "meta-llama/llama-4-scout",
)

private val openRouterModels = openAICompatibleModels(
    provider = KoogLLMProvider.OpenRouter,
    modelIds = listOf(
        "qwen/qwen3.8-max",
        "qwen/qwen3.7-plus",
        "qwen/qwen3.7-flash",
        "qwen/qwen3.6-plus",
        "qwen/qwen3.6-flash",
        "qwen/qwen3.6-35b-a3b",
        "qwen/qwen3.6-27b",
        "qwen/qwen3-vl-235b-a22b-instruct",
        "qwen/qwen3-vl-32b-instruct",
        "qwen/qwen3-vl-30b-a3b-thinking",
        "qwen/qwen3-vl-30b-a3b-instruct",
        "qwen/qwen3-vl-8b-thinking",
        "qwen/qwen3-vl-8b-instruct",
        "moonshotai/kimi-k3",
        "moonshotai/kimi-k2.7-code",
        "moonshotai/kimi-k2.6",
        "z-ai/glm-5v-turbo",
        "z-ai/glm-4.6v",
        "z-ai/glm-4.5v",
        "minimax/minimax-m3",
        "minimax/minimax-01",
        "openai/gpt-5.1",
        "openai/gpt-5-mini",
        "openai/gpt-4.1",
        "openai/gpt-4o-mini",
        "anthropic/claude-sonnet-4.5",
        "google/gemini-2.5-pro",
        "google/gemini-2.5-flash",
        "moonshotai/kimi-k2.5",
        "qwen/qwen3-vl-235b-a22b-thinking",
        "qwen/qwen3-max",
        "deepseek/deepseek-chat-v3.1",
        "deepseek/deepseek-r1",
        "meta-llama/llama-4-maverick",
        "meta-llama/llama-4-scout",
        "mistralai/mistral-large",
    ),
    visionModelIds = openRouterVisionModelIds,
)

private val deepSeekModels = openAICompatibleModels(
    provider = KoogLLMProvider.DeepSeek,
    modelIds = listOf(
        "deepseek-chat",
        "deepseek-reasoner",
    ),
)

private val mistralAIModels = openAICompatibleModels(
    provider = KoogLLMProvider.MistralAI,
    modelIds = listOf(
        "mistral-large-latest",
        "mistral-small-latest",
        "codestral-latest",
        "pixtral-large-latest",
    ),
    visionModelIds = setOf("pixtral-large-latest"),
)

private val miniMaxModels = openAICompatibleModels(
    provider = KoogLLMProvider.MiniMax,
    modelIds = listOf(
        "MiniMax-M1",
        "MiniMax-Text-01",
        "MiniMax-VL-01",
    ),
    visionModelIds = setOf("MiniMax-VL-01"),
)

private val zhipuAIModels = openAICompatibleModels(
    provider = KoogLLMProvider.ZhipuAI,
    modelIds = listOf(
        "glm-4.6",
        "glm-4.5",
        "glm-4.5-air",
        "glm-4.1v-thinking-flash",
        "glm-4v-plus-0111",
    ),
    visionModelIds = setOf(
        "glm-4.1v-thinking-flash",
        "glm-4v-plus-0111",
    ),
)

private val huggingFaceModels = openAICompatibleModels(
    provider = KoogLLMProvider.HuggingFace,
    modelIds = listOf(
        "meta-llama/Llama-3.3-70B-Instruct",
        "meta-llama/Llama-3.1-8B-Instruct",
        "Qwen/Qwen3-235B-A22B-Instruct-2507",
        "Qwen/Qwen2.5-VL-72B-Instruct",
        "mistralai/Mistral-Large-Instruct-2411",
        "deepseek-ai/DeepSeek-R1",
    ),
    visionModelIds = setOf("Qwen/Qwen2.5-VL-72B-Instruct"),
)
