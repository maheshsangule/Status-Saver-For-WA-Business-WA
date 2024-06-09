package com.developermaheshapps.statussaver.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.databinding.ActivityImagesPreviewBinding
import com.developermaheshapps.statussaver.models.MediaModel
import com.developermaheshapps.statussaver.utils.Constants
import com.developermaheshapps.statussaver.views.adapters.ImagePreviewAdapter

class ImagesPreview : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityImagesPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: ImagePreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        binding.apply {


            if (intent.getStringExtra("i").equals("image")) {
                toolBar.title = "Image"
            }
            toolBar.setNavigationOnClickListener{
                finish()
            }
            val list =
                intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            adapter = ImagePreviewAdapter(list, activity)
            imagesViewPager.adapter = adapter
            imagesViewPager.currentItem = scrollTo
        }

    }
}