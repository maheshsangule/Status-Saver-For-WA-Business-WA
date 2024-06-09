package com.developermaheshapps.statussaver.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.data.MediaType
import com.developermaheshapps.statussaver.data.SavedItem
import com.developermaheshapps.statussaver.databinding.FragmentSettingsBinding
import com.developermaheshapps.statussaver.views.adapters.SavedAdapter
import java.io.File

class FragmentSettings : Fragment() {
    private val binding by lazy {
        FragmentSettingsBinding.inflate(layoutInflater)
    }
    private lateinit var savedAdapter: SavedAdapter

    //    private lateinit var recyclerView: RecyclerView
    //    val requireContext()=requireActivity()
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        binding.swipeRefreshLayout.setOnRefreshListener {

            Handler(Looper.myLooper()!!).postDelayed({
                binding.swipeRefreshLayout.isRefreshing = false
            }, 2000)
        }
//        recyclerView = requireActivity().findViewById(R.id.downloaded_recycler_view)
        binding.noDataFound.layoutHowToUse.setOnClickListener {

            if (binding.noDataFound.layoutHowToUse.isVisible) {


                Utils.guideDialog(requireActivity())
            } else {
                binding.noDataFound.layoutHowToUse.visibility = View.VISIBLE
                Utils.guideDialog(requireActivity())
            }
        }
        val folderPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File("${folderPath}/${getString(R.string.folder_path)}")
        val mediaList = getMediaFiles(file)

        if (mediaList.isEmpty()) {
            binding.tempMediaText.visibility = View.VISIBLE
        } else {

            binding.tempMediaText.visibility = View.GONE
            setupRecyclerView(mediaList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    private fun setupRecyclerView(mediaList: ArrayList<SavedItem>) {
        try {
            savedAdapter = SavedAdapter(requireActivity(), mediaList)
//        val recyclerView: RecyclerView = requireActivity().findViewById(R.id.downloaded_recycler_view)
//        recyclerView.layoutManager = GridLayoutManager(this, 3)
            binding.downloadedRecyclerView.adapter = savedAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getMediaFiles(directory: File): ArrayList<SavedItem> {
        val mediaItems = mutableListOf<SavedItem>()
        val files = directory.listFiles()

        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    mediaItems.addAll(getMediaFiles(file))
                } else {
                    if (isVideoFile(file)) {
                        mediaItems.add(SavedItem(file.absolutePath, MediaType.VIDEO))
                    } else if (isImageFile(file)) {
                        mediaItems.add(SavedItem(file.absolutePath, MediaType.PHOTO))
                    }
                }
            }
        }

        // Reverse the order of mediaItems
        mediaItems.reverse()

        return mediaItems as ArrayList<SavedItem>
    }


    private fun isVideoFile(file: File): Boolean {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            MimeTypeMap.getFileExtensionFromUrl(file.path)
        )
        return mimeType?.startsWith("video") == true
    }

    private fun isImageFile(file: File): Boolean {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            MimeTypeMap.getFileExtensionFromUrl(file.path)
        )
        return mimeType?.startsWith("image") == true
    }
}