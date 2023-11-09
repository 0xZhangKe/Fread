package com.zhangke.utopia.status.account

import com.zhangke.utopia.status.source.StatusSource


class SourcesAuthValidateResult(
    val validateList: List<StatusSource>,
    val invalidateList: List<StatusSource>,
)
