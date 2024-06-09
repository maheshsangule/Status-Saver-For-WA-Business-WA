package com.developermaheshapps.statussaver.views.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.data.StatusRepo
import com.developermaheshapps.statussaver.databinding.FragmentStatusBinding
import com.developermaheshapps.statussaver.utils.Constants
import com.developermaheshapps.statussaver.utils.SharedPrefKeys
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import com.developermaheshapps.statussaver.utils.getFolderPermissions
import com.developermaheshapps.statussaver.viewmodels.StatusViewModel
import com.developermaheshapps.statussaver.viewmodels.factories.StatusViewModelFactory
import com.developermaheshapps.statussaver.views.adapters.MediaViewPagerAdapter
import com.google.android.material.color.MaterialColors.getColor
import com.google.android.material.tabs.TabLayoutMediator

class FragmentStatus : Fragment() {
    private val binding by lazy {
        FragmentStatusBinding.inflate(layoutInflater)
    }
    private lateinit var type: String
    private val WHATSAPP_REQUEST_CODE = 101
    private val WHATSAPP_BUSINESS_REQUEST_CODE = 102

    private val viewPagerTitles = arrayListOf("Images", "Videos")
    lateinit var viewModel: StatusViewModel
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary));

            arguments?.let {
                val repo = StatusRepo(requireActivity())
                viewModel = ViewModelProvider(
                    requireActivity(),
                    StatusViewModelFactory(repo)
                )[StatusViewModel::class.java]
                binding.swipeRefreshLayout.setOnRefreshListener {
                    Handler(Looper.myLooper()!!).postDelayed({
                        binding.swipeRefreshLayout.isRefreshing = false
                    }, 2000)
                }
                type = it.getString(Constants.FRAGMENT_TYPE_KEY, "")

                when (type) {
                    Constants.TYPE_WHATSAPP_MAIN -> {
                        // check permission
                        // granted then fetch statuses
                        // get permission
                        // fetch statuses
                        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                            false
                        )
                        if (isPermissionGranted) {
                            getWhatsAppStatuses()

                            binding.swipeRefreshLayout.setOnRefreshListener {
                                refreshStatuses()
                            }

                        }
                        permissionLayout.btnPermission.setOnClickListener {
                            val whatsappPackage = "com.whatsapp"
                            val intent= requireActivity().packageManager.getLaunchIntentForPackage(whatsappPackage)
                            try {

                                if(intent!=null)
                                {
                                    getFolderPermissions(
                                        context = requireActivity(),
                                        REQUEST_CODE = WHATSAPP_REQUEST_CODE,
                                        initialUri = Constants.getWhatsappUri()
                                    )
//                                    startActivity(intent)
                                }
                                else
                                {
                                    Utils.appNotFound(requireActivity(),"w")
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()

                            }


//                            val dialog = Dialog(requireContext())
//                            dialog.setContentView(R.layout.activity_instructions)
//                            val dialogBinding =
//                                DialogGuideBinding.inflate((activity as Activity).layoutInflater)
////                            dialogBinding.okayBtn.setOnClickListener {
////                                dialog.dismiss()
////                            }
//                            val tv= requireActivity().findViewById<TextView>(R.id.btn_use_this_folder)
//                            tv.setOnClickListener{
//                                dialog.dismiss()
//                            }
//
//
//                            dialog.window?.setLayout(
//                                ActionBar.LayoutParams.MATCH_PARENT,
//                                ActionBar.LayoutParams.MATCH_PARENT
//                            )
//
//                            dialog.show()
                        }


                        val viewPagerAdapter = MediaViewPagerAdapter(requireActivity())
                        statusViewPager.adapter = viewPagerAdapter
                        TabLayoutMediator(tabLayout, statusViewPager) { tab, pos ->
                            tab.text = viewPagerTitles[pos]
                        }.attach()

                    }

                    Constants.TYPE_WHATSAPP_BUSINESS -> {
                        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                            false
                        )
                        if (isPermissionGranted) {
                            getWhatsAppBusinessStatuses()

                            binding.swipeRefreshLayout.setOnRefreshListener {
                                refreshStatuses()
                            }

                        }
                        permissionLayout.btnPermission.setOnClickListener {

                            val whatsappBusinessPackage = "com.whatsapp.w4b"
                            val intent= requireActivity().packageManager.getLaunchIntentForPackage(whatsappBusinessPackage)
                            try {

                                if(intent!=null)
                                {
                                    getFolderPermissions(
                                        context = requireActivity(),
                                        REQUEST_CODE = WHATSAPP_BUSINESS_REQUEST_CODE,
                                        initialUri = Constants.getWhatsappBusinessUri()
                                    )
//                                    startActivity(intent)
                                }
                                else
                                {
                                    Utils.appNotFound(requireActivity(),"w4b")
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()

                            }

                        }
                        val viewPagerAdapter = MediaViewPagerAdapter(
                            requireActivity(),
                            imagesType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES,
                            videosType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS
                        )
                        statusViewPager.adapter = viewPagerAdapter
                        TabLayoutMediator(tabLayout, statusViewPager) { tab, pos ->
                            tab.text = viewPagerTitles[pos]
                        }.attach()
                    }
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    fun refreshStatuses() {
        when (type) {
            Constants.TYPE_WHATSAPP_MAIN -> {
                Toast.makeText(requireActivity(), "Refreshing WP Statuses", Toast.LENGTH_SHORT)
                    .show()
                getWhatsAppStatuses()
            }

            else -> {
                Toast.makeText(
                    requireActivity(),
                    "Refreshing WP Business Statuses",
                    Toast.LENGTH_SHORT
                ).show()
                getWhatsAppBusinessStatuses()
            }
        }

        Handler(Looper.myLooper()!!).postDelayed({
            binding.swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }

    fun getWhatsAppStatuses() {
        // function to get wp statuses

        binding.permissionLayoutHolder.visibility = View.GONE
        viewModel.getWhatsAppStatuses()
    }

    private val TAG = "FragmentStatus"
    fun getWhatsAppBusinessStatuses() {


        // function to get wp statuses
        binding.permissionLayoutHolder.visibility = View.GONE
        Log.d(TAG, "getWhatsAppBusinessStatuses: Getting Wp Business Statuses")
        viewModel.getWhatsAppBusinessStatuses()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            val treeUri = data?.data!!
            requireActivity().contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            if (requestCode == WHATSAPP_REQUEST_CODE) {
                // whatsapp logic here
                SharedPrefUtils.putPrefString(
                    SharedPrefKeys.PREF_KEY_WP_TREE_URI,
                    treeUri.toString()
                )
                SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, true)
                getWhatsAppStatuses()
            } else if (requestCode == WHATSAPP_BUSINESS_REQUEST_CODE) {
                // whatsapp business logic here
                SharedPrefUtils.putPrefString(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI,
                    treeUri.toString()
                )
                SharedPrefUtils.putPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                    true
                )
                getWhatsAppBusinessStatuses()
            }
        }



    }

//    val callback = context.onBackPressedDispatcher.addCallback(context) {
//        Toast.makeText(context, "Are you sure want to exit App", Toast.LENGTH_SHORT).show()
//    }

}















