package com.developermaheshapps.statussaver.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.developermaheshapps.statussaver.models.MEDIA_TYPE_IMAGE
import com.developermaheshapps.statussaver.models.MEDIA_TYPE_VIDEO
import com.developermaheshapps.statussaver.models.MediaModel
import com.developermaheshapps.statussaver.utils.Constants
import com.developermaheshapps.statussaver.utils.SharedPrefKeys
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import com.developermaheshapps.statussaver.utils.getFileExtension
import com.developermaheshapps.statussaver.utils.isStatusExist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


//class StatusRepo(val context: Context) {
//
//    val whatsAppStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()
//    val whatsAppBusinessStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()
//
//    val activity = context as Activity
//
//    private val wpStatusesList = ArrayList<MediaModel>()
//    private val wpBusinessStatusesList = ArrayList<MediaModel>()
//
//    private val TAG = "StatusRepo"
//
//    fun getAllStatuses(whatsAppType: String = Constants.TYPE_WHATSAPP_MAIN) {
//        val treeUri = when (whatsAppType) {
//            Constants.TYPE_WHATSAPP_MAIN -> {
//                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")?.toUri()!!
//            }
//
//            else -> {
//                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, "")
//                    ?.toUri()!!
//            }
//        }
//        Log.d(TAG, "getAllStatuses: $treeUri")
//
//        activity.contentResolver.takePersistableUriPermission(
//            treeUri,
//            Intent.FLAG_GRANT_READ_URI_PERMISSION
//        )
//
//        val fileDocument = DocumentFile.fromTreeUri(activity, treeUri)
//
//        fileDocument?.let {
//            it.listFiles().forEach { file ->
//                Log.d(TAG, "getAllStatuses: ${file.name}")
//                if (file.name != ".nomedia" && file.isFile) {
//                    val isDownloaded = context.isStatusExist(file.name!!)
//                    Log.d(
//                        TAG,
//                        "getAllStatusesExtension: Extension: ${getFileExtension(file.name!!)} ||${file.name}"
//                    )
//                    val type = if (getFileExtension(file.name!!) == "mp4") {
//                        MEDIA_TYPE_VIDEO
//                    } else {
//                        MEDIA_TYPE_IMAGE
//                    }
//
//                    val model = MediaModel(
//                        pathUri = file.uri.toString(),
//                        fileName = file.name!!,
//                        type = type,
//                        isDownloaded = isDownloaded,
//                        creationDate = file.lastModified()
//                    )
//                    when (whatsAppType) {
//                        Constants.TYPE_WHATSAPP_MAIN -> {
//
//                            wpStatusesList.add(model)
//                        }
//
//                        else -> {
//                            wpBusinessStatusesList.add(model)
//                        }
//
//                    }
//
//                }
//            }
//            when (whatsAppType) {
//                Constants.TYPE_WHATSAPP_MAIN -> {
//                    Log.d(TAG, "getAllStatuses: Pushing Value to Wp live Data")
//                    whatsAppStatusesLiveData.postValue(wpStatusesList)
//                }
//
//                else -> {
//                    Log.d(TAG, "getAllStatuses: Pushing Value to Wp Business live Data")
//                    whatsAppBusinessStatusesLiveData.postValue(wpBusinessStatusesList)
//                }
//
//            }
//
//        }
//
//
//    }
//
//
//}
class StatusRepo(public val context: Context) {

    val whatsAppStatusesLiveData = MutableLiveData<List<MediaModel>>()
    val whatsAppBusinessStatusesLiveData = MutableLiveData<List<MediaModel>>()

    public val activity = context as Activity
    private val TAG = "StatusRepo"

    suspend fun getAllStatuses(whatsAppType: String = Constants.TYPE_WHATSAPP_MAIN) {
        val treeUriString = when (whatsAppType) {
            Constants.TYPE_WHATSAPP_MAIN ->
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")
            else ->
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, "")
        }

        if (treeUriString.isNullOrEmpty()) {
            return
        }

        val treeUri = Uri.parse(treeUriString)

        activity.contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val statusesList = withContext(Dispatchers.IO) {
            val file = DocumentFile.fromTreeUri(context, treeUri)

            val tempList = mutableListOf<MediaModel>()
            file?.listFiles()?.forEach { documentFile ->
                if (documentFile.isFile && documentFile.name != ".nomedia") {
                    val isDownloaded = context.isStatusExist(documentFile.name!!)
                    val type = if (getFileExtension(documentFile.name!!) == "mp4") {
                        MEDIA_TYPE_VIDEO
                    } else {
                        MEDIA_TYPE_IMAGE
                    }
                    val model = MediaModel(
                        pathUri = documentFile.uri.toString(),
                        fileName = documentFile.name!!,
                        type = type,
                        isDownloaded = isDownloaded,
                        creationDate = documentFile.lastModified()
                    )
                    tempList.add(model)
                }
            }
            tempList
        }
        when (whatsAppType) {
            Constants.TYPE_WHATSAPP_MAIN -> {
                whatsAppStatusesLiveData.postValue(statusesList)
            }
            else -> {
                whatsAppBusinessStatusesLiveData.postValue(statusesList)
            }
        }
    }
}













