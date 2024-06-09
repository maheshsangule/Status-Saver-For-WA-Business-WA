package com.developermaheshapps.statussaver.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.toRawFile
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.models.MediaModel
import java.io.File
import java.io.FileOutputStream

fun Context.isStatusExist(fileName: String): Boolean {
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val file = File("${downloadDir}/${getString(R.string.folder_path)}", fileName)
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
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        return saveStatusBeforeQ(this, Uri.parse(model.pathUri))
    }

    val extension = getFileExtension(model.fileName)
    val mimeType = "${model.type}/$extension"
    val inputStream = contentResolver.openInputStream(model.pathUri.toUri())
    try {
        val values = ContentValues()
        values.apply {
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.DISPLAY_NAME, model.fileName)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + "/" + getString(R.string.folder_path)
            )
        }
        val uri = contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            values
        )
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
    try {
        val documentFile = DocumentFile.fromTreeUri(context, uri)
        documentFile?.let {
            val sourceFile = documentFile.toRawFile(context)?.takeIf { f2 -> f2.canRead() }
            val destinationFile = sourceFile?.let { sourceF ->
                File(
                    "${Environment.getExternalStorageDirectory()}/Documents/${context.getString(R.string.folder_path)}",
                    sourceF.name
                )
            }

            destinationFile?.let { destFile ->
                if (!destFile.parentFile?.exists()!!) {
                    destFile.parentFile?.mkdirs()
                }

                val sourceStream = sourceFile.inputStream()
                val destStream = FileOutputStream(destFile)
                sourceStream.use { input ->
                    destStream.use { output ->
                        input.copyTo(output, bufferSize = DEFAULT_BUFFER_SIZE)
                    }
                }

                return true
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}



