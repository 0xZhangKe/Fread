package com.zhangke.fread.status.ui.like

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.ui.action.likeIcon
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// 粒子数据类
data class Particle(
    val id: Int,
    val startX: Float,
    val startY: Float,
    val initialVelocityX: Float,
    val initialVelocityY: Float,
    val color: Color,
    val radius: Float,
    val gravity: Float = 980f, // 重力加速度 (像素/秒²)
    val dampening: Float = 0.9f // 阻尼系数
)

@Composable
fun LikeIcon(
    modifier: Modifier,
    liked: Boolean,
    contentDescription: String,
) {
    var particles by remember { mutableStateOf(emptyList<Particle>()) }
    val density = LocalDensity.current
    val animationDuration = 10000
    val animatable = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val contentColor = if (liked) {
        MaterialTheme.colorScheme.tertiary
    } else {
        LocalContentColor.current
    }
    Box(modifier = modifier.size(18.dp)) {
        LaunchedEffect(liked) {
            if (liked) {
                particles = generateParticles(
                    centerX = 0F,
                    centerY = 0F,
                    viewSize = with(density) { 18.dp.toPx() },
                )
            }
            coroutineScope.launch {
                animatable.snapTo(0f)
                animatable.animateTo(
                    targetValue = animationDuration.toFloat(),
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = LinearEasing,
                    )
                )
                particles = emptyList()
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    clip = false
                }
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val currentTime = animatable.value

            particles.forEach { particle ->
                drawParticle(particle, currentTime, centerX, centerY)
            }
        }
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = likeIcon(liked),
            contentDescription = contentDescription,
            tint = contentColor,
        )
    }
}

// 生成粒子
private fun generateParticles(
    centerX: Float,
    centerY: Float,
    viewSize: Float
): List<Particle> {
    val particleCount = 12 // 粒子数量
    val particles = mutableListOf<Particle>()

    // 颜色数组
    val colors = listOf(
        Color(0xFFFF6B6B),
        Color(0xFFFF8787),
        Color(0xFFFFA5A5),
        Color(0xFFFFB3B3),
        Color(0xFFFF5252),
        Color(0xFFFF1744)
    )

    for (i in 0 until particleCount) {
        val angle = (360f / particleCount * i) + Random.nextFloat() * 30 - 15 // 添加随机偏移
        val angleRad = toRadians(angle.toDouble())

        // 初始速度 (像素/秒)
        val speed = viewSize * (2.5f + Random.nextFloat() * 1.5f) // 速度范围
        val velocityX = (cos(angleRad) * speed).toFloat()
        val velocityY = (sin(angleRad) * speed).toFloat() - viewSize * 2 // 向上的初始速度

        particles.add(
            Particle(
                id = i,
                startX = 0f,
                startY = 0f,
                initialVelocityX = velocityX,
                initialVelocityY = velocityY,
                color = colors.random(),
                radius = 3f + Random.nextFloat() * 3f, // 3-6像素半径
                gravity = viewSize * 3f // 重力与视图大小成比例
            )
        )
    }

    return particles
}

// 绘制粒子
private fun DrawScope.drawParticle(
    particle: Particle,
    currentTimeMs: Float,
    centerX: Float,
    centerY: Float
) {
    val t = currentTimeMs / 1000f // 转换为秒

    // 物理运动公式
    // x = x0 + v0x * t
    // y = y0 + v0y * t + 0.5 * g * t²
    val x = centerX + particle.startX + particle.initialVelocityX * t
    val y =
        centerY + particle.startY + particle.initialVelocityY * t + 0.5f * particle.gravity * t * t

    // 计算透明度（随时间递减）
    val alpha = (1f - t / 2f).coerceIn(0f, 1f)

    // 只在视图范围内绘制
    if (x >= -particle.radius && x <= size.width + particle.radius &&
        y >= -particle.radius && y <= size.height + particle.radius
    ) {
        drawCircle(
            color = particle.color.copy(alpha = alpha),
            radius = particle.radius,
            center = Offset(x, y)
        )
    }
}

private const val DEGREES_TO_RADIANS = 0.017453292519943295

fun toRadians(angdeg: Double): Double {
    return angdeg * DEGREES_TO_RADIANS
}
