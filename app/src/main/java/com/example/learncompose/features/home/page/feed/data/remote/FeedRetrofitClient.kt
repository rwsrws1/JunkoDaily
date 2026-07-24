package com.example.learncompose.features.home.page.feed.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

// 1. 最外层响应体
data class FeedResponse(
    val data: PageInfo,
    val errorCode: Int,
    val errorMsg: String
)

// 2. 分页信息
data class PageInfo(
    val curPage: Int,
    val datas: List<Article>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

// 3. 文章/项目数据实体
data class Article(
    val adminAdd: Boolean,
    val apkLink: String,
    val audit: Int,
    val author: String,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean,
    val host: String,
    val id: Int,
    val isAdminAdd: Boolean,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: List<Tag>,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int
)

// 4. 标签实体
data class Tag(
    val name: String,
    val url: String
)

interface FeedApi {
    @GET("project/list/{page}/json")
    suspend fun getProjects(
        @Path("page") page: Int,
        @Query("cid") cid: Int = 294
    ): FeedResponse
}

object FeedRetrofitClient {
    // 🌟 修复 1：BASE_URL 必须是根域名，且以 / 结尾
    private const val BASE_URL = "https://www.wanandroid.com/"

    // 🌟 新增：配置 OkHttpClient 并添加日志拦截器
    private val okHttpClient: OkHttpClient by lazy {
        // 1. 创建日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // Level.BODY 会打印完整的请求 URL、Headers、参数以及返回的 JSON 响应体
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. 创建并配置 OkHttpClient
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // 挂载日志拦截器
            .connectTimeout(15, TimeUnit.SECONDS) // 设置连接超时时间
            .readTimeout(15, TimeUnit.SECONDS)    // 设置读取超时时间
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // 🌟 新增：告诉 Retrofit 使用我们配置好日志的 OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val feedApi: FeedApi by lazy {
        retrofit.create(FeedApi::class.java)
    }
}