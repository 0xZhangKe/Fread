package com.zhangke.fread.common.ai.model

import ai.koog.prompt.llm.LLMCapability
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LLMProviderTest {

    @Test
    fun supportedProvidersHaveMatchingKoogModels() {
        val providers = LLMProvider.allSupportedProvider

        assertEquals(
            expected = setOf(
                "openai",
                "anthropic",
                "google",
                "meta",
                "alibaba",
                "openrouter",
                "ollama",
                "deepseek",
                "mistralai",
                "minimax",
                "zhipuai",
                "huggingface",
            ),
            actual = providers.mapTo(mutableSetOf(), LLMProvider::id),
        )
        providers.forEach { provider ->
            assertTrue(provider.koogModels.isNotEmpty(), "${provider.id} has no models")
            assertTrue(
                provider.koogModels.all { model -> model.provider.id == provider.id },
                "${provider.id} contains a model from another provider",
            )
        }
    }

    @Test
    fun customProviderCreatesOpenAICompatibleKoogModel() {
        val provider = LLMProvider(
            id = "custom",
            displayName = "Custom",
            baseUrl = "https://example.com/v1",
        )

        val model = provider.resolveKoogModel("custom-model")

        assertEquals("custom", model.provider.id)
        assertEquals("custom-model", model.id)
        assertTrue(model.supports(LLMCapability.Completion))
        assertTrue(model.supports(LLMCapability.OpenAIEndpoint.Completions))
    }
}
