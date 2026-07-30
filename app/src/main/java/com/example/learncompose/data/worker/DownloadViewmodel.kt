package com.example.learncompose.data.worker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
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

    fun startDownload() {
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .addTag("download_tag")
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