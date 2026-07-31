package com.example.learncompose.data.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val _locationState = MutableStateFlow<LocationData?>(null)
    val locationState: StateFlow<LocationData?> = _locationState.asStateFlow()

    private val _isServiceBound = MutableStateFlow(false)
    val isServiceBound: StateFlow<Boolean> = _isServiceBound.asStateFlow()

    // 1. 移除 `private var locationService: LocationService? = null`
    // 改为只保存 Job，用于取消监听
    private var locationCollectJob: Job? = null

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 2. 从 binder 转换出 Service
            val binder = service as? LocationService.LocalBinder
            val locationService = binder?.getService() ?: return

            _isServiceBound.value = true

            // 3. 开始监听 Flow，不要把 locationService 存为 ViewModel 的成员变量
            locationCollectJob?.cancel()
            locationCollectJob = viewModelScope.launch {
                locationService.locationFlow.collect { location ->
                    _locationState.value = location
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _isServiceBound.value = false
            locationCollectJob?.cancel()
        }
    }

    fun bindService(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        // 使用 applicationContext 绑定，进一步规避 Context 泄漏风险
        context.applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (_isServiceBound.value) {
            context.applicationContext.unbindService(serviceConnection)
            _isServiceBound.value = false
            locationCollectJob?.cancel()
        }
    }

    fun stopService(context: Context) {
        unbindService(context)
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        context.startService(intent)
        _locationState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        locationCollectJob?.cancel()
    }
}