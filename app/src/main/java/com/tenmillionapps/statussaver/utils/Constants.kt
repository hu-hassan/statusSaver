package com.tenmillionapps.statussaver.utils

import android.net.Uri
import android.os.Build
import android.util.Log

object Constants {

    const val TYPE_WHATSAPP_MAIN = "com.whatsapp"
    const val TYPE_WHATSAPP_BUSINESS = "com.whatsapp.w4b"
    const val TYPE_STATUS_SAVED = "whatsapp_saved"

    const val MEDIA_TYPE_WHATSAPP_IMAGES = "com.whatsapp.images"
    const val MEDIA_TYPE_WHATSAPP_VIDEOS = "com.whatsapp.videos"
    const val MEDIA_TYPE_WHATSAPP_SAVED = "whatsapp_saved"

    const val MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES = "com.whatsapp.w4b.images"
    const val MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS = "com.whatsapp.w4b.videos"

    const val MEDIA_MODEL_KEY = "media_model_key"
    const val MEDIA_LIST_KEY = "MEDIA_LIST"
    const val MEDIA_SCROLL_KEY = "MEDIA_SCROLL"
    const val MEDIA_TYPE_KEY = "MEDIA_TYPE"

    const val FRAGMENT_TYPE_KEY = "TYPE"

    // URIs
    val WHATSAPP_PATH_URI_ANDROID =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AWhatsApp%2FMedia%2F.Statuses")
    val WHATSAPP_PARENT_URI =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fmedia")
    val WHATSAPP_PARENT_URI_OLD =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3A")
    val WHATSAPP_PATH_URI_ANDROID_11 =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")

    val WHATSAPP_BUSINESS_PATH_URI_ANDROID =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AWhatsApp%20Business%2FMedia%2F.Statuses")
    val WHATSAPP_BUSINESS_PATH_URI_ANDROID_11 =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp%20Business%2FMedia%2F.Statuses")

    fun getWhatsappUri(): Uri {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Log.d("Constants","Returning uri of Android 10 +")
            WHATSAPP_PARENT_URI
        } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
            Log.d("Constants","Returning uri of Android 10")
            WHATSAPP_PARENT_URI_OLD
        }
        else {
            Log.d("Constants","Returning uri of Android 10 -")
            WHATSAPP_PARENT_URI_OLD
        }
//        return WHATSAPP_PATH_URI_ANDROID_TEMP
    }
    fun getWhatsappBusinessUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WHATSAPP_BUSINESS_PATH_URI_ANDROID_11
        } else {
            WHATSAPP_BUSINESS_PATH_URI_ANDROID
        }
    }
    object Constants {
        const val WHATSAPP_PACKAGE = "com.whatsapp"
        const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
    }
}
