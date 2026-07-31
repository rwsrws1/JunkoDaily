package com.example.learncompose.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // 暴露一个表示当前网络是否可用的 Flow
    val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true) // 网络可用
            }

            override fun onLost(network: Network) {
                trySend(false) // 网络断开
            }
        }

        // 构建网络请求条件：需要具有 INTERNET 能力的网络
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // 注册回调
        connectivityManager.registerNetworkCallback(request, callback)

        // 初始化时先发送一次当前网络状态
        val currentState = isCurrentlyConnected()
        trySend(currentState)

        // 当 Flow 收集结束/取消时，自动注销回调，防止内存泄漏
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged() // 防抖：只有状态真正改变时才发出新值

    // 辅助检查当前初始网络连接状态
    private fun isCurrentlyConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}