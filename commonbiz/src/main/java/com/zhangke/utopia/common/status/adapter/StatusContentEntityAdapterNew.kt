package com.zhangke.utopia.common.status.adapter

import com.google.gson.JsonObject
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.common.status.repo.db.StatusContentTableNewNewNew
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusType

class StatusContentEntityAdapterNew {

    fun toStatus(entity: StatusContentTableNewNewNew): Status {
        return when (entity.type) {
            StatusType.BLOG -> convertToNewBlog(entity.payload)
            StatusType.REBLOG -> convertToReblog(entity.payload)
        }
    }

    private fun convertToNewBlog(payload: String): Status {

    }

    private fun convertToReblog(payload: String): Status {

    }

    private fun convertToBlog(blogObject: JsonObject): Blog {
        return globalGson.fromJson(blogObject, Blog::class.java)
    }
}
