package com.example.learncompose.core.designsystem.property

import androidx.annotation.DrawableRes
import com.example.learncompose.core.designsystem.R

enum class PresetImage(
    val resName: String,
    @DrawableRes val resId: Int
) {
    BRUSH("brush", R.drawable.brush),
    RUN("run", R.drawable.run),
    WORK("work", R.drawable.work),
    BEDMAKING("bedmaking", R.drawable.bedmaking),
    CAT("cat", R.drawable.cat),
    CHARGE("charge", R.drawable.charge),
    CLEAN("clean", R.drawable.clean),
    COOK("cook", R.drawable.cook),
    DANCE("dance", R.drawable.dance),
    DRINK("drink", R.drawable.drink),
    EARLY("early", R.drawable.early),
    FITNESS("fitness", R.drawable.fitness),
    FRUIT("fruit", R.drawable.fruit),
    MAKEUP("makeup", R.drawable.makeup),
    MEDITATION("mediataion", R.drawable.mediataion),
    NEATEN("neaten", R.drawable.neaten),
    PHOTOGRAPH("photograph", R.drawable.photograph),
    SKIPPING("skipping", R.drawable.skipping),
    SLEEP("sleep", R.drawable.sleep),
    STUDY("study", R.drawable.study),
    VEGETABLES("vegetables", R.drawable.vegetables),
    WALK("walk", R.drawable.walk),
    YOGA("yoga", R.drawable.yoga);

    companion object {
        // 构建静态 Map，通过字符串 resName 做到 O(1) 查找
        private val nameMap: Map<String, PresetImage> by lazy {
            entries.associateBy { it.resName }
        }

        /**
         * 数据库恢复场景：根据存储的字符串名称查找枚举
         * 如果未找到，默认返回第一个图片（如 BRUSH）
         */
        fun fromResName(name: String?): PresetImage {
            return nameMap[name] ?: BRUSH
        }

        /**
         * 替代原来的 PresetImageList（获取所有枚举列表）
         */
        val allImages: List<PresetImage> get() = entries

        /**
         * 随机获取 5 张不重复图片
         */
        val fiveRandomImages: List<PresetImage>
            get() = entries.shuffled().take(5)

        val defaultImage = BRUSH
    }
}