package com.example.learncompose.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import coil3.ImageLoader
import coil3.PlatformContext
import dagger.hilt.android.HiltAndroidApp
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
//import com.example.learncompose.data.room.AppContainer

@HiltAndroidApp
class LearnApplication : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
//        AppContainer.init(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            }

            override fun onActivityDestroyed(p0: Activity) {
            }

            override fun onActivityPaused(p0: Activity) {
            }

            override fun onActivityResumed(p0: Activity) {
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityStarted(p0: Activity) {
            }

            override fun onActivityStopped(p0: Activity) {
            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                println("App 进入前台")
                // App 进入前台
            }

            override fun onStop(owner: LifecycleOwner) {
                // App 进入后台
                println("App 进入后台")
            }
        })
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        println("onTrimMemory level = $level")
        if (level >= TRIM_MEMORY_BACKGROUND) {
            // 当系统内存紧张时，立即清空 Coil 的内存缓存，释放几十 MB 空间保命
            SingletonImageLoader.get(this).memoryCache?.clear()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        println("onLowMemory")
        SingletonImageLoader.get(this).memoryCache?.clear()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(256 * 1024 * 1024L)
                    .build()
            }
            .build()
    }
}