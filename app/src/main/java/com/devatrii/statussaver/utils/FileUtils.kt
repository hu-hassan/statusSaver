package com.devatrii.statussaver.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.toRawFile
import com.devatrii.statussaver.R
import com.devatrii.statussaver.models.MediaModel
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
    var statusesDir = SharedPrefKeys.whatsappDirectoryAdress10p

    if (!statusesDir.exists()) {
        statusesDir = SharedPrefKeys.whatsappDirectoryAdress10m
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
        val values = ContentValues()
        values.apply {
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
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

private fun saveStatusBeforeQ(context: Context, uri: Uri): Boolean {
    // converting doc file to file
    try {
        val documentFile = DocumentFile.fromTreeUri(context, uri)
        if (documentFile != null) {
            val sourceFile = documentFile.toRawFile(context)?.takeIf { f2 ->
                f2.canRead()
            }
            val destinationFile = sourceFile?.let { sourceF ->
                File(
                    "${Environment.getExternalStorageDirectory()}/Documents/${context.getString(R.string.app_name)}",
                    sourceF.name
                )
            }

            destinationFile?.let { destFile ->
                // making dirs & file
                if (!destFile.parentFile?.exists()!!) {
                    destFile.mkdirs()
                }
                if (!destFile.exists()) {
                    destFile.createNewFile()
                }

                // copying content from dest file to source file
                val source = FileInputStream(sourceFile).channel
                val destination = FileOutputStream(destFile).channel

                destination.transferFrom(source, 0, source.size())
                source.close()
                destination.close()


                return true

            }
        }
        return false
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }


}















