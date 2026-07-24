package com.example.learncompose.features.home.page.feed.data

// --- 1. 数据模型定义 ---
enum class FeedItemType { TEXT, IMAGE }

data class FeedItem(
    val id: String = "",          // 唯一标识符
    val type: FeedItemType = FeedItemType.TEXT,  // 区分内容类型
    val content: String = "",
    val imageUrl: String? = null
)