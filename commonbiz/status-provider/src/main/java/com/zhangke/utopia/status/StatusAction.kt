package com.zhangke.utopia.status

/**
 * Created by ZhangKe on 2022/12/9.
 */
sealed class StatusAction {

    class Forawrd : StatusAction()

    class Like : StatusAction()

    class Comment : StatusAction()
}