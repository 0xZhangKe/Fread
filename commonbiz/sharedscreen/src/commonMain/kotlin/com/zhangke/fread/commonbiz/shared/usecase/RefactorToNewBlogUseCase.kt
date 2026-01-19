package com.zhangke.fread.commonbiz.shared.usecase

import com.zhangke.fread.status.status.model.Status

class RefactorToNewBlogUseCase () {

    operator fun invoke(status: Status): Status.NewBlog {
        return when (status) {
            is Status.NewBlog -> status
            is Status.Reblog -> Status.NewBlog(
                blog = status.reblog,
            )
        }
    }
}