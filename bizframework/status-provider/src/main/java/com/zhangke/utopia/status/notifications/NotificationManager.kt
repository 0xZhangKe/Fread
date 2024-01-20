package com.zhangke.utopia.status.notifications

import com.zhangke.utopia.status.model.StatusNotification

class NotificationManager(
    private val managerList: List<INotificationManager>,
) {

}

interface INotificationManager {

    suspend fun getNotificationList(): List<StatusNotification>
}
