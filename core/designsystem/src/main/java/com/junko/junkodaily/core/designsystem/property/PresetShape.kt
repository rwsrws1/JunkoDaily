package com.junko.junkodaily.core.designsystem.property

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.graphics.shapes.RoundedPolygon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
enum class PresetShape(
    val shapeName: String,
    val polygon: RoundedPolygon
) {
    CIRCLE("Circle", MaterialShapes.Circle),
    SQUARE("Square", MaterialShapes.Square),
    SLANTED("Slanted", MaterialShapes.Slanted),
    ARCH("Arch", MaterialShapes.Arch),
    FAN("Fan", MaterialShapes.Fan),
    ARROW("Arrow", MaterialShapes.Arrow),
    SEMI_CIRCLE("SemiCircle", MaterialShapes.SemiCircle),
    OVAL("Oval", MaterialShapes.Oval),
    PILL("Pill", MaterialShapes.Pill),
    TRIANGLE("Triangle", MaterialShapes.Triangle),
    DIAMOND("Diamond", MaterialShapes.Diamond),
    CLAM_SHELL("ClamShell", MaterialShapes.ClamShell),
    PENTAGON("Pentagon", MaterialShapes.Pentagon),
    GEM("Gem", MaterialShapes.Gem),
    SUNNY("Sunny", MaterialShapes.Sunny),
    VERY_SUNNY("VerySunny", MaterialShapes.VerySunny),
    COOKIE_4_SIDED("Cookie4Sided", MaterialShapes.Cookie4Sided),
    COOKIE_6_SIDED("Cookie6Sided", MaterialShapes.Cookie6Sided),
    COOKIE_7_SIDED("Cookie7Sided", MaterialShapes.Cookie7Sided),
    COOKIE_9_SIDED("Cookie9Sided", MaterialShapes.Cookie9Sided),
    COOKIE_12_SIDED("Cookie12Sided", MaterialShapes.Cookie12Sided),
    GHOSTISH("Ghostish", MaterialShapes.Ghostish),
    CLOVER_4_LEAF("Clover4Leaf", MaterialShapes.Clover4Leaf),
    CLOVER_8_LEAF("Clover8Leaf", MaterialShapes.Clover8Leaf),
    BURST("Burst", MaterialShapes.Burst),
    SOFT_BURST("SoftBurst", MaterialShapes.SoftBurst),
    BOOM("Boom", MaterialShapes.Boom),
    SOFT_BOOM("SoftBoom", MaterialShapes.SoftBoom),
    FLOWER("Flower", MaterialShapes.Flower),
    PUFFY("Puffy", MaterialShapes.Puffy),
    PUFFY_DIAMOND("PuffyDiamond", MaterialShapes.PuffyDiamond),
    //  PIXEL_CIRCLE("PixelCircle", MaterialShapes.PixelCircle),
//  PIXEL_TRIANGLE("PixelTriangle", MaterialShapes.PixelTriangle),
    BUN("Bun", MaterialShapes.Bun),
    HEART("Heart", MaterialShapes.Heart);

    companion object {
        // 构建静态 Map 索引，通过字符串名称快速匹配，$O(1) 性能
        private val nameMap: Map<String, PresetShape> by lazy {
            entries.associateBy { it.shapeName }
        }

        /**
         * 根据名称（例如存入数据库的字符串）查找对应的 PresetShape
         */
        fun fromName(name: String?): PresetShape = nameMap[name] ?: CIRCLE

        /**
         * 如果仍然需要在特定 UI 组件中以 List 的形式循环展示
         */
        val allShapes: List<PresetShape> get() = entries

        val presetFiveRandomShape: List<PresetShape>
            get() = PresetShape.entries.shuffled().take(5)

        val defaultShape = CIRCLE
    }
}