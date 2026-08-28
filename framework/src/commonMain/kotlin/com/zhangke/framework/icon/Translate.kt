package com.zhangke.framework.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.UnTranslate: ImageVector
    get() {
        if (_unTranslate != null) {
            return _unTranslate!!
        }
        _unTranslate = materialIcon(name = "Filled.UnTranslate") {
            materialPath {
                moveTo(12.87f, 15.07f)
                lineToRelative(-2.54f, -2.51f)
                lineToRelative(0.03f, -0.03f)
                curveToRelative(1.74f, -1.94f, 2.98f, -4.17f, 3.71f, -6.53f)
                lineTo(17.0f, 6.0f)
                lineTo(17.0f, 4.0f)
                horizontalLineToRelative(-7.0f)
                lineTo(10.0f, 2.0f)
                lineTo(8.0f, 2.0f)
                verticalLineToRelative(2.0f)
                lineTo(1.0f, 4.0f)
                verticalLineToRelative(1.99f)
                horizontalLineToRelative(11.17f)
                curveTo(11.5f, 7.92f, 10.44f, 9.75f, 9.0f, 11.35f)
                curveTo(8.07f, 10.32f, 7.3f, 9.19f, 6.69f, 8.0f)
                horizontalLineToRelative(-2.0f)
                curveToRelative(0.73f, 1.63f, 1.73f, 3.17f, 2.98f, 4.56f)
                lineToRelative(-5.09f, 5.02f)
                lineTo(4.0f, 19.0f)
                lineToRelative(5.0f, -5.0f)
                lineToRelative(3.11f, 3.11f)
                lineToRelative(0.76f, -2.04f)
                close()
                moveTo(18.5f, 10.0f)
                horizontalLineToRelative(-2.0f)
                lineTo(12.0f, 22.0f)
                horizontalLineToRelative(2.0f)
                lineToRelative(1.12f, -3.0f)
                horizontalLineToRelative(4.75f)
                lineTo(21.0f, 22.0f)
                horizontalLineToRelative(2.0f)
                lineToRelative(-4.5f, -12.0f)
                close()
                moveTo(15.88f, 17.0f)
                lineToRelative(1.62f, -4.33f)
                lineTo(19.12f, 17.0f)
                horizontalLineToRelative(-3.24f)
                close()
            }
            materialPath {
                moveTo(3.27f, 2.0f)
                lineTo(22.0f, 20.73f)
                lineTo(20.73f, 22.0f)
                lineTo(2.0f, 3.27f)
                close()
            }
        }
        return _unTranslate!!
    }

private var _unTranslate: ImageVector? = null
