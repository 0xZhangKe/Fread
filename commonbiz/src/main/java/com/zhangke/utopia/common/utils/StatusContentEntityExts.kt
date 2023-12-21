package com.zhangke.utopia.common.utils

import com.zhangke.utopia.common.status.repo.StatusContentRepo
import com.zhangke.utopia.common.status.repo.db.StatusContentEntity

fun StatusContentEntity.isFirstStatus(): Boolean {
    return nextStatusId == StatusContentRepo.STATUS_END_MAGIC_NUMBER
}

fun StatusContentEntity.markToFirstStatus(): StatusContentEntity {
    return copy(nextStatusId = StatusContentRepo.STATUS_END_MAGIC_NUMBER)
}
