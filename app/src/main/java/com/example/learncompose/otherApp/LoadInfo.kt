package com.example.learncompose.otherApp

import android.content.Context
import android.net.Uri
import android.util.Log

fun fetchRemoteUsers(context: Context) {
    val uri = Uri.parse("content://com.example.myapp.userprovider/users")

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex("name")
        val ageIndex = cursor.getColumnIndex("age")

        while (cursor.moveToNext()) {
            val name = cursor.getString(nameIndex)
            val age = cursor.getInt(ageIndex)
            Log.d("OtherApp", "读取到外部用户: $name, $age")
        }
    }
}