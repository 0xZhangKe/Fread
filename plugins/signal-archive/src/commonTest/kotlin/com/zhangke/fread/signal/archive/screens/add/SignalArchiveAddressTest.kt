package com.zhangke.fread.signal.archive.screens.add

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SignalArchiveAddressTest {
    @Test
    fun convertsRepositoryAddressesToArchiveBranch() {
        for (input in listOf(
            "https://github.com/owner/repository",
            " https://github.com/owner/repository/ ",
            "https://github.com/owner/repository.git",
        )) {
            assertEquals("https://raw.githubusercontent.com/owner/repository/archive/", signalArchiveBaseUrl(input))
        }
    }

    @Test
    fun preservesStaticHostingDataDirectory() {
        for (input in listOf(
            "https://example.com/data",
            "https://example.com/data/",
            "https://example.com/data/rendered.json",
        )) {
            assertEquals("https://example.com/data/", signalArchiveBaseUrl(input))
        }
        assertEquals("https://example.com/", signalArchiveBaseUrl("https://example.com/rendered.json"))
        assertEquals("http://localhost:8080/data/", signalArchiveBaseUrl("http://localhost:8080/data"))
    }

    @Test
    fun rejectsUnsupportedOrAmbiguousAddresses() {
        for (input in listOf(
            "", "example.com/data", "file:///data", "https://github.com/owner",
            "https://github.com/owner/repository/tree/main",
            "https://example.com/data?token=value", "https://example.com/data#section",
        )) {
            assertNull(signalArchiveBaseUrl(input), input)
        }
    }
}
