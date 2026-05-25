package com.zhangke.fread.common.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class LLMProvider(
    val id: String,
    val displayName: String,
    val baseUrl: String,
) {

    companion object {

        val allSupportedProvider = listOf(
            LLMProvider("openai", "OpenAI", "https://api.openai.com/v1"),
            LLMProvider("anthropic", "Anthropic", "https://api.anthropic.com"),
            LLMProvider("google", "Google", "https://generativelanguage.googleapis.com/v1beta"),
            LLMProvider("meta", "Meta", "https://api.llama.com/compat/v1"),
            LLMProvider("alibaba", "Alibaba", "https://dashscope.aliyuncs.com/compatible-mode/v1"),
            LLMProvider("openrouter", "OpenRouter", "https://openrouter.ai/api/v1"),
            LLMProvider("ollama", "Ollama", "http://localhost:11434/v1"),
            LLMProvider("bedrock", "Bedrock", "https://bedrock-runtime.us-east-1.amazonaws.com"),
            LLMProvider("deepseek", "DeepSeek", "https://api.deepseek.com/v1"),
            LLMProvider("mistralai", "Mistral AI", "https://api.mistral.ai/v1"),
            LLMProvider("oci", "OCI", "https://inference.generativeai.us-chicago-1.oci.oraclecloud.com"),
            LLMProvider("minimax", "MiniMax", "https://api.minimax.chat/v1"),
            LLMProvider("zhipuai", "Zhipu AI", "https://open.bigmodel.cn/api/paas/v4"),
            LLMProvider("huggingface", "Hugging Face", "https://router.huggingface.co/v1"),
            LLMProvider("azure", "Azure", "https://{resource-name}.openai.azure.com/openai/deployments/{deployment-name}"),
            LLMProvider("vertex", "Vertex", "https://{region}-aiplatform.googleapis.com/v1"),
        )
    }
}

val LLMProvider.versions: List<String>
    get() = when (id) {
        "openai" -> openAIVersions
        "anthropic" -> anthropicVersions
        "google" -> googleVersions
        "meta" -> metaVersions
        "alibaba" -> alibabaVersions
        "openrouter" -> openRouterVersions
        "ollama" -> ollamaVersions
        "bedrock" -> bedrockVersions
        "deepseek" -> deepSeekVersions
        "mistralai" -> mistralAIVersions
        "oci" -> ociVersions
        "minimax" -> miniMaxVersions
        "zhipuai" -> zhipuAIVersions
        "huggingface" -> huggingFaceVersions
        "azure" -> azureVersions
        "vertex" -> vertexVersions
        else -> emptyList()
    }

private val openAIVersions = listOf(
    "gpt-5.5",
    "gpt-5.5-pro",
    "gpt-5.4",
    "gpt-5.4-mini",
    "gpt-5.4-nano",
    "gpt-5.4-pro",
    "gpt-5.3-codex",
    "gpt-5.2",
    "gpt-5.2-pro",
    "gpt-5.2-codex",
    "gpt-5.1",
    "gpt-5.1-codex",
    "gpt-5.1-codex-max",
    "gpt-5",
    "gpt-5-mini",
    "gpt-5-nano",
    "gpt-5-codex",
    "gpt-5-pro",
    "gpt-4.1",
    "gpt-4.1-mini",
    "gpt-4.1-nano",
    "gpt-4o",
    "gpt-4o-mini",
    "o4-mini",
    "o3",
    "o3-mini",
    "o1",
)

private val anthropicVersions = listOf(
    "claude-opus-4-7",
    "claude-opus-4-6",
    "claude-opus-4-5",
    "claude-opus-4-1",
    "claude-opus-4-0",
    "claude-sonnet-4-6",
    "claude-sonnet-4-5",
    "claude-sonnet-4-0",
    "claude-haiku-4-5",
)

private val googleVersions = listOf(
    "gemini-2.5-pro",
    "gemini-2.5-flash",
    "gemini-2.5-flash-lite",
    "gemini-2.0-flash",
    "gemini-2.0-flash-lite",
)

private val metaVersions = listOf(
    "llama-4-maverick",
    "llama-4-scout",
    "llama-3.3-70b-instruct",
    "llama-3.2-90b-vision-instruct",
    "llama-3.2-11b-vision-instruct",
    "llama-3.1-405b-instruct",
    "llama-3.1-70b-instruct",
    "llama-3.1-8b-instruct",
)

private val alibabaVersions = listOf(
    "qwen3-max",
    "qwen3-plus",
    "qwen3-turbo",
    "qwen3-vl-plus",
    "qwen3-vl-max",
    "qwen2.5-vl-72b-instruct",
    "qwen2.5-max",
)

private val openRouterVersions = listOf(
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
)

private val ollamaVersions = listOf(
    "gpt-oss:20b",
    "llama4:latest",
    "llama4:scout",
    "llama3.2:latest",
    "llama3.2:3b",
    "qwen3.5:9b",
    "qwen3:0.6b",
    "qwen2.5-coder:32b",
    "qwen2.5:0.5b",
    "qwq:32b",
    "deepseek-r1:1.5b",
    "granite3.2-vision",
    "llama3-groq-tool-use:70b",
    "llama3-groq-tool-use:8b",
)

private val bedrockVersions = listOf(
    "anthropic.claude-3-5-sonnet-20241022-v2:0",
    "anthropic.claude-3-5-haiku-20241022-v1:0",
    "anthropic.claude-3-opus-20240229-v1:0",
    "amazon.nova-pro-v1:0",
    "amazon.nova-lite-v1:0",
    "amazon.nova-micro-v1:0",
    "meta.llama3-3-70b-instruct-v1:0",
    "mistral.mistral-large-2407-v1:0",
)

private val deepSeekVersions = listOf(
    "deepseek-chat",
    "deepseek-reasoner",
)

private val mistralAIVersions = listOf(
    "mistral-large-latest",
    "mistral-small-latest",
    "codestral-latest",
    "pixtral-large-latest",
)

private val ociVersions = listOf(
    "cohere.command-r-plus",
    "cohere.command-r",
    "meta.llama-3.3-70b-instruct",
    "meta.llama-3.1-405b-instruct",
)

private val miniMaxVersions = listOf(
    "MiniMax-M1",
    "MiniMax-Text-01",
    "MiniMax-VL-01",
)

private val zhipuAIVersions = listOf(
    "glm-4.6",
    "glm-4.5",
    "glm-4.5-air",
    "glm-4.1v-thinking-flash",
    "glm-4v-plus-0111",
)

private val huggingFaceVersions = listOf(
    "meta-llama/Llama-3.3-70B-Instruct",
    "meta-llama/Llama-3.1-8B-Instruct",
    "Qwen/Qwen3-235B-A22B-Instruct-2507",
    "Qwen/Qwen2.5-VL-72B-Instruct",
    "mistralai/Mistral-Large-Instruct-2411",
    "deepseek-ai/DeepSeek-R1",
)

private val azureVersions = listOf(
    "gpt-5.1",
    "gpt-5-mini",
    "gpt-4.1",
    "gpt-4.1-mini",
    "gpt-4o",
    "gpt-4o-mini",
    "o4-mini",
)

private val vertexVersions = listOf(
    "gemini-2.5-pro",
    "gemini-2.5-flash",
    "gemini-2.5-flash-lite",
    "gemini-2.0-flash",
    "gemini-2.0-flash-lite",
    "claude-sonnet-4-5",
    "claude-opus-4-1",
    "llama-3.1-405b-instruct-maas",
)
