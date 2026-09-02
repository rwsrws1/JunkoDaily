package com.example.learncompose.feature.experiment.data.contract

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.learncompose.feature.experiment.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ==================== 数据结构与数据库查询 logic ====================

data class ContactInfo(val name: String, val phoneNumber: String?)

fun getContacts(context: Context): List<ContactInfo> {
    val contactsList = mutableListOf<ContactInfo>()

    val projection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Contacts.HAS_PHONE_NUMBER
    )

    context.contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        projection,
        null,
        null,
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
        val nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
        val hasPhoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

        while (cursor.moveToNext()) {
            val id = cursor.getString(idColumn)
            val name = cursor.getString(nameColumn) ?: "未知"
            val hasPhone = cursor.getInt(hasPhoneColumn) > 0

            var phoneNumber: String? = null

            // 如果有电话号码，查 Phone 表
            if (hasPhone) {
                context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id),
                    null
                )?.use { phoneCursor ->
                    if (phoneCursor.moveToFirst()) {
                        val phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        phoneNumber = phoneCursor.getString(phoneIndex)
                    }
                }
            }

            contactsList.add(ContactInfo(name, phoneNumber))
        }
    }

    return contactsList
}

data class MediaImage(val id: Long, val uri: Uri, val displayName: String)

fun getPhotosFromGallery(context: Context): List<MediaImage> {
    val photoList = mutableListOf<MediaImage>()

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME
    )

    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            photoList.add(MediaImage(id, contentUri, name))
        }
    }

    return photoList
}

// ==================== Compose 页面展示层 ====================

@Composable
fun ContentProviderDemoScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        // Tab 选项卡
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("通讯录") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("相册照片") }
            )
        }

        when (selectedTab) {
            0 -> ContactsTabContent()
            1 -> PhotosTabContent()
        }
    }
}

/**
 * 1. 通讯录页面内容
 */
@Composable
fun ContactsTabContent() {
    val context = LocalContext.current
    var contacts by remember { mutableStateOf<List<ContactInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // 当获得权限后，在后台线程 (Dispatchers.IO) 异步查询数据
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            isLoading = true
            contacts = withContext(Dispatchers.IO) {
                getContacts(context)
            }
            isLoading = false
        }
    }

    if (!hasPermission) {
        PermissionRequestView(
            title = "需要通讯录权限",
            description = "请允许访问系统通讯录以显示联系人数据",
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }
        )
    } else if (isLoading) {
        LoadingView()
    } else if (contacts.isEmpty()) {
        EmptyView(text = "未检测到联系人数据")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts) { contact ->
                ContactItemCard(contact)
            }
        }
    }
}

/**
 * 2. 相册页面内容
 */
@Composable
fun PhotosTabContent() {
    val context = LocalContext.current
    var photos by remember { mutableStateOf<List<MediaImage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // 动态判断 Android 13 (API 33) 及其以上的媒体权限
    val photoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, photoPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // 后台线程异步加载照片
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            isLoading = true
            photos = withContext(Dispatchers.IO) {
                getPhotosFromGallery(context)
            }
            isLoading = false
        }
    }

    if (!hasPermission) {
        PermissionRequestView(
            title = "需要相册权限",
            description = "请允许访问图片库以读取系统相册照片",
            onRequestPermission = { permissionLauncher.launch(photoPermission) }
        )
    } else if (isLoading) {
        LoadingView()
    } else if (photos.isEmpty()) {
        EmptyView(text = "未检测到相册照片")
    } else {
        // 3列网格展示照片
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(photos, key = { it.id }) { photo ->
                PhotoItemCard(photo)
            }
        }
    }
}

// ==================== 复用 UI 子组件 ====================

@Composable
fun ContactItemCard(contact: ContactInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.person_24px_filled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = contact.phoneNumber ?: "无号码",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PhotoItemCard(photo: MediaImage) {
    AsyncImage(
        model = photo.uri,
        contentDescription = photo.displayName,
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PermissionRequestView(
    title: String,
    description: String,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("授权并查看")
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyView(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ContentProviderDemoScreen()
}