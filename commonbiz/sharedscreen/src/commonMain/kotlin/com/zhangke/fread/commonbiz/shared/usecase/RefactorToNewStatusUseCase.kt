package com.zhangke.fread.commonbiz.shared.usecase

import com.zhangke.fread.status.model.StatusUiState

class RefactorToNewStatusUseCase (
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) {

    operator fun invoke(status: StatusUiState): StatusUiState {
        return status.copy(
            status = refactorToNewBlog(status.status),
        )
    }
}