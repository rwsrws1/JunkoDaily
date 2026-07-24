package com.example.learncompose.features.home.page.feed.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.learncompose.features.home.page.feed.data.FeedApi
import com.example.learncompose.features.home.page.feed.data.FeedItem
import com.example.learncompose.features.home.page.feed.data.FeedItemType

// 假设你有一个网络请求 Api 或 Repository
class FeedRepository(
    private val api: FeedApi // 你的网络请求接口
) : PagingSource<Int, FeedItem>() { // <页码类型(通常是Int), 数据类型>

    // 核心方法 1：如何加载数据
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedItem> {
        return try {
            // 1. 获取当前页码。如果是第一次加载，key 为 null，我们默认从第 1 页开始
            val currentPage = params.key ?: 1

            // 2. 发起网络请求获取数据
            // params.loadSize 是 Pager 建议的加载数量
//            val response = api.getProjects(page = currentPage, pageSize = params.loadSize)
            val response = api.getProjects(page = currentPage)

            val feedList = response.data.datas.map { article ->
                FeedItem(
                    id = article.id.toString(),
                    type = if (article.envelopePic.isNotBlank()) FeedItemType.IMAGE else FeedItemType.TEXT,
                    content = "${article.title}\n${article.desc}",
                    imageUrl = article.envelopePic.takeIf { it.isNotBlank() }
                )
            }

            // 3. 返回加载成功的结果，并告诉系统上一页和下一页的页码
            LoadResult.Page(
                data = feedList, // 本页的数据 List<FeedItem>
                // 如果是第一页，上一页就是 null（不能往前滑了）
                prevKey = if (currentPage == 1) null else currentPage - 1,
                // 如果后端返回的数据为空，或者达到了总页数，下一页就是 null（到底了）
                nextKey = if (feedList.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            // 网络异常等错误，直接返回 Error，UI 层会接收到这个状态
            LoadResult.Error(e)
        }
    }
    // 核心方法 2：数据刷新时的基准点（直接抄标准模板即可）
    override fun getRefreshKey(state: PagingState<Int, FeedItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}