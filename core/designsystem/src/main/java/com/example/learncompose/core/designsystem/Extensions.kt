package com.example.learncompose.core.designsystem

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toColorLong
import androidx.core.graphics.ColorUtils
import androidx.graphics.shapes.RoundedPolygon
import kotlin.Float
import kotlin.random.Random

val PresetColorList: List<Long> by lazy {
    generateDistinctColorLongs(
        count = 20,
        saturation = 0.45f,
        lightness = 0.45f
    )
}

val PresetFiveRandomColor: List<Long>
    get() = PresetColorList.shuffled().take(5)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val PresetShapeList: List<RoundedPolygon> by lazy {
    MaterialShapes.run {
        listOf(
            Circle,
            Square,
            Slanted,
            Arch,
            Fan,
            Arrow,
            SemiCircle,
            Oval,
            Pill,
            Triangle,
            Diamond,
            ClamShell,
            Pentagon,
            Gem,
            Sunny,
            VerySunny,
            Cookie4Sided,
            Cookie6Sided,
            Cookie7Sided,
            Cookie9Sided,
            Cookie12Sided,
            Ghostish,
            Clover4Leaf,
            Clover8Leaf,
            Burst,
            SoftBurst,
            Boom,
            SoftBoom,
            Flower,
            Puffy,
            PuffyDiamond,
            PixelCircle,
            PixelTriangle,
            Bun,
            Heart
        )
    }
}

val PresetFiveRandomShape: List<RoundedPolygon>
    get() = PresetShapeList.shuffled().take(5)



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
    saturation: Float = 0.7f,
    lightness: Float = 0.5f
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

fun generateDistinctColors(
    count: Int,
    saturation: Float = 0.75f,
    lightness: Float = 0.5f
): List<Color> {
    if (count <= 0) return listOf()

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
        composeColor
    }
}

/**
 * 将 Compose Color 转为标准 0xAARRGGBB 格式的 Long
 */
fun Color.toArgbLong(): Long {
    val a = (alpha * 255f + 0.5f).toInt() and 0xFF
    val r = (red * 255f + 0.5f).toInt() and 0xFF
    val g = (green * 255f + 0.5f).toInt() and 0xFF
    val b = (blue * 255f + 0.5f).toInt() and 0xFF
    return ((a.toLong() shl 24) or (r.toLong() shl 16) or (g.toLong() shl 8) or b.toLong()) and 0xFFFFFFFFL
}

fun Long.toComposeColor(): Color = Color(this)

fun Color.adjustSaturationAndLightness(
    deltaHue: Float? = null,
    saturationFactor: Float? = null,
    lightnessFactor: Float? = null
): Color {
    val hsl = FloatArray(3)
    // 1. 将 Compose Color 转为 ARGB 整数，再解析为 HSL
    ColorUtils.colorToHSL(this.toArgb(), hsl)

    // hsl[0] -> Hue (0 ~ 360)
    // hsl[1] -> Saturation (0.0 ~ 1.0)
    // hsl[2] -> Lightness (0.0 ~ 1.0)

    deltaHue?.let {
        var updatedHue = (hsl[0] + deltaHue) % 360f
        if (updatedHue < 0) {
            updatedHue += 360f // 处理负数偏移
        }
        hsl[0] = updatedHue
    }
    // 2. 调整饱和度与明度，并使用 coerceIn 限制在 [0.0, 1.0] 范围
    saturationFactor?.let {
        hsl[1] = (hsl[1] * saturationFactor).coerceIn(0f, 1f)
    }
    lightnessFactor?.let {
        hsl[2] = (hsl[2] * lightnessFactor).coerceIn(0f, 1f)
    }

    // 3. 将 HSL 转回 Compose Color，同时保留原有的 Alpha 通道
    return Color(ColorUtils.HSLToColor(hsl)).copy(alpha = this.alpha)
}