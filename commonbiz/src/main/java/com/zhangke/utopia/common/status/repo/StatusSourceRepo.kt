package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import javax.inject.Inject

class StatusSourceRepo @Inject constructor(
    private val statusDatabase: StatusDatabase,
){

}
