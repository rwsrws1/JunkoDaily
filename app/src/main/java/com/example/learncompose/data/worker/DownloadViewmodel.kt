package com.example.learncompose.data.worker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(application: Application) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)
    private val _workId = MutableStateFlow<UUID?>(null)

    // 监听指定 ID 的 WorkInfo 状态流
    @OptIn(ExperimentalCoroutinesApi::class)
    val workInfo: StateFlow<WorkInfo?> = _workId.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            workManager.getWorkInfoByIdFlow(id)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 约束条件：必须有网络才触发（断网中断后，恢复网络会自动重启任务）
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun startDownload() {
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .addTag("download_tag")
            .setConstraints(constraints) // 设置网络约束
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL, // 指数退避算法（重试间隔随着失败次数递增，如 10s, 20s, 40s...）
                backoffDelay = WorkRequest.MIN_BACKOFF_MILLIS, // 最小重试间隔（系统限制最低为 10 秒）
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        // 提交唯一任务（防止重复点击触发多次）
        workManager.enqueueUniqueWork(
            "unique_download_work",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        _workId.value = workRequest.id
    }

    fun cancelDownload() {
        _workId.value?.let { id ->
            workManager.cancelWorkById(id)
        }
    }
}