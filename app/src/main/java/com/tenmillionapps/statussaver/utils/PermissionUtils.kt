package com.tenmillionapps.statussaver.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log

fun getFolderPermissions(context: Context, REQUEST_CODE: Int, initialUri: Uri) {
    Log.d("PermissionUtils", "getFolderPermissions: Requesting folder permissions"+"${initialUri}")
    val activity = context as Activity
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri)
//    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri)
    intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
    activity.startActivityForResult(intent, REQUEST_CODE)
}
