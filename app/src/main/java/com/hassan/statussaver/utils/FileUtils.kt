package com.hassan.statussaver.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.toRawFile
import com.hassan.statussaver.R
import com.hassan.statussaver.models.MediaModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun Context.isStatusExist(fileName: String): Boolean {
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val file = File("${downloadDir}/${getString(R.string.app_name)}", fileName)
    return file.exists()
}
fun Context.isStatusSaved(fileName: String): Boolean {
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
    val file = File("${downloadDir}/${getString(R.string.app_name)}", fileName)
    return file.exists()
}
fun isStatusExistInStatuses(fileName: String): Boolean {
    var statusesDir = SharedPrefKeys.whatsappDirectoryAdress10p

    if (!statusesDir.exists()) {
        statusesDir = SharedPrefKeys.whatsappDirectoryAdress10m
    } else {
        Log.d("FileUtils", "isStatusExistInStatuses: Status directory does not exist")
    }
    val file = File(statusesDir, fileName)
    return file.exists()
}
fun isStatusExistInBStatuses(fileName: String): Boolean {
    var statusesDir = SharedPrefKeys.whatsappBusinessDirectoryAdress10p

    if (!statusesDir.exists()) {
        statusesDir = SharedPrefKeys.whatsappBusinessDirectoryAdress10m
    } else {
        Log.d("FileUtils", "isStatusExistInStatuses: Status directory does not exist")
    }
    val file = File(statusesDir, fileName)
    return file.exists()
}



fun getFileExtension(fileName: String): String {
    val lastDotIndex = fileName.lastIndexOf(".")

    if (lastDotIndex >= 0 && lastDotIndex < fileName.length - 1) {
        return fileName.substring(lastDotIndex + 1)
    }
    return ""
}

fun Context.saveStatus(model: MediaModel): Boolean {
    if (isStatusExist(model.fileName)) {
        return true
    }

    val extension = getFileExtension(model.fileName)
    val mimeType = "${model.type}/$extension"
    val inputStream = contentResolver.openInputStream(model.pathUri.toUri())

    try {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Handle Android 10 and below
            return saveStatusBeforeQ(this, model.pathUri.toUri(), model)
        } else {
            // Handle Android 11 and above
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.DISPLAY_NAME, model.fileName)

                // Change RELATIVE_PATH based on media type
                val relativePath = if (model.type == "image") {
                    Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name)
                } else { // Assuming video if not image
                    Environment.DIRECTORY_MOVIES + "/" + getString(R.string.app_name)
                }
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            }

            val contentUri = if (model.type == "image") {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val uri = contentResolver.insert(contentUri, values)
            uri?.let {
                val outputStream = contentResolver.openOutputStream(it)
                if (inputStream != null) {
                    outputStream?.write(inputStream.readBytes())
                }
                outputStream?.close()
                inputStream?.close()
                return true
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

private fun saveStatusBeforeQ(context: Context, uri: Uri, model: MediaModel): Boolean {
    try {
        Log.d("FileUtils", "Attempting to save status for URI: $uri")
        val documentFile = DocumentFile.fromTreeUri(context, uri)
        if (documentFile != null) {
            Log.d("FileUtils", "Document file found: ${documentFile.uri}")
            val sourceFile = documentFile.toRawFile(context)?.takeIf { f2 ->
                f2.canRead()
            }
            Log.d("FileUtils", "Source file found: ${sourceFile}")
            if (sourceFile == null) {
                Log.e("FileUtils", "Source file is null or cannot be read")
                return false
            }
            Log.d("FileUtils", "Source file found: ${sourceFile.absolutePath}")
            val destinationFile = sourceFile.let { sourceF ->
                val directory = if (model.type == "image") {
                    Environment.DIRECTORY_PICTURES
                } else {
                    Environment.DIRECTORY_MOVIES
                }
                File(
                    "${Environment.getExternalStorageDirectory()}/$directory/${context.getString(R.string.app_name)}",
                    sourceF.name
                )
            }

            destinationFile.let { destFile ->
                // making dirs & file
                if (!destFile.parentFile?.exists()!!) {
                    destFile.parentFile.mkdirs()
                }
                if (!destFile.exists()) {
                    destFile.createNewFile()
                }

                // copying content from source file to destination file
                val source = FileInputStream(sourceFile).channel
                val destination = FileOutputStream(destFile).channel

                destination.transferFrom(source, 0, source.size())
                source.close()
                destination.close()

                Log.d("FileUtils", "File saved successfully: ${destFile.absolutePath}")
                return true
            }
        }
        Log.e("FileUtils", "Document file is null or cannot read source file")
        return false
    } catch (e: Exception) {
        e.message?.let { Log.d("SaveStatusBeforeQ", it) }
        e.printStackTrace()
        Log.e("FileUtils", "Error saving file: ${e.message}")
        return false
    }
}