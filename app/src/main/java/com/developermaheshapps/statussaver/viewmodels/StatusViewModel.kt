package com.developermaheshapps.statussaver.viewmodels

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.developermaheshapps.statussaver.data.StatusRepo
import com.developermaheshapps.statussaver.models.MEDIA_TYPE_IMAGE
import com.developermaheshapps.statussaver.models.MEDIA_TYPE_VIDEO
import com.developermaheshapps.statussaver.models.MediaModel
import com.developermaheshapps.statussaver.utils.Constants
import com.developermaheshapps.statussaver.utils.SharedPrefKeys
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatusViewModel(val repo: StatusRepo) : ViewModel() {
    private val wpStatusLiveData get() = repo.whatsAppStatusesLiveData
    private val wpBusinessStatusLiveData get() = repo.whatsAppBusinessStatusesLiveData
    private val TAG = "StatusViewModel"

    // wp main
    val whatsAppImagesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppVideosLiveData = MutableLiveData<ArrayList<MediaModel>>()

    // wp business
    val whatsAppBusinessImagesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppBusinessVideosLiveData = MutableLiveData<ArrayList<MediaModel>>()

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
                repo.getAllStatuses(Constants.TYPE_WHATSAPP_BUSINESS)
            }
        }
    }

    fun getWhatsAppStatuses() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!isPermissionsGranted) {
                Log.d(TAG, "getWhatsAppStatuses: Requesting WP Statuses")

                repo.getAllStatuses()
            }

            withContext(Dispatchers.Main) {
                getWhatsAppImages()
                getWhatsAppVideos()
            }

        }
    }

    fun getWhatsAppImages() {
        wpStatusLiveData.observe(repo.activity as LifecycleOwner) { statusList ->

            val tempList = ArrayList<MediaModel>()

            // Create a copy of the list to avoid ConcurrentModificationException

            val copyList = ArrayList(statusList)
            copyList.forEach { mediaModel ->
                if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                    tempList.add(mediaModel)
                }
            }

            whatsAppImagesLiveData.postValue(tempList)
        }
    }


    fun getWhatsAppVideos() {
        wpStatusLiveData.observe(repo.activity as LifecycleOwner) { statusList ->
            val tempList = ArrayList<MediaModel>()

            // Create a copy of the list to avoid ConcurrentModificationException
            val copyList = ArrayList(statusList)

            copyList.forEach { mediaModel ->
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

            withContext(Dispatchers.Main) {
                getWhatsAppBusinessImages()
                getWhatsAppBusinessVideos()
            }

        }
    }

    fun getWhatsAppBusinessImages() {
        wpBusinessStatusLiveData.observe(repo.activity as LifecycleOwner) {
            wpBusinessStatusLiveData.observe(repo.activity as LifecycleOwner) { statusList ->
                val tempList = ArrayList<MediaModel>()

                // Create a copy of the list to avoid ConcurrentModificationException
                val copyList = ArrayList(statusList)

                copyList.forEach { mediaModel ->
                    if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                        tempList.add(mediaModel)
                    }
                }
                whatsAppBusinessImagesLiveData.postValue(tempList)
            }
        }
    }

    fun getWhatsAppBusinessVideos() {
        wpBusinessStatusLiveData.observe(repo.activity as LifecycleOwner) { statusList ->
            val tempList = ArrayList<MediaModel>()

            // Create a copy of the list to avoid ConcurrentModificationException
            val copyList = ArrayList(statusList)

            copyList.forEach { mediaModel ->
                if (mediaModel.type == MEDIA_TYPE_VIDEO) {
                    tempList.add(mediaModel)
                }
            }
            whatsAppBusinessVideosLiveData.postValue(tempList)
        }
    }


}












