package com.example.learncompose.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// --- 1. 数据模型定义 ---
enum class FeedItemType { TEXT, IMAGE }

data class FeedItem(
    val id: String = "",          // 唯一标识符
    val type: FeedItemType = FeedItemType.TEXT,  // 区分内容类型
    val content: String = "",
    val imageUrl: String? = null
)

// --- 2. 页面 UI 实现 ---
@Composable
fun OptimizedFeedScreen(
    // 假设 ViewModel 吐出了一个 Paging 3 的数据流
    pagingDataFlow: Flow<PagingData<FeedItem>>
) {
    // 🌟 优化 1 (物理): 接入 Paging 3
    // collectAsLazyPagingItems 会自动处理分页加载、内存回收和数据流生命周期
    val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 🌟 优化 3.3 (Compose 专属): 使用 derivedStateOf 隔离滑动状态读取
    // 绝对不要直接写 `if (listState.firstVisibleItemIndex > 5)`，那会导致列表每滑动一像素，整个页面重组一次！
    // derivedStateOf 会将高频变化的滑动偏移量，转换为低频的布尔值变化，极大地节省性能。
    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                // Paging 3 在 Compose 中的标准写法
                count = lazyPagingItems.itemCount,

                // 🌟 优化 3.1 (Compose 专属): 提供绝对唯一的 Key
                // Paging 扩展函数 itemKey 会自动处理 null 占位符的情况。这避免了数据插入/删除时的全局卡顿。
                key = lazyPagingItems.itemKey { it.id },

                // 🌟 优化 3.2 (Compose 专属): 提供 ContentType
                // 让纯文本卡片和图片卡片进入各自专属的“复用池”，避免复杂的测量混乱，提升滑动帧率。
                contentType = lazyPagingItems.itemContentType { it.type }
            ) { index ->
                val item = lazyPagingItems[index]
                if (item != null) {
                    // 根据类型渲染不同的 UI
                    when (item.type) {
                        FeedItemType.TEXT -> TextCard(item)
                        FeedItemType.IMAGE -> ImageCard(item,)
                    }
                }
            }

            // 处理 Paging 3 的底部加载状态
            when (lazyPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                is LoadState.Error -> {
                    item {
                        Text("加载失败，请重试", modifier = Modifier.padding(16.dp))
                    }
                }
                is LoadState.NotLoading -> Unit
            }
        }

        // 回到顶部按钮（依赖隔离后的滑动状态）
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "回到顶部")
            }
        }
    }
}

// --- 3. 子组件实现 ---

@Composable
fun TextCard(item: FeedItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = item.content,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ImageCard(item: FeedItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = item.content,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            // 🌟 优化 2 (物理): 使用 Coil 的 AsyncImage 进行图片加载
            // 它底层自带内存缓存池、磁盘缓存，并且会在卡片滑出屏幕（离开 Composition）时，自动取消网络请求并释放 Bitmap。
            if (item.imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .listener(
                            onStart = { Log.d("CoilTest", "开始发起网络请求...") },
                            onSuccess = { _, _ -> Log.d("CoilTest", "图片加载成功！") },
                            onError = { _, result -> Log.e("CoilTest", "加载失败，原因:", result.throwable) }
                        )
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f), // 强制比例，避免图片下载完成时高度突变导致列表跳动
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(com.example.learncompose.R.drawable.autorenew_24px), // 替换为你项目里的占位图资源
                    error = painterResource(com.example.learncompose.R.drawable.do_not_disturb_on_24px)
                )
            }
        }
    }
}

//// 假设你有一个网络请求 Api 或 Repository
//class FeedPagingSource(
//    private val api: FeedApi // 你的网络请求接口
//) : PagingSource<Int, FeedItem>() { // <页码类型(通常是Int), 数据类型>
//
//    // 核心方法 1：如何加载数据
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedItem> {
//        return try {
//            // 1. 获取当前页码。如果是第一次加载，key 为 null，我们默认从第 1 页开始
//            val currentPage = params.key ?: 1
//
//            // 2. 发起网络请求获取数据
//            // params.loadSize 是 Pager 建议的加载数量
//            val response = api.fetchFeed(page = currentPage, pageSize = params.loadSize)
//
//            // 3. 返回加载成功的结果，并告诉系统上一页和下一页的页码
//            LoadResult.Page(
//                data = response.items, // 本页的数据 List<FeedItem>
//                // 如果是第一页，上一页就是 null（不能往前滑了）
//                prevKey = if (currentPage == 1) null else currentPage - 1,
//                // 如果后端返回的数据为空，或者达到了总页数，下一页就是 null（到底了）
//                nextKey = if (response.items.isEmpty()) null else currentPage + 1
//            )
//        } catch (e: Exception) {
//            // 网络异常等错误，直接返回 Error，UI 层会接收到这个状态
//            LoadResult.Error(e)
//        }
//    }
//    // 核心方法 2：数据刷新时的基准点（直接抄标准模板即可）
//    override fun getRefreshKey(state: PagingState<Int, FeedItem>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//}

// 假设后端返回的 JSON 格式是：
// {
//   "code": 200,
//   "message": "success",
//   "items": [ { "id": "1", "type": "TEXT", ... } ]
// }
//
//data class FeedResponse(
//    val code: Int,
//    val message: String,
//    val items: List<FeedItem> // 这里的 FeedItem 就是我们之前定义的那个实体类
//)
//
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface FeedApi {
//    // 假设你的接口地址是 https://api.yoursite.com/v1/feed
//    // 这里的 @GET 里面写相对路径
//    @GET("v1/feed")
//    suspend fun fetchFeed(
//        // @Query 会自动把参数拼在 URL 后面，例如：?page=1&pageSize=20
//        @Query("page") page: Int,
//        @Query("pageSize") pageSize: Int
//    ): FeedResponse
//    // Retrofit 会自动把网络请求的 JSON 结果解析成 FeedResponse 对象
//}
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object RetrofitClient {
//    // 你的服务器根域名（注意最后一定要以 / 结尾）
//    private const val BASE_URL = "https://api.yoursite.com/"
//
//    // 使用 lazy 延迟初始化，只有在第一次调用时才会创建
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            // 添加 Gson 转换器，用来处理 JSON 和 Kotlin 对象的互相转换
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    // 暴露给 ViewModel 或 Repository 使用的 API 实例
//    val feedApi: FeedApi by lazy {
//        retrofit.create(FeedApi::class.java)
//    }
//}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    // 1. 生成 50 条假数据
    val fakeList = List(50) { index ->
        val isImage = index % 3 == 0 // 每隔 3 个造一个图片类型
        FeedItem(
            id = index.toString(),
            type = if (isImage) FeedItemType.IMAGE else FeedItemType.TEXT,
            content = "这是第 $index 条模拟假数据",
            imageUrl = if (isImage) "https://picsum.photos/seed/$index/400/200" else null // 用 picsum 生成随机占位图
        )
    }

    // 2. 直接塞给 Flow
    val mockPagingDataFlow: Flow<PagingData<FeedItem>> = flowOf(PagingData.from(fakeList))
    OptimizedFeedScreen(mockPagingDataFlow)
}