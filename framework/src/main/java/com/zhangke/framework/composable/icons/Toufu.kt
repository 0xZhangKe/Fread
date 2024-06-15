package com.zhangke.framework.composable.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.addPathNodes

private var _tofu: ImageVector? = null

val Icons.Filled.Tofu: ImageVector
    get() {
        if (_tofu != null) {
            return _tofu!!
        }
        _tofu = materialIcon(name = "Filled.Tofu") {
            addPath(
                buildPathData(
                    width = 24.0f,
                    height = 24.0f,
                    radius = 4.0f,
                ),
                fill = SolidColor(Color.Gray.copy(alpha = 0.6F))
            )
        }
        return _tofu!!
    }

private fun buildPathData(width: Float, height: Float, radius: Float): List<PathNode> {
    return addPathNodes(
        "M${radius},0 " +
                "H${width - radius} " +
                "A${radius},${radius} 0 0,1 $width,$radius " +
                "V${height - radius} " +
                "A${radius},${radius} 0 0,1 ${width - radius},$height " +
                "H$radius " +
                "A${radius},${radius} 0 0,1 0,${height - radius} " +
                "V$radius " +
                "A${radius},${radius} 0 0,1 $radius,0 " +
                "Z"
    )
}
