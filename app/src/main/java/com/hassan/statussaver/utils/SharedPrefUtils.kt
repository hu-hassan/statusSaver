package com.hassan.statussaver.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.hassan.statussaver.R
import java.io.File

@OptIn(UnstableApi::class)
object SharedPrefUtils {
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            "${context.getString(R.string.app_name).replace(" ", "_")}_prefs",
            Context.MODE_PRIVATE
        )
    }
    fun clearPreferences() {
        preferences.edit().clear().apply()  // Clear all existing preferences
    }

    fun getPrefString(key: String, defaultValue: String): String? {
        return preferences.getString(key, null)
    }

    fun getPrefBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = preferences.getBoolean(key, defaultValue)
        return value
    }

    fun getPrefInt(key: String, defaultValue: Int): Int {
        return preferences.getInt(key, 0)
    }

    fun putPrefString(key: String, value: String): Boolean {
        preferences.edit().putString(key, value).apply()
        return true
    }

    fun putPrefInt(key: String, value: Int): Boolean {
        preferences.edit().putInt(key, value).apply()
        return true
    }

    fun putPrefBoolean(key: String, value: Boolean): Boolean {
        preferences.edit().putBoolean(key, value).apply()
        return true
    }

    fun setPrefString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

}

object SharedPrefKeys {
    const val PREF_KEY_WP_PERMISSION_GRANTED = "PREF_KEY_PERMISSION_GRANTED"
    const val PREF_KEY_WP_TREE_URI = "PREF_KEY_TREE_URI"
    const val PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED = "PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED"
    const val PREF_KEY_WP_BUSINESS_TREE_URI = "PREF_KEY_WP_BUSINESS_TREE_URI"
    const val PREF_KEY_IS_PERMISSIONS_GRANTED = "PREF_KEY_IS_PERMISSIONS_GRANTED"
    @kotlin.jvm.JvmStatic
    val whatsappDirectoryAdress10p = File("${Environment.getExternalStorageDirectory()}/Android/media/com.whatsapp/WhatsApp/Media/.Statuses")
    @kotlin.jvm.JvmStatic
    val whatsappDirectoryAdress10m = File("${Environment.getExternalStorageDirectory()}/WhatsApp/Media/.Statuses")
    @kotlin.jvm.JvmStatic
    val whatsappBusinessDirectoryAdress10p = File("${Environment.getExternalStorageDirectory()}/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses")
    @kotlin.jvm.JvmStatic
    val whatsappBusinessDirectoryAdress10m = File("${Environment.getExternalStorageDirectory()}/WhatsApp Business/Media/.Statuses")
}