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
            LLMProvider("openai", "OpenAI", "https://api.openai.com"),
            LLMProvider("anthropic", "Anthropic", "https://api.anthropic.com"),
            LLMProvider("openrouter", "OpenRouter", "https://openrouter.ai/api"),
            LLMProvider("ollama", "Ollama", "http://localhost:11434"),
        )
    }
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
        "openrouter" -> openRouterModels
        "ollama" -> OllamaModels.models.filterNot { model ->
            model.supports(LLMCapability.Embed) ||
                model.supports(LLMCapability.Moderation)
        }

        else -> emptyList()
    }

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

private val openRouterModels = listOf(
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
).map { modelId ->
    LLModel(
        provider = KoogLLMProvider.OpenRouter,
        id = modelId,
        capabilities = buildList {
            add(LLMCapability.Completion)
            add(LLMCapability.OpenAIEndpoint.Completions)
            if (modelId in openRouterVisionModelIds) {
                add(LLMCapability.Vision.Image)
            }
        },
    )
}
