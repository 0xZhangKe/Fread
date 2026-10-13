package com.zhangke.fread.common.language

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LanguageDetectorTest {

    @Test
    fun detectsLanguagesAndReturnsLowercaseIsoTags() = runTest {
        LanguageDetector().use { detector ->
            assertEquals("ja", detector.detect("今日はいい天気ですね。友達と一緒に公園へ散歩に行きました。"))
            assertEquals("ko", detector.detect("오늘은 날씨가 좋아서 친구들과 함께 공원에 산책하러 갔습니다."))
        }
    }

    @Test
    fun detectsEnglishAndChinesePosts() = runTest {
        LanguageDetector().use { detector ->
            val english = "I have been reading a wonderful book about the history of science. " +
                "The author explains how researchers ask questions, design experiments, and share " +
                "their discoveries with people around the world. I would recommend this book " +
                "to anyone who enjoys learning new things."
            assertEquals("en", detector.detect(english))
            assertEquals("zh", detector.detect("今天天气晴朗，我和朋友一起去公园散步。我们看到了美丽的花朵，还在湖边拍了很多照片，度过了愉快的一天。"))
        }
    }

    @Test
    fun returnsNoSuggestionForBlankOrNonLinguisticText() = runTest {
        LanguageDetector().use { detector ->
            assertNull(detector.detect(""))
            assertNull(detector.detect("   \n\t"))
            assertNull(detector.detect("12345 🎉 !!!"))
        }
    }

    @Test
    fun invalidTextDoesNotInterruptTheComposer() = runTest {
        LanguageDetector().use { detector ->
            assertNull(detector.detect("\uD800"))
        }
    }

    @Test
    fun closedDetectorReturnsNoSuggestion() = runTest {
        val detector = LanguageDetector()
        detector.close()
        assertNull(detector.detect("今日はいい天気ですね。"))
    }
}
