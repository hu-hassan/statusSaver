package com.hassan.statussaver.viewmodels.factories

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hassan.statussaver.data.StatusRepo
import com.hassan.statussaver.models.MEDIA_TYPE_IMAGE
import com.hassan.statussaver.models.MEDIA_TYPE_VIDEO
import com.hassan.statussaver.models.MediaModel
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.utils.SharedPrefKeys
import com.hassan.statussaver.utils.SharedPrefUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

class StatusViewModel(val repo: StatusRepo) : ViewModel() {
    private val wpStatusLiveData get() = repo.whatsAppStatusesLiveData
    private val wpBusinessStatusLiveData get() = repo.whatsAppBusinessStatusesLiveData
    val wpSavedStatusLiveData get() = repo.whatsAppSavedStatusesLiveData
    private val TAG = "StatusViewModel"

    // wp main
    val whatsAppImagesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppVideosLiveData = MutableLiveData<ArrayList<MediaModel>>()

    // wp business
    val whatsAppBusinessImagesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppBusinessVideosLiveData = MutableLiveData<ArrayList<MediaModel>>()

    //saved statuses
    val savedStatusesImagesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val savedStatusesVideosLiveData = MutableLiveData<ArrayList<MediaModel>>()

    private var isPermissionsGranted = false

    init {
        SharedPrefUtils.init(repo.context)

        val wpPermissions =
            SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, false)
        val wpBusinessPermissions = SharedPrefUtils.getPrefBoolean(
            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
            false
        )

        isPermissionsGranted = wpPermissions && wpBusinessPermissions
        Log.d(TAG, "Status View Model: isPermissions=> $isPermissionsGranted ")
        if (isPermissionsGranted) {
            Log.d(TAG, "Status View Model: Permissions Already Granted Getting Statuses ")
            CoroutineScope(Dispatchers.IO).launch {
                repo.getAllStatuses()

            }
            CoroutineScope(Dispatchers.IO).launch {
                repo.getAllStatuses(Constants.TYPE_WHATSAPP_BUSINESS)
            }
        }
    }

    fun getWhatsAppStatuses() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!isPermissionsGranted) {
                Log.d(TAG, "getWhatsAppStatuses: Requesting WP Statuses")

                repo.getAllStatuses(Constants.TYPE_WHATSAPP_MAIN)
            }

            withContext(Dispatchers.Main){
                getWhatsAppImages()
                getWhatsAppVideos()
            }

        }
    }

    fun getWhatsAppImages() {
        wpStatusLiveData.observe(repo.activity as LifecycleOwner) { mediaList ->
            // Create a defensive copy of the mediaList to avoid ConcurrentModificationException
            val safeList = CopyOnWriteArrayList(mediaList)
            val tempList = ArrayList<MediaModel>()

            for (mediaModel in safeList) {
                if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                    Log.d(TAG, "getWhatsAppImages: ${mediaModel.fileName}")
                    tempList.add(mediaModel)
                }
            }
            // Post the updated list to LiveData
            whatsAppImagesLiveData.postValue(tempList)
        }
    }



    fun getWhatsAppVideos() {
        wpStatusLiveData.observe(repo.activity as LifecycleOwner) { mediaList ->
            val safeList = CopyOnWriteArrayList(mediaList)
            val tempList = ArrayList<MediaModel>()


            for (mediaModel in safeList) {
                if (mediaModel.type == MEDIA_TYPE_VIDEO) {
                    tempList.add(mediaModel)
                }
            }
            whatsAppVideosLiveData.postValue(tempList)
        }
    }


    fun getWhatsAppBusinessStatuses() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!isPermissionsGranted) {
                Log.d(TAG, "getWhatsAppStatuses: Requesting WP Business Statuses")
                repo.getAllStatuses(Constants.TYPE_WHATSAPP_BUSINESS)
            }

            withContext(Dispatchers.Main){
                getWhatsAppBusinessImages()
                getWhatsAppBusinessVideos()
            }

        }
    }

    fun getWhatsAppBusinessImages() {
        wpBusinessStatusLiveData.observe(repo.activity as LifecycleOwner) {mediaList ->

            val safeList = CopyOnWriteArrayList(mediaList)
            val tempList = ArrayList<MediaModel>()

            for (mediaModel in safeList) {
                if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                    tempList.add(mediaModel)
                }
            }
            whatsAppBusinessImagesLiveData.postValue(tempList)
        }
    }
    fun getWhatsAppBusinessVideos() {
        wpBusinessStatusLiveData.observe(repo.activity as LifecycleOwner) {mediaList ->
            val safeList = CopyOnWriteArrayList(mediaList)
            val tempList = ArrayList<MediaModel>()
            for (mediaModel in safeList) {
                if (mediaModel.type == MEDIA_TYPE_VIDEO) {
                    tempList.add(mediaModel)
                }
            }
            whatsAppBusinessVideosLiveData.postValue(tempList)
        }
    }
    fun deleteTrashedItems(filename: String) {
        val directories = listOf(
            File(Environment.getExternalStorageDirectory(), "Pictures/Status Saver"),
            File(Environment.getExternalStorageDirectory(), "Movies/Status Saver")
        )
        val filenamesToDelete = filename

        directories.forEach { directory ->
            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()?.forEach { file ->
                    if (file.name.contains(".trashed-"+filename) || filenamesToDelete.contains(file.name)) {
                        file.delete()
                    }
                }
            }
        }
    }
    fun getSavedStatuses() {
        Log.d(TAG, "StatusViewModel: getSavedStatuses called")
        CoroutineScope(Dispatchers.IO).launch {
            repo.getSavedStatuses()
            withContext(Dispatchers.Main){
                getSavedStatusImages()
                getSavedStatusVideos()
            }
        }
    }
    private val tempListSI = mutableListOf<MediaModel>()
    fun getSavedStatusImages() {
        wpSavedStatusLiveData.observe(repo.activity as LifecycleOwner) {mediaList ->
            val safeList = CopyOnWriteArrayList(mediaList)
            tempListSI.clear()
            for (mediaModel in safeList) {
                if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                    tempListSI.add(mediaModel)
                }
            }
            savedStatusesImagesLiveData.postValue(tempListSI as ArrayList<MediaModel>?)
        }
    }
    private val tempListSV = mutableListOf<MediaModel>()
    fun getSavedStatusVideos() {
        wpSavedStatusLiveData.observe(repo.activity as LifecycleOwner) {mediaList ->
            val safeList = CopyOnWriteArrayList(mediaList)
            tempListSV.clear()
            for (mediaModel in safeList) {
                if (mediaModel.type == MEDIA_TYPE_VIDEO) {
                    tempListSV.add(mediaModel)
                }
            }
            savedStatusesVideosLiveData.postValue(tempListSV as ArrayList<MediaModel>?)
        }
    }



}

//class SharedViewModel : ViewModel() {
//    private val _fragmentType = MutableLiveData<String>()
//    val fragmentType: LiveData<String> get() = _fragmentType
//
//    fun setFragmentType(type: String) {
//        _fragmentType.value = type
//    }
//    fun getFragmentType(): String? {
//        return _fragmentType.value
//    }
//}