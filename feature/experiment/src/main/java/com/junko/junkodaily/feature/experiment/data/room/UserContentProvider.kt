package com.junko.junkodaily.feature.experiment.data.room

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class UserContentProvider : ContentProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UserContentProviderEntryPoint {
        fun userDao(): UserDao
    }

    private lateinit var userDao: UserDao

    companion object {
        // 唯一的 Authority 标识，必须与 Manifest 中一致
        const val AUTHORITY = "com.example.myapp.userprovider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/users")

        private const val CODE_USERS = 1
        private const val CODE_USER_ITEM = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            // content://com.example.myapp.userprovider/users
            addURI(AUTHORITY, "users", CODE_USERS)
            // content://com.example.myapp.userprovider/users/1
            addURI(AUTHORITY, "users/#", CODE_USER_ITEM)
        }
    }

    override fun onCreate(): Boolean {
        val appContext = context?.applicationContext ?: return false
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            UserContentProviderEntryPoint::class.java
        )
        userDao = hiltEntryPoint.userDao()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = when (uriMatcher.match(uri)) {
            CODE_USERS -> userDao.selectAllUsersCursor()
            CODE_USER_ITEM -> {
                val id = ContentUris.parseId(uri)
                userDao.selectUserByIdCursor(id)
            }
            else -> throw IllegalArgumentException("未知的 URI: $uri")
        }
        // 注册监听，数据变更时可以自动通知
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            CODE_USERS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.users"
            CODE_USER_ITEM -> "vnd.android.cursor.item/vnd.$AUTHORITY.users"
            else -> null
        }
    }

    // 如果允许外部写入/删除，需对应调用 userDao 的 insert/delete 方法，这里示例置空或抛出 Unsupported
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}