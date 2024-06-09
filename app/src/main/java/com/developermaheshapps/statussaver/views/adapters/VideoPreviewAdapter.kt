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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.databinding.ItemVideoPreviewBinding
import com.developermaheshapps.statussaver.models.MediaModel
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import com.developermaheshapps.statussaver.utils.saveStatus
import java.io.File

class VideoPreviewAdapter(val list: ArrayList<MediaModel>, val context: Activity) :
    RecyclerView.Adapter<VideoPreviewAdapter.ViewHolder>() {
    private var player: ExoPlayer? = null

    inner class ViewHolder(val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {

                player = ExoPlayer.Builder(context).build()
                playerView.player = player
                val mediaItem = MediaItem.fromUri(mediaModel.pathUri)

                player!!.setMediaItem(mediaItem)

                player!!.prepare()


                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)

                tools.llRepost.setOnClickListener {

                    val videoUri = Uri.parse(mediaModel.pathUri.toString())

                    // Share image using an Intent
                    try {

                        val videoUri =Uri.parse(mediaModel.pathUri.toString())
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "video/*"
                            putExtra(Intent.EXTRA_STREAM, videoUri)
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
                tools.llShare.setOnClickListener{
                    val videoUri = Uri.parse(mediaModel.pathUri.toString())
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "video/*"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri)

                    try {
                        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                    }
                    catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                }

                tools.download.setOnClickListener {
                    val isDownloaded = context.saveStatus(mediaModel)
                    if (isDownloaded) {
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


        fun releasePlayer(s:String)
        {
            if(s=="release")
            {
                binding.playerView.player?.stop()
                binding.playerView.player?.release()
            }
            else if(s=="stop")
            {
                binding.playerView.player?.stop()

            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoPreviewAdapter.ViewHolder {
        return ViewHolder(
            ItemVideoPreviewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoPreviewAdapter.ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size

}











