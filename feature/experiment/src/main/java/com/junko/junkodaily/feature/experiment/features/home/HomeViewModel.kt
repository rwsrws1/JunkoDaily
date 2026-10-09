package com.junko.junkodaily.feature.experiment.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.junko.junkodaily.feature.experiment.features.home.page.feed.data.FeedItem
import com.junko.junkodaily.feature.experiment.features.home.page.feed.data.remote.FeedApi
import com.junko.junkodaily.feature.experiment.features.home.page.feed.data.remote.FeedRetrofitClient
import com.junko.junkodaily.feature.experiment.features.home.page.feed.data.repo.FeedRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    private val api: FeedApi = FeedRetrofitClient.feedApi // 依赖注入或直接实例化
) : ViewModel() {

    // 这就是 UI 层需要的那个 Flow
    val pagingDataFlow: Flow<PagingData<FeedItem>> = Pager(
        // 1. 配置分页参数
        config = PagingConfig(
            pageSize = 20,          // 每页加载多少条数据
            prefetchDistance = 1,   // 距离底部还有几条数据时，提前去拉取下一页（非常重要，保证滑动丝滑）
            initialLoadSize = 20    // 第一次加载时拉取的数量（通常是 pageSize 的 2-3 倍）
        ),
        // 2. 告诉 Pager 怎么创建 PagingSource
        pagingSourceFactory = {
            FeedRepository(api)
        }
    )
        .flow
        // 3. 极其关键的一步：将数据流缓存在 ViewModel 作用域中！
        // 这样当屏幕旋转（Activity 重建）时，之前加载的几百条数据都在内存里，不需要重新请求网络。
        .cachedIn(viewModelScope)
}