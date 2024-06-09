package com.developermaheshapps.statussaver.views.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.databinding.ItemImagePreviewBinding
import com.developermaheshapps.statussaver.models.MediaModel
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import com.developermaheshapps.statussaver.utils.saveStatus
import java.io.File
import kotlin.math.log

class ImagePreviewAdapter (val list: ArrayList<MediaModel>, val context: Activity) :
    RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                tools.llRepost.setOnClickListener {
                    try {

                        val intent=Intent()
                        val videoPath = mediaModel.pathUri.toString()
                        val videoUri = FileProvider.getUriForFile(
                          context,
                            context.packageName + ".fileprovider",
                            File(videoPath)
                        )

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "video/*"
                            putExtra(Intent.EXTRA_STREAM, videoUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            `package` = SharedPrefUtils.getPrefString("package", "")
                                .toString() // Specify WhatsApp package
                        }

                        context.startActivity(shareIntent)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(zoomableImageView)

                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)

                tools.llShare.setOnClickListener {
                    val imageUri = Uri.parse(mediaModel.pathUri.toString())
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "image/*"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)

                    try {
                        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                    }
                    catch (e:Exception)
                    {
                        e.printStackTrace()
                    }


//                    try {
//                        val imagePath =
//                            mediaModel.pathUri.toString()
//                        Log.d("Share", mediaModel.pathUri.toString())// Replace with the actual path of your image file
////                            val imageUri = Uri.parse("file://$imagePath")
//                        val imageUri = FileProvider.getUriForFile(
//                            context,
//                            context.packageName + ".fileprovider",
//                            File(imagePath)
//                        )
//
//                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                            type = "image/*"
//                            putExtra(Intent.EXTRA_STREAM, imageUri)
//                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                        }
//
//                        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
//                    } catch (e: Exception) {
//                        Log.d("Share", e.localizedMessage.toString())
//                    }
                }

                tools.llRepost.setOnClickListener {
                    try {

                        val imageUri =Uri.parse(mediaModel.pathUri.toString())
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, imageUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        }
                        val whatsappPackage = "com.whatsapp"
                        val businessWhatsAppPackage = "com.whatsapp.w4b"
                        val packageName = SharedPrefUtils.getPrefString("package", "").toString()

                        if(packageName==businessWhatsAppPackage)
                        {
                            if (context.packageManager.getLaunchIntentForPackage(packageName) != null) {
                                shareIntent.setPackage(packageName)
                                context.startActivity(shareIntent)

                            } else {
                                Utils.appNotFound(context ,"w4bw")
                            }
                        }

                        else  if(packageName==whatsappPackage)
                        {
                            if (context.packageManager.getLaunchIntentForPackage(packageName) != null) {
                                shareIntent.setPackage(packageName)
                                context.startActivity(shareIntent)

                            } else {
                                Utils.appNotFound(context ,"ww")
                            }
                        }



                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


                tools.download.setOnClickListener {
                    val isDownloaded = context.saveStatus(mediaModel)
                    if (isDownloaded) {
                        Log.d("Share", isDownloaded.toString())
                        // status is downloaded
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        mediaModel.isDownloaded = true
                        tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
                    } else {
                        // unable to download status
                        Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagePreviewAdapter.ViewHolder {
        return ViewHolder(ItemImagePreviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ImagePreviewAdapter.ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size

}











