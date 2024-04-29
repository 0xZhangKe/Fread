package com.zhangke.utopia.status.ui.richtext.android;


import com.zhangke.framework.utils.ContextUtils;

class UiUtils {

    public static int dp(float dp) {
        return Math.round(dp * ContextUtils.getAppContext().getResources().getDisplayMetrics().density);
    }
}
