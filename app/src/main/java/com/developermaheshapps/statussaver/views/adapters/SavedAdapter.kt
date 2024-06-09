package com.developermaheshapps.statussaver.views.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.data.MediaType
import com.developermaheshapps.statussaver.data.SavedItem
import com.developermaheshapps.statussaver.databinding.ItemDownloadedBinding
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import com.developermaheshapps.statussaver.views.activities.SavedImagePreview
import com.developermaheshapps.statussaver.views.activities.SavedVideoPreview
import java.io.File

// SavedAdapter.kt
class SavedAdapter(private val context: Activity, private val mediaList: ArrayList<SavedItem>) :
    RecyclerView.Adapter<SavedAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(
            ItemDownloadedBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaItem = mediaList[position]
        holder.bind(mediaItem)
    }

    override fun getItemCount(): Int = mediaList.size

    inner class MediaViewHolder(val binding: ItemDownloadedBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        private val thumbnailImageView: ImageView = itemView.findViewById(R.id.saved_image)
//        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)

        fun bind(mediaItem: SavedItem) {
//            Glide.with(context)
//                .load(if (mediaItem.type == MediaType.VIDEO) R.drawable.ic_play else R.drawable.play_icon)
//                .into(thumbnailImageView)

            binding.apply {

                loadThumbnail(context, mediaItem, savedImage)

                if (mediaItem.type == MediaType.PHOTO) {
                    statusPlay.visibility = View.GONE
                } else if (mediaItem.type == MediaType.VIDEO) {
                    statusPlay.visibility = View.VISIBLE
                }


//                savedImage.setImageResource(R.drawable.temp_image)


//            titleTextView.text = getMediaTitle(mediaItem.filePath)

                statusRepost.setOnClickListener {
                    if (mediaItem.type == MediaType.PHOTO) {


                        try {

                            val videoPath = mediaItem.filePath.toString()

                            val videoUri = FileProvider.getUriForFile(
                                context,
                                context.packageName + ".fileprovider",
                                File(videoPath)

                            )

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/*"
                                putExtra(Intent.EXTRA_STREAM, videoUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            }
                            val whatsappPackage = "com.whatsapp"
                            val businessWhatsAppPackage = "com.whatsapp.w4b"
                            val packageName =
                                SharedPrefUtils.getPrefString("package", "").toString()

                            if (packageName == businessWhatsAppPackage) {
                                if (context.packageManager.getLaunchIntentForPackage(packageName) != null) {
                                    shareIntent.setPackage(packageName)
                                    context.startActivity(shareIntent)

                                } else {

                                    Utils.appNotFound(context, "w4bw")
                                }
                            } else if (packageName == whatsappPackage) {
                                if (context.packageManager.getLaunchIntentForPackage(packageName) != null) {
                                    shareIntent.setPackage(packageName)
                                    context.startActivity(shareIntent)

                                } else {

                                    Utils.appNotFound(context, "ww")
                                }
                            }


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    } else if (mediaItem.type == MediaType.VIDEO) {
                        try {

                            val videoPath = mediaItem.filePath.toString()

                            val videoUri = FileProvider.getUriForFile(
                                context,
                                context.packageName + ".fileprovider",
                                File(videoPath)

                            )

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "video/*"
                                putExtra(Intent.EXTRA_STREAM, videoUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            }
                            val whatsappPackage = "com.whatsapp"
                            val businessWhatsAppPackage = "com.whatsapp.w4b"
                            val packageName =
                                SharedPrefUtils.getPrefString("package", "").toString()

                            if (packageName == businessWhatsAppPackage) {
                                if (context.packageManager.getLaunchIntentForPackage(packageName) != null) {
                                    shareIntent.setPackage(packageName)
                                    context.startActivity(shareIntent)

                                } else {
                                    Utils.appNotFound(context, "w4bw")
                                }
                            } else if (packageName == whatsappPackage) {
                                if (context.packageManager.getLaunchIntentForPackage(packageName) != null) {
                                    shareIntent.setPackage(packageName)
                                    context.startActivity(shareIntent)

                                } else {
                                    Utils.appNotFound(context, "ww")
                                }
                            }


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                statusShare.setOnClickListener {
                    if (mediaItem.type == MediaType.PHOTO) {
                        try {

                            val imagePath =
                                mediaItem.filePath.toString() // Replace with the actual path of your image file
//                            val imageUri = Uri.parse("file://$imagePath")
                            val imageUri = FileProvider.getUriForFile(
                                context,
                                context.packageName + ".fileprovider",
                                File(imagePath)
                            )

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/*"
                                putExtra(Intent.EXTRA_STREAM, imageUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }

                            context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                        } catch (e: Exception) {
                            Log.d("Share", e.localizedMessage.toString())
                        }

                    } else if (mediaItem.type == MediaType.VIDEO) {
                        try {
                            val videoPath =
                                mediaItem.filePath // Replace with the actual path of your video file

                            // Create a content URI using FileProvider
                            val videoUri = FileProvider.getUriForFile(
                                context,
                                context.packageName + ".fileprovider",
                                File(videoPath)
                            )

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "video/*"
                                putExtra(Intent.EXTRA_STREAM, videoUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }

                            context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
                        } catch (e: Exception) {
                            Log.d("Share", e.localizedMessage.toString())
                        }


                    }
                }
                cardSaved.setOnClickListener {
                    if (mediaItem.type == MediaType.PHOTO) {
                        // goto image preview activity

                        try {
                            Intent().apply {
                                putExtra("image", mediaItem.filePath.toString())
                                putExtra("i", "image")
                                setClass(context, SavedImagePreview::class.java)
                                context.startActivity(this)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
//                        context.startActivity(Intent(context,SavedImagePreview::class.java))

//                        savedImage.setImageURI(mediaItem.filePath.toUri())

                    } else {
                        // goto video preview activity
                        Intent().apply {
                            putExtra("video", mediaItem.filePath.toString())
                            putExtra("v", "video")
//                            putExtra(Constants.MEDIA_LIST_KEY,mediaList)
//                            putExtra(Constants.MEDIA_SCROLL_KEY,layoutPosition)
                            setClass(context, SavedVideoPreview::class.java)
                            context.startActivity(this)
                        }
                    }
                }
            }

        }
    }


    fun loadThumbnail(context: Context, mediaItem: SavedItem, imageView: ImageView) {
        when (mediaItem.type) {
            MediaType.PHOTO -> {
//                imageView.setImageURI(mediaItem.filePath.toUri())
                Glide.with(context)
                    .load(mediaItem.filePath)
                    .into(imageView)
//                Log.d("FilePath", "File path: ${mediaItem.filePath}")
            }

            MediaType.VIDEO -> {
//                Log.d("FilePath", "File path: ${mediaItem.filePath}")
//                imageView.setImageURI(mediaItem.filePath.toUri())
                val requestOptions = RequestOptions().frame(1000000)
                Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .asDrawable()
                    .load(mediaItem.filePath)
                    .into(imageView)

            }
        }
    }

}
