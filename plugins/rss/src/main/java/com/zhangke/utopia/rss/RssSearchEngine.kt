package com.zhangke.utopia.rss

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.search.ISearchEngine
import com.zhangke.utopia.status.search.SearchResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class RssSearchEngine @Inject constructor(): ISearchEngine {

    override suspend fun search(query: String): Result<List<SearchResult>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchStatus(query: String, maxId: String?): Result<List<Status>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchHashtag(query: String, offset: Int?): Result<List<Hashtag>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchAuthor(query: String, offset: Int?): Result<List<BlogAuthor>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchPlatform(query: String, offset: Int?): Result<List<BlogPlatform>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchSource(query: String): Result<List<StatusSource>> {
        TODO("Not yet implemented")
    }
}
