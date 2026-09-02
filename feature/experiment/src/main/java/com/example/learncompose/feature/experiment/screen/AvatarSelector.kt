package com.example.learncompose.feature.experiment.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.learncompose.feature.experiment.R
import java.io.File
import java.io.FileOutputStream

@Composable
fun AvatarSelector(
    modifier: Modifier = Modifier // 👈 允许外部传入 Modifier 控制大小和位置
) {
    val context = LocalContext.current
    val avatarFile = remember { File(context.filesDir, "current_user_avatar.jpg") }
    var avatarTimestamp by remember { mutableLongStateOf(if (avatarFile.exists()) avatarFile.lastModified() else 0L) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val success = saveUriToInternalStorage(context, uri, avatarFile)
            if (success) {
                avatarTimestamp = avatarFile.lastModified() // 更新时间戳
            }
        }
    }

    // 💡 核心修复：根据时间戳动态构建 Coil 的 ImageRequest
    val imageModel = remember(avatarTimestamp) {
        if (avatarTimestamp > 0L) {
            ImageRequest.Builder(context)
                .data(avatarFile)
                // 关键点：在缓存 Key 后面拼上时间戳！
                // 这样每次选新图，Key 都不一样，Coil 就会强制刷新内存和磁盘缓存
                .memoryCacheKey("${avatarFile.absolutePath}?t=$avatarTimestamp")
                .diskCacheKey("${avatarFile.absolutePath}?t=$avatarTimestamp")
                .build()
        } else {
            R.drawable.face_24px // 默认矢量图
        }
    }

    AsyncImage(
        model = imageModel,
        contentDescription = "User Avatar",
        modifier = modifier // 👈 使用外部传入的 modifier
            .clip(CircleShape)
            .clickable {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        contentScale = ContentScale.Crop
    )
}

/**
 * 核心核心工具函数：将选中的系统图片复制到 App 私有目录
 */
fun saveUriToInternalStorage(context: Context, uri: Uri, targetFile: File): Boolean {
    return try {
        // 通过 ContentResolver 打开输入流（凭票取货）
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // 打开私有目录文件的输出流
            FileOutputStream(targetFile).use { outputStream ->
                // 边读边写，完成复制
                inputStream.copyTo(outputStream)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@Preview
@Composable
private fun Preview() {
    AvatarSelector(Modifier.size(100.dp))
}