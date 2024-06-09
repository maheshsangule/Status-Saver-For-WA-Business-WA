package com.developermaheshapps.statussaver.views.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.databinding.ActivityVideosPreviewBinding
import com.developermaheshapps.statussaver.models.MediaModel
import com.developermaheshapps.statussaver.utils.Constants
import com.developermaheshapps.statussaver.views.adapters.VideoPreviewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideosPreview : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityVideosPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: VideoPreviewAdapter
    private val TAG = "VideosPreview"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        binding.apply {


            if (intent.getStringExtra("v").equals("video")) {
                toolBar.title = "Video"
            }
            toolBar.setNavigationOnClickListener{
                finish()
                stopAllPlayers("release")
            }
            val list =
                intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            adapter = VideoPreviewAdapter(list, activity)
            videoRecyclerView.adapter = adapter
            val pageSnapHelper = PagerSnapHelper()
            pageSnapHelper.attachToRecyclerView(videoRecyclerView)
            videoRecyclerView.scrollToPosition(scrollTo)

            videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        Log.d(TAG, "onScrollStateChanged: Dragging")
                        stopAllPlayers("stop")
                    }
                }


            })


        }


    }

    private fun stopAllPlayers(s:String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.apply {
                    for (i in 0 until videoRecyclerView.childCount) {
                        val child = videoRecyclerView.getChildAt(i)
                        val viewHolder = videoRecyclerView.getChildViewHolder(child)
                        if (viewHolder is VideoPreviewAdapter.ViewHolder) {

                            viewHolder.releasePlayer(s.toString())


                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopAllPlayers("stop")

    }

    override fun onDestroy() {
        super.onDestroy()
        stopAllPlayers("release")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        stopAllPlayers("release")
    }

}
















