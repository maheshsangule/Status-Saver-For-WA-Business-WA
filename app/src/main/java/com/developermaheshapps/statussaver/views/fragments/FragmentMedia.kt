package com.developermaheshapps.statussaver.views.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.data.StatusRepo
import com.developermaheshapps.statussaver.databinding.FragmentMediaBinding
import com.developermaheshapps.statussaver.models.MediaModel
import com.developermaheshapps.statussaver.utils.Constants
import com.developermaheshapps.statussaver.utils.SharedPrefKeys
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import com.developermaheshapps.statussaver.viewmodels.StatusViewModel
import com.developermaheshapps.statussaver.viewmodels.factories.StatusViewModelFactory
import com.developermaheshapps.statussaver.views.adapters.MediaAdapter
import java.util.Collections


class FragmentMedia : Fragment() {
    private val binding by lazy {
        FragmentMediaBinding.inflate(layoutInflater)
    }
    var pbDialog: Dialog? = null
    lateinit var viewModel: StatusViewModel
    lateinit var adapter: MediaAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            arguments?.let {
                val repo = StatusRepo(requireActivity())
                viewModel = ViewModelProvider(
                    requireActivity(),
                    StatusViewModelFactory(repo)
                )[StatusViewModel::class.java]
                val isBusinessPermissionGranted = SharedPrefUtils.getPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                    false
                )

                val isWhatsAppPermissionGranted = SharedPrefUtils.getPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                    false
                )

                val mediaType = it.getString(Constants.MEDIA_TYPE_KEY, "")
                when (mediaType) {
                    Constants.MEDIA_TYPE_WHATSAPP_IMAGES -> {
                        binding.animView.visibility = View.VISIBLE
                        if(!isWhatsAppPermissionGranted)
                        {
                            binding.animView.visibility = View.GONE
                        }
                        viewModel.whatsAppImagesLiveData.observe(requireActivity()) { unFilteredList ->
                            val filteredList = unFilteredList.distinctBy { model ->
                                model.fileName
                            }

                            val list = ArrayList<MediaModel>()
                            filteredList.forEach { model ->
                                binding.animView.visibility = View.GONE
                                list.add(model)
                            }
                            adapter = MediaAdapter(list, requireActivity())
                            mediaRecyclerView.adapter = adapter
                            if (list.size == 0) {
                                binding.animView.visibility = View.GONE
                                tempMediaText.visibility = View.VISIBLE
                                noDataFound.tvNoFileSaved.text = "No Data found"
                                noDataFound.layoutHowToUse.setOnClickListener {
                                    Utils.guideDialog(requireActivity())
                                }
                            } else {
                                binding.animView.visibility = View.GONE
                                tempMediaText.visibility = View.GONE
                            }

                        }
                    }

                    Constants.MEDIA_TYPE_WHATSAPP_VIDEOS -> {

                            binding.animView.visibility = View.VISIBLE
                        if(!isWhatsAppPermissionGranted)
                        {
                            binding.animView.visibility = View.GONE
                        }


                        viewModel.whatsAppVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            val filteredList = unFilteredList.distinctBy { model ->
                                model.fileName
                            }

                            val list = ArrayList<MediaModel>()
                            filteredList.forEach { model ->
                                binding.animView.visibility = View.GONE

                                list.add(model)
                            }

                            if (list.size == 0) {
                                binding.animView.visibility = View.GONE
                                tempMediaText.visibility = View.VISIBLE
                                noDataFound.tvNoFileSaved.text = "No Data found"
                                noDataFound.layoutHowToUse.setOnClickListener {
                                    binding.animView.visibility = View.GONE
                                    if (binding.noDataFound.layoutHowToUse.isVisible) {
                                        binding.animView.visibility = View.GONE
                                        Utils.guideDialog(requireActivity())


                                    } else {
                                        binding.noDataFound.layoutHowToUse.visibility = View.VISIBLE
                                        binding.animView.visibility = View.GONE
                                        Utils.guideDialog(requireActivity())


                                    }
                                }
                            } else {
                                binding.animView.visibility = View.GONE

                                adapter = MediaAdapter(list, requireActivity())
                                mediaRecyclerView.adapter = adapter
                            }
                        }
                    }

                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES -> {
                        binding.animView.visibility = View.VISIBLE
                        if(!isBusinessPermissionGranted)
                        {
                            binding.animView.visibility = View.GONE
                        }
                        viewModel.whatsAppBusinessImagesLiveData.observe(requireActivity()) { unFilteredList ->
                            val filteredList = unFilteredList.distinctBy { model ->
                                model.fileName
                            }

                            val list = ArrayList<MediaModel>()
                            val list1 = ArrayList<MediaModel>()
                            filteredList.forEach { model ->
                                binding.animView.visibility = View.GONE
                                list1.add(model)
                            }
                            Collections.reverse(list1)
                            list.addAll(filteredList.sortedByDescending { it.creationDate })


                            if (list.size == 0) {
                                binding.animView.visibility = View.GONE
                                tempMediaText.visibility = View.VISIBLE
                                noDataFound.tvNoFileSaved.text = "No Data found"
                                noDataFound.layoutHowToUse.setOnClickListener {

                                    if (binding.noDataFound.layoutHowToUse.isVisible) {
                                        binding.animView.visibility = View.GONE

                                        Utils.guideDialog(requireActivity())
                                    } else {
                                        binding.animView.visibility = View.GONE
                                        binding.noDataFound.layoutHowToUse.visibility = View.VISIBLE
                                        Utils.guideDialog(requireActivity())
                                    }
                                }
                            } else {
                                binding.animView.visibility = View.GONE
                                adapter = MediaAdapter(list, requireActivity())
                                mediaRecyclerView.adapter = adapter
                            }
                        }
                    }

                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS -> {
                        binding.animView.visibility = View.VISIBLE
                        if(!isBusinessPermissionGranted)
                        {
                            binding.animView.visibility = View.GONE
                        }
                        viewModel.whatsAppBusinessVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            val filteredList = unFilteredList.distinctBy { model ->
                                model.fileName
                            }

                            val list = ArrayList<MediaModel>()
                            val list1 = ArrayList<MediaModel>()
                            filteredList.forEach { model ->
                                binding.animView.visibility = View.GONE

                                list1.add(model)
                            }
//                            Collections.reverse(list1)
                            list.addAll(filteredList.sortedByDescending { it.creationDate })

                            adapter = MediaAdapter(list, requireActivity())
                            mediaRecyclerView.adapter = adapter
                            if (list.size == 0) {
                                binding.animView.visibility = View.GONE
                                tempMediaText.visibility = View.VISIBLE
                                noDataFound.tvNoFileSaved.text = "No Data found"
                                noDataFound.layoutHowToUse.setOnClickListener {

                                    if (binding.noDataFound.layoutHowToUse.isVisible) {
                                        binding.animView.visibility = View.GONE

                                        Utils.guideDialog(requireActivity())
                                    } else {
                                        binding.animView.visibility = View.GONE
                                        binding.noDataFound.layoutHowToUse.visibility = View.VISIBLE
                                        Utils.guideDialog(requireActivity())
                                    }
                                }
                            } else {
                                binding.animView.visibility = View.GONE
                                tempMediaText.visibility = View.GONE
                            }
                        }
                    }
                }


            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root


}