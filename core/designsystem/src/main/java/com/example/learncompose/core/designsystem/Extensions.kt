package com.example.learncompose.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * 根据指定数量生成区分度最高的颜色 Long 值列表
 *
 * @param count 需要生成的颜色数量
 * @param saturation 饱和度 (0.0f - 1.0f)，默认 0.75f 确保色彩鲜艳
 * @param lightness 亮度 (0.0f - 1.0f)，默认 0.55f 避免过亮或过暗
 * @return 颜色 ARGB 值的 List<Long>
 */
fun generateDistinctColorLongs(
    count: Int,
    saturation: Float = 0.75f,
    lightness: Float = 0.55f
): List<Long> {
    if (count <= 0) return emptyList()

    // 计算色相步进（360度均分）
    val step = 360f / count

    return List(count) { index ->
        val hue = index * step
        // 使用 Compose 的 HSL 构造颜色
        val composeColor = Color.hsl(
            hue = hue,
            saturation = saturation,
            lightness = lightness
        )
        // 转为标准的 32位 ARGB Long 值 (0xAARRGGBB)
        // 注：composeColor.value 内部是 ULong（编码了颜色空间），
        // 如果要用于 Compose Color(value = ...) 或标准 Hex，转换为 ARGB Long 最为通用安全
        composeColor.toArgbLong()
    }
}

/**
 * 将 Compose Color 转为标准 0xAARRGGBB 格式的 Long
 */
private fun Color.toArgbLong(): Long {
    val a = (alpha * 255f + 0.5f).toInt() and 0xFF
    val r = (red * 255f + 0.5f).toInt() and 0xFF
    val g = (green * 255f + 0.5f).toInt() and 0xFF
    val b = (blue * 255f + 0.5f).toInt() and 0xFF
    return ((a.toLong() shl 24) or (r.toLong() shl 16) or (g.toLong() shl 8) or b.toLong()) and 0xFFFFFFFFL
}