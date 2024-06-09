package com.developermaheshapps.statussaver.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.data.SavedItem
import com.developermaheshapps.statussaver.databinding.ActivitySavedVideosBinding
import com.developermaheshapps.statussaver.utils.Constants
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SavedVideoPreview : AppCompatActivity() {

    private val activity = this
    private val binding by lazy {
        ActivitySavedVideosBinding.inflate(layoutInflater)
    }
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        binding.apply {

            if(intent.getStringExtra("v").equals("video"))
            {
                toolBar.title="Video"
            }
            tools.llRepost.setOnClickListener {
//                try {
//
//                    val videoPath = intent.getStringExtra("video").toString()
//
//                    val videoUri = FileProvider.getUriForFile(
//                        this@SavedVideoPreview,
//                        packageName + ".fileprovider",
//                        File(videoPath)
//                    )
//
//                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                        type ="video/*"
//                        putExtra(Intent.EXTRA_STREAM, videoUri)
//                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                        `package` = SharedPrefUtils.getPrefString("package", "")
//                            .toString() // Specify WhatsApp package
//                    }
//
//                    startActivity(shareIntent)
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }

                try {

                    val videoPath = intent.getStringExtra("video").toString()

                    val videoUri = FileProvider.getUriForFile(
                        this@SavedVideoPreview,
                        packageName + ".fileprovider",
                        File(videoPath)

                    )

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
                        if (packageManager.getLaunchIntentForPackage(packageName) != null) {
                            shareIntent.setPackage(packageName)
                            startActivity(shareIntent)

                        } else {
                            Utils.appNotFound(this@SavedVideoPreview ,"w4bw")
                        }
                    }

                    else  if(packageName==whatsappPackage)
                    {
                        if (packageManager.getLaunchIntentForPackage(packageName) != null) {
                            shareIntent.setPackage(packageName)
                            startActivity(shareIntent)

                        } else {
                            Utils.appNotFound(this@SavedVideoPreview ,"ww")
                        }
                    }



                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            tools.llShare.setOnClickListener {

                try {
                    val videoPath = intent.getStringExtra("video").toString() // Replace with the actual path of your image file
//                            val imageUri = Uri.parse("file://$videoPath")
                    val videoUri = FileProvider.getUriForFile(
                        this@SavedVideoPreview,
                        packageName + ".fileprovider",
                        File(videoPath)
                    )

                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type ="video/*"
                        putExtra(Intent.EXTRA_STREAM, videoUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    startActivity(Intent.createChooser(shareIntent, "Share Image"))
                } catch (e: Exception) {
                    Log.d("Share", e.localizedMessage.toString())
                }


            }
            tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
            tools.tvSave.text="Saved"
            toolBar.setNavigationOnClickListener{
                finish()
                stopAllPlayers()
            }
            player = ExoPlayer.Builder(activity).build()
            playerView.player = player
            val mediaItem = MediaItem.fromUri(intent.getStringExtra("video").toString())

            player!!.setMediaItem(mediaItem)

            player!!.prepare()
            player!!.play()



        }


    }
    private fun stopAllPlayers() {

                binding.apply {

                    player?.stop()
                    player!!.release()

        }
    }

    override fun onPause() {
        super.onPause()
        player?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        stopAllPlayers()
    }
}

