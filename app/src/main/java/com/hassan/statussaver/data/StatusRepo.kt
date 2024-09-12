package com.hassan.statussaver.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.hassan.statussaver.R
import com.hassan.statussaver.models.MEDIA_TYPE_IMAGE
import com.hassan.statussaver.models.MEDIA_TYPE_VIDEO
import com.hassan.statussaver.models.MediaModel
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.utils.SharedPrefKeys
import com.hassan.statussaver.utils.SharedPrefUtils
import com.hassan.statussaver.utils.getFileExtension
import com.hassan.statussaver.utils.isStatusExist
import com.hassan.statussaver.utils.isStatusSaved
import java.io.File

class StatusRepo(val context: Context) {

    val whatsAppStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppBusinessStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppSavedStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()

    val activity = context as Activity

    private val wpStatusesList = ArrayList<MediaModel>()
    private val wpBusinessStatusesList = ArrayList<MediaModel>()
    private val wpSavedStatusesList = ArrayList<MediaModel>()

    private val TAG = "StatusRepo"

    fun getAllStatuses(whatsAppType: String = Constants.TYPE_WHATSAPP_MAIN) {
        val treeUri = when (whatsAppType) {
            Constants.TYPE_WHATSAPP_MAIN -> {
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")?.toUri()
            }
            else -> {
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, "")?.toUri()
            }
        }

        if (treeUri == null) {
            Log.e(TAG, "getAllStatuses: Tree URI is null")
            return
        }

        Log.d(TAG, "getAllStatuses: $treeUri")

        val uriPermission = activity.contentResolver.persistedUriPermissions
            .find { it.uri == treeUri && it.isReadPermission }

        if (uriPermission == null) {
            requestUriPermission(treeUri)
            return
        }

        val fileDocument = DocumentFile.fromTreeUri(activity, treeUri)
        if (fileDocument == null) {
            Log.e(TAG, "getAllStatuses: DocumentFile is null")
            return
        }

        fileDocument.listFiles().forEach { file ->
            Log.d(TAG, "getAllStatuses: ${file.name}")
            if (file.name != ".nomedia" && file.isFile) {
                val isDownloaded = context.isStatusExist(file.name!!) || context.isStatusSaved(file.name!!)

                val type = if (getFileExtension(file.name!!) == "mp4") {
                    MEDIA_TYPE_VIDEO
                } else {
                    MEDIA_TYPE_IMAGE
                }

                val model = MediaModel(
                    pathUri = file.uri.toString(),
                    fileName = file.name!!,
                    type = type,
                    isDownloaded = isDownloaded
                )

                when (whatsAppType) {
                    Constants.TYPE_WHATSAPP_MAIN -> {
                        Log.d(TAG, "getAllStatuses: Adding to WhatsApp Statuses")
                        if (!model.fileName.contains(".png")) {
                            wpStatusesList.add(model)
                        }
                    }
                    else -> {
                        if (!model.fileName.contains(".png")) {
                            wpBusinessStatusesList.add(model)
                        }
                    }
                }
            }
        }

        when (whatsAppType) {
            Constants.TYPE_WHATSAPP_MAIN -> {
                Log.d(TAG, "getAllStatuses: Pushing Value to WhatsApp live data")
                whatsAppStatusesLiveData.postValue(wpStatusesList)
            }
            else -> {
                Log.d(TAG, "getAllStatuses: Pushing Value to WhatsApp Business live data")
                whatsAppBusinessStatusesLiveData.postValue(wpBusinessStatusesList)
            }
        }
    }

    private fun requestUriPermission(uri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        activity.startActivityForResult(intent, REQUEST_CODE_URI_PERMISSION)
    }

    companion object {
        const val REQUEST_CODE_URI_PERMISSION = 1001
    }
    // In StatusRepo.kt
    // In StatusRepo.kt
    fun getSavedStatuses() {
        Log.d(TAG, "getSavedStatuses: Getting Saved Statuses")
        val downloadDirI = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val downloadDirV = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val savedStatusesI = File("${downloadDirI}/${context.getString(R.string.app_name)}").listFiles()
        val savedStatusesV = File("${downloadDirV}/${context.getString(R.string.app_name)}").listFiles()
        val uniqueFiles = mutableSetOf<String>()

        savedStatusesI?.let { files ->
            val filesCopy = files.toList() // Create a copy of the list
            filesCopy.forEach { file ->
                Log.d(TAG, "getSavedStatuses: File: " + getFileExtension(file.name))
                if (file.isFile) {
                    if (uniqueFiles.add(file.name)) { // Add to set and check for duplicates
                        val isDownloaded = context.isStatusSaved(file.name)
                        val type = MEDIA_TYPE_IMAGE

                        val model = MediaModel(
                            pathUri = file.toUri().toString(),
                            fileName = file.name,
                            type = type,
                            isDownloaded = isDownloaded
                        )
                        if (!model.fileName.contains(".trashed-")) {
                            wpSavedStatusesList.add(model)
                        }
                    }
                }
            }
        }
        whatsAppSavedStatusesLiveData.postValue(wpSavedStatusesList)

        savedStatusesV?.let { files ->
            val filesCopy = files.toList() // Create a copy of the list
            filesCopy.forEach { file ->
                Log.d(TAG, "getSavedStatuses: File: " + getFileExtension(file.name))
                if (file.isFile) {
                    if (uniqueFiles.add(file.name)) { // Add to set and check for duplicates
                        val isDownloaded = context.isStatusSaved(file.name)
                        val type = MEDIA_TYPE_VIDEO

                        val model = MediaModel(
                            pathUri = file.toUri().toString(),
                            fileName = file.name,
                            type = type,
                            isDownloaded = isDownloaded
                        )
                        if (!model.fileName.contains(".trashed-")) {
                            wpSavedStatusesList.add(model)
                        }
                    }
                }
            }
        }
        whatsAppSavedStatusesLiveData.postValue(wpSavedStatusesList)

    }
}