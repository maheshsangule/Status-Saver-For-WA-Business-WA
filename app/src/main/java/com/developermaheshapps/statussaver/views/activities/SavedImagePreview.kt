package com.developermaheshapps.statussaver.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.databinding.ActivitySavedImagepreviewBinding
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import java.io.File


class SavedImagePreview : AppCompatActivity() {


    private val binding by lazy {
        ActivitySavedImagepreviewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        binding.apply {
            tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
            tools.tvSave.text = "Saved"
            if (intent.getStringExtra("i").equals("image")) {
                toolBar.title = "Image"
            }
            toolBar.setNavigationOnClickListener {
                finish()

            }
            tools.llRepost.setOnClickListener {
                try {

                    val imagePath = intent.getStringExtra("image").toString()

                    val imageUri = FileProvider.getUriForFile(
                        this@SavedImagePreview,
                        packageName + ".fileprovider",
                        File(imagePath)

                    )

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
                        if (packageManager.getLaunchIntentForPackage(packageName) != null) {
                            shareIntent.setPackage(packageName)
                            startActivity(shareIntent)

                        } else {
                            Utils.appNotFound(this@SavedImagePreview ,"w4bw")
                        }
                    }

                    else  if(packageName==whatsappPackage)
                    {
                        if (packageManager.getLaunchIntentForPackage(packageName) != null) {
                            shareIntent.setPackage(packageName)
                            startActivity(shareIntent)

                        } else {
                            Utils.appNotFound(this@SavedImagePreview ,"ww")
                        }
                    }



                } catch (e: Exception) {
                 e.printStackTrace()
                }
            }
            tools.llShare.setOnClickListener {

                try {
                    val imagePath = intent.getStringExtra("image")
                        .toString() // Replace with the actual path of your image file
//                            val imageUri = Uri.parse("file://$imagePath")
                    Log.i("maheshking", "onCreate:$imagePath ")
                    val imageUri = FileProvider.getUriForFile(
                        this@SavedImagePreview,
                        packageName + ".fileprovider",
                        File(imagePath)
                    )

                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    startActivity(Intent.createChooser(shareIntent, "Share Image"))
                } catch (e: Exception) {
                    Log.d("Share", e.localizedMessage.toString())
                }


            }
//            val list = intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<SavedItem>
//            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
//            adapter = SavedImagePreviewAdapter(list, activity, scrollTo)
//            imagesViewPager.adapter = adapter
//            imagesViewPager.currentItem = scrollTo
            if (intent.getStringExtra("image") != null) {
                val receivedUriString = intent.getStringExtra("image")
//                val imageUri = Uri.parse(receivedUriString)
                if (receivedUriString != null) {
                    zoomableImageView.setImageURI(receivedUriString.toUri())
                }

//            imageview.setImageURI(intent.getStringArrayExtra("image").toUri())
//                Glide.with(activity)
//                    .load(imageUri)
//                    .into(imageview)
            }


//            Glide.with(activity)
//                .load(intent.getStringExtra("image")?.toUri())
//                .into(zoomableImageView)
        }
    }
}
