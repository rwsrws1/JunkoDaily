package com.example.learncompose.feature.experiment.data.contract

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.learncompose.core.designsystem.components.getActivity

/**
 * 跳转到系统设置页
 */
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@Composable
fun SmartPermissionWrapper(
    permission: String = Manifest.permission.READ_CONTACTS,
    permissionName: String = "通讯录",
    onPermissionGranted: () -> Unit,
    content: @Composable (onRequestPermission: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = remember(context) { context.getActivity() }

    // 是否需要展示“去设置”弹窗
    var showGoToSettingsDialog by remember { mutableStateOf(false) }

    // 是否需要展示“权限解释”弹窗（针对首次拒绝但未勾选不再询问）
    var showRationaleDialog by remember { mutableStateOf(false) }

    // 记录是否已经发起了至少一次权限请求（用于区分是“首次请求”还是“永久拒绝”）
    var hasRequestedBefore by rememberSaveable { mutableStateOf(false) }

    // 1. 注册权限申请 Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            // 权限申请被拒绝，标记已请求过
            hasRequestedBefore = true

            // 拒绝后判断：如果 shouldShowRequestPermissionRationale 返回 false，
            // 说明用户勾选了“不再询问”（或 Android 11+ 连续拒绝了两次）
            val showRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false

            if (!showRationale) {
                // 用户勾选了“不再询问”/永久拒绝 -> 弹窗引导去设置页
                showGoToSettingsDialog = true
            }
        }
    }

    // 2. 检查并处理权限请求的统一入口
    val checkAndRequestPermission = {
        val isAlreadyGranted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (isAlreadyGranted) {
            onPermissionGranted()
        } else {
            val showRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false

            when {
                // 情况 A：曾经拒绝过，但没有勾选不再询问 -> 弹解释框告知用户为什么需要
                showRationale -> {
                    showRationaleDialog = true
                }
                // 情况 B：如果之前申请过，且 now showRationale == false -> 永久拒绝状态
                hasRequestedBefore -> {
                    showGoToSettingsDialog = true
                }
                // 情况 C：第一次申请 -> 直接调起系统弹窗
                else -> {
                    launcher.launch(permission)
                }
            }
        }
    }

    // 3. 监听 Lifecycle：从系统设置页切回 App 时，自动重新检测权限状态
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isGranted = ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED

                if (isGranted && showGoToSettingsDialog) {
                    // 如果用户在设置页开启了权限并返回，自动关闭弹窗并触发回调
                    showGoToSettingsDialog = false
                    onPermissionGranted()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 4. 渲染核心UI与弹窗逻辑
    content(checkAndRequestPermission)

    // 弹窗 A：去设置页引导框（永久拒绝场景）
    if (showGoToSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showGoToSettingsDialog = false },
            title = { Text("需要${permissionName}权限") },
            text = { Text("您已禁用了${permissionName}权限。请在系统设置中手动开启，以便正常使用该功能。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showGoToSettingsDialog = false
                        openAppSettings(context)
                    }
                ) {
                    Text("去设置")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoToSettingsDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 弹窗 B：权限功能解释框（普通拒绝场景）
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("权限申请说明") },
            text = { Text("我们需要${permissionName}权限来为您同步联系人数据，请在随后的系统弹窗中点击“允许”。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationaleDialog = false
                        launcher.launch(permission)
                    }
                ) {
                    Text("继续")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}