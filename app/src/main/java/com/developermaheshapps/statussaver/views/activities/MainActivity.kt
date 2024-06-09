package com.developermaheshapps.statussaver.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModelProvider
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.data.StatusRepo
import com.developermaheshapps.statussaver.databinding.ActivityMainBinding
import com.developermaheshapps.statussaver.models.SettingsModel
import com.developermaheshapps.statussaver.utils.Constants
import com.developermaheshapps.statussaver.utils.SharedPrefKeys
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import com.developermaheshapps.statussaver.utils.replaceFragment
import com.developermaheshapps.statussaver.viewmodels.StatusViewModel
import com.developermaheshapps.statussaver.viewmodels.factories.StatusViewModelFactory
import com.developermaheshapps.statussaver.views.fragments.FragmentSettings
import com.developermaheshapps.statussaver.views.fragments.FragmentStatus
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var viewModel: StatusViewModel
    private val list = ArrayList<SettingsModel>()

    private lateinit var toolbar: MaterialToolbar
    private var menuItem: MenuItem? = null
    private val PREFS_NAME = "MyPrefs"
    private val NOTIFICATION_PREF_KEY = "notificationEnabled"
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
            false
        ) || SharedPrefUtils.getPrefBoolean(
            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
            false
        )
        if (isPermissionGranted) {
            refreshVideo(this@MainActivity)

            val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isNotificationEnabled: Boolean = prefs.getBoolean(NOTIFICATION_PREF_KEY, true)

            if (isNotificationEnabled) {


                createNotification(
                    this@MainActivity,
                    "Notify new status",
                    "You will be notified on new statuses available"
                )
            } else {
                // Cancel notifications
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }


        }
        Utils.check_internet(this)
        toolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)

        SharedPrefUtils.init(activity)
        binding.apply {
            val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isNotificationEnabled: Boolean = prefs.getBoolean(NOTIFICATION_PREF_KEY, true)
            if(isNotificationEnabled)
            {
                msbarNotification.isChecked=true
                msbarNotification.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_notification,  // Left
                    0,  // Top
                    0,  // Right
                    0   // Bottom
                )
                msbarNotification.text="On"
            }
            else{
                msbarNotification.isChecked=false
                msbarNotification.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_notification_off,  // Left
                    0,  // Top
                    0,  // Right
                    0   // Bottom
                )
                msbarNotification.text="Off"
            }

            msbarNotification.isChecked=isNotificationEnabled
            msbarNotification.setOnCheckedChangeListener{_,isChecked->
                if(isChecked)
                {
                    msbarNotification.isChecked=true
                    msbarNotification.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_notification,  // Left
                        0,  // Top
                        0,  // Right  2Q
                        0   // Bottom
                    )
                    msbarNotification.text="On"
                }
                else
                {
                    msbarNotification.isChecked=false
                    msbarNotification.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_notification_off,  // Left
                        0,  // Top
                        0,  // Right
                        0   // Bottom
                    )
                    msbarNotification.text="Off"
                }
                prefs.edit().putBoolean(NOTIFICATION_PREF_KEY, isChecked).apply()

            }
            tvDisclaimar.setOnClickListener{
                MaterialAlertDialogBuilder(
                    this@MainActivity,
                    R.style.CustomAlertDialogStyle
                ).apply {
                    setTitle("Disclaimar")
                    setMessage("\"Whatsapp\" and the \"Whatsapp\" name are copyrighted to WhatsApp, Inc. ©⚠" +
                            "This status video downloader app is not affiliated with, sponsored by, or endorsed by WhatsApp, Inc. \uD83D\uDEAB\uD83D\uDD04\uD83E\uDD1D" +
                            "Any use of downloaded content by the user is not the responsibility of this app. \uD83D\uDCE4\uD83D\uDEAB⚠\n")
                    setPositiveButton("OK", null)
                    show()
                }
            }
            val whatsAppType = SharedPrefUtils.getPrefString("package", "").toString()
            ibHelp.setOnClickListener {
                MaterialAlertDialogBuilder(
                    this@MainActivity,
                    R.style.CustomAlertDialogStyle
                ).apply {
                    setTitle("Help")

                    if (SharedPrefUtils.getPrefString("main", "").toString().equals("w")) {
                        setMessage("WhatsApp is set As default option for reposting and Sharing Statuses and WhatsApp Statuses section will open first")
                    } else if (SharedPrefUtils.getPrefString("main", "").toString().equals("w4b")) {
                        setMessage("Business WhatsApp is set As default option for reposting and Sharing Statuses and Business Statuses section will open first")
                    } else if (SharedPrefUtils.getPrefString("main", "").toString().isEmpty()) {
                        if (whatsAppType == "com.whatsapp") {
                            setMessage("WhatsApp is set As default option for reposting and Sharing Statuses and WhatsApp Statuses section will open first")
                        } else if (whatsAppType == "com.whatsapp.w4b") {
                            setMessage("Business WhatsApp is set As default option for reposting and Sharing Statuses and Business Statuses section will open first")
                        }
                    }
                    setPositiveButton("OK", null)
                    show()
                }
            }
            if (SharedPrefUtils.getPrefString("main", "").toString().equals("w")) {
                msWhatsappType.isChecked = true
                SharedPrefUtils.putPrefString("package", "com.whatsapp")
                msWhatsappType.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_whatsapp1,  // Left
                    0,  // Top
                    0,  // Right
                    0   // Bottom
                )

                msWhatsappType.text = "WhatsApp"
            } else if (SharedPrefUtils.getPrefString("main", "").toString().equals("w4b")) {
                msWhatsappType.isChecked = false
                SharedPrefUtils.putPrefString("package", "com.whatsapp.w4b")
                msWhatsappType.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_whatsapp_buisness1,  // Left
                    0,  // Top
                    0,  // Right
                    0   // Bottom
                )

                msWhatsappType.text = "B WhatsApp"

            } else if (SharedPrefUtils.getPrefString("main", "").toString().isEmpty()) {
                if (whatsAppType == "com.whatsapp") {
                    msWhatsappType.isChecked = true
                    msWhatsappType.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_whatsapp1,  // Left
                        0,  // Top
                        0,  // Right
                        0   // Bottom
                    )

                    msWhatsappType.text = "WhatsApp"
                } else if (whatsAppType == "com.whatsapp.w4b") {
                    msWhatsappType.isChecked = false
                    msWhatsappType.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_whatsapp_buisness1,  // Left
                        0,  // Top
                        0,  // Right
                        0   // Bottom
                    )

                    msWhatsappType.text = "B WhatsApp"
                }
            }
            msWhatsappType.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    SharedPrefUtils.putPrefString("package", "com.whatsapp")
                    msWhatsappType.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_whatsapp1,  // Left
                        0,  // Top
                        0,  // Right
                        0   // Bottom
                    )
                    msWhatsappType.text = "WhatsApp"
                    SharedPrefUtils.putPrefString("main", "w")


                } else if (!isChecked) {
                    SharedPrefUtils.putPrefString("package", "com.whatsapp.w4b")
                    msWhatsappType.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_whatsapp_buisness1,  // Left
                        0,  // Top
                        0,  // Right
                        0   // Bottom
                    )
                    msWhatsappType.text = "B WhatsApp"
                    SharedPrefUtils.putPrefString("main", "w4b")
                }
            }
            requestPermission()
            toolBar.setNavigationOnClickListener {
                drawerLayout.open()
            }
            tvHowToUse.setOnClickListener {
                Utils.guideDialog(this@MainActivity)
//                val dialog = Dialog(activity)
//                val dialogBinding =
//                    DialogGuideBinding.inflate((activity as Activity).layoutInflater)
//                dialogBinding.okayBtn.setOnClickListener {
//                    dialog.dismiss()
//                }
//                dialog.setContentView(dialogBinding.root)
//
//                dialog.window?.setLayout(
//                    ActionBar.LayoutParams.MATCH_PARENT,
//                    ActionBar.LayoutParams.WRAP_CONTENT
//                )
//
//                dialog.show()
            }
            tvShare.setOnClickListener {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name))
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "My App is soo cool please download it :https://play.google.com/store/apps/details?id=${activity.packageName}"
                    )
                    activity.startActivity(this)
                }
            }
            tvRateUs.setOnClickListener {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + activity.packageName)
                ).apply {
                    activity.startActivity(this)
                }
            }
            tvPrivacyPolicy.setOnClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/status--saver/home/privacy-policy")).apply {
                    activity.startActivity(this)
                }
            }
            tvVersionCode.text = "Version:" + PackageInfoCompat.getLongVersionCode(
                activity.packageManager.getPackageInfo(
                    activity.packageName,
                    0
                )
            ) + ".0"
            toolBar.setOnMenuItemClickListener {
                when (it.itemId) {

                    R.id.whatsapp_type -> {
                        val whatsappPackage = "com.whatsapp"
                        val businessWhatsAppPackage = "com.whatsapp.w4b"
                        val status = SharedPrefUtils.getPrefString("whatsapp", "").toString()

                        if (status.equals("whatsapp")) {
                            val intent = packageManager.getLaunchIntentForPackage(whatsappPackage)
                            try {

                                if (intent != null) {
                                    startActivity(intent)
                                } else {
                                    Utils.appNotFound(this@MainActivity, "w")
//                                    Toast.makeText(this@MainActivity, "App not found", Toast.LENGTH_SHORT).show()
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        } else if (status.equals("bwhatsapp")) {
                            val intent =
                                packageManager.getLaunchIntentForPackage(businessWhatsAppPackage)
                            try {

                                if (intent != null) {
                                    startActivity(intent)
                                } else {
                                    Utils.appNotFound(this@MainActivity, "w4b")
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }

                    }

                    R.id.ic_person -> {
                        Utils.devData(this@MainActivity)
                    }

                    R.id.send_msg -> {
                        Utils.sendMessage(this@MainActivity)
                    }
                }

                return@setOnMenuItemClickListener true
            }

            Handler(Looper.getMainLooper()).postDelayed({
                menuItem?.setIcon(R.drawable.ic_whatsapp)
            }, 100)


            if (SharedPrefUtils.getPrefString("main", "").toString().equals("w")) {
                try {


                    SharedPrefUtils.putPrefInt("app_type_icon", R.drawable.ic_whatsapp)
                    SharedPrefUtils.putPrefString("whatsapp", "whatsapp")
                    SharedPrefUtils.putPrefString("package", "com.whatsapp")
                    bottomNavigationView.selectedItemId = R.id.menu_status
                    val fragmentWhatsAppStatus = FragmentStatus()
                    val bundle = Bundle()
                    bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                    replaceFragment(fragmentWhatsAppStatus, bundle)


                } catch (e: Exception) {
                    e.printStackTrace()

                }
            } else if (SharedPrefUtils.getPrefString("main", "").toString().equals("w4b")) {
                try {
                    SharedPrefUtils.putPrefInt(
                        "app_type_icon",
                        R.drawable.ic_whatsapp_buisness
                    )
                    SharedPrefUtils.putPrefString("package", "com.whatsapp.w4b")

                    bottomNavigationView.selectedItemId = R.id.menu_business_status
                    val fragmentWhatsAppStatus = FragmentStatus()
                    val bundle = Bundle()
                    bundle.putString(
                        Constants.FRAGMENT_TYPE_KEY,
                        Constants.TYPE_WHATSAPP_BUSINESS
                    )
                    replaceFragment(fragmentWhatsAppStatus, bundle)
                } catch (e: Exception) {
                    e.printStackTrace()

                }
            } else if (SharedPrefUtils.getPrefString("main", "").toString().isEmpty()) {
                if (whatsAppType == "com.whatsapp") {
                    try {

                        SharedPrefUtils.putPrefInt(
                            "app_type_icon",
                            R.drawable.ic_whatsapp
                        )
                        bottomNavigationView.selectedItemId = R.id.menu_status
                        val fragmentWhatsAppStatus = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                        replaceFragment(fragmentWhatsAppStatus, bundle)


                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                } else if (whatsAppType == "com.whatsapp.w4b") {
                    try {
                        SharedPrefUtils.putPrefInt(
                            "app_type_icon",
                            R.drawable.ic_whatsapp_buisness
                        )
                        bottomNavigationView.selectedItemId = R.id.menu_business_status
                        val fragmentWhatsAppStatus = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.FRAGMENT_TYPE_KEY,
                            Constants.TYPE_WHATSAPP_BUSINESS
                        )
                        replaceFragment(fragmentWhatsAppStatus, bundle)
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                }
            }
            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {


                    R.id.menu_status -> {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            menuItem?.setIcon(R.drawable.ic_whatsapp)
//                        }, 100) // 1000 milliseconds (1 second) delay

                        SharedPrefUtils.putPrefInt("app_type_icon", R.drawable.ic_whatsapp)
                        SharedPrefUtils.putPrefString("whatsapp", "whatsapp")
                        val fragmentWhatsAppStatus = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                        replaceFragment(fragmentWhatsAppStatus, bundle)

                    }

                    R.id.menu_business_status -> {
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            menuItem?.setIcon(R.drawable.ic_whatsapp_buisness)
//                        }, 100) // 1000 milliseconds (1 second) delay
                        SharedPrefUtils.putPrefInt("app_type_icon", R.drawable.ic_whatsapp_buisness)
                        SharedPrefUtils.putPrefString("whatsapp", "bwhatsapp")
                        val fragmentWhatsAppStatus = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.FRAGMENT_TYPE_KEY,
                            Constants.TYPE_WHATSAPP_BUSINESS
                        )
                        replaceFragment(fragmentWhatsAppStatus, bundle)
                    }

                    R.id.menu_settings -> {
                        val fragmentWhatsAppStatus = FragmentSettings()
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.FRAGMENT_TYPE_KEY,
                            Constants.TYPE_SAVED_VIDEOS
                        )
                        replaceFragment(fragmentWhatsAppStatus, bundle)

                    }
                }
                return@setOnItemSelectedListener true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        menuItem = menu.findItem(R.id.whatsapp_type)
        Handler(Looper.getMainLooper()).postDelayed({
            menuItem?.setIcon(SharedPrefUtils.getPrefInt("app_type_icon", 0))
        }, 100) // 1000 milliseconds (1 second) delay


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.whatsapp_type -> {
                // Handle the menu item click
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private val PERMISSION_REQUEST_CODE = 50
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val isPermissionsGranted = SharedPrefUtils.getPrefBoolean(
                SharedPrefKeys.PREF_KEY_IS_PERMISSIONS_GRANTED,
                false
            )

            if (!isPermissionsGranted) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
                Toast.makeText(activity, "Please Grant Permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val isGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (isGranted) {
                SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_IS_PERMISSIONS_GRANTED, true)
            } else {
                SharedPrefUtils.putPrefBoolean(
                    SharedPrefKeys.PREF_KEY_IS_PERMISSIONS_GRANTED,
                    false
                )

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager?.findFragmentById(R.id.fragment_container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        refreshVideo(this@MainActivity)
    }

    override fun onRestart() {
        super.onRestart()
        refreshVideo(this@MainActivity)
    }

    fun refreshVideo(context: Activity) {
        val repo = StatusRepo(this)
        viewModel = ViewModelProvider(
            this,
            StatusViewModelFactory(repo)
        )[StatusViewModel::class.java]

        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
            false
        )
        if (isPermissionGranted) {
            viewModel.getWhatsAppStatuses()
            viewModel.getWhatsAppBusinessStatuses()
        }
    }


    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        var sheetDialog: BottomSheetDialog? = null
        sheetDialog = BottomSheetDialog(this, R.style.BottomSheetStyle)
        val inflater = layoutInflater
        val view =
            inflater.inflate(R.layout.quiz_onback, findViewById(R.id.quiz_onback) as LinearLayout?)

        val btnQuit = view.findViewById<MaterialButton>(R.id.btn_quit)
        val btnContinue = view.findViewById<MaterialButton>(R.id.btn_continue)
        val tvText = view.findViewById<TextView>(R.id.tv_text)

        tvText.text = "Are you sure you want to exit the app?"
        btnQuit.text = "Yes"
        btnContinue.text = "No"
        btnQuit.setOnClickListener {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
            finish()
        }

        btnContinue.setOnClickListener {
            sheetDialog?.dismiss()
        }

        sheetDialog?.setContentView(view)
        sheetDialog?.show()
    }

    @SuppressLint("MissingPermission")
    fun createNotification(context: Activity, title: String, message: String) {
        val channelId = "your_channel_id" // Choose a unique channel ID
        val notificationId = 1 // Choose a unique notification ID

        // Create a notification channel (required for Android 8.0 and higher)
        createNotificationChannel(context, channelId)
        val fullScreenIntent = Intent(context, SplashActivity::class.java)
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_IMMUTABLE
        )
        fullScreenIntent.action="NOTIFICATION_ACTION"
        // Build the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(fullScreenPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)



        builder.setFullScreenIntent(fullScreenPendingIntent, true)
        // Display the notification
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())

        }
    }

    private fun createNotificationChannel(context: Activity, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Status Saver" // Choose a name for your channel
            val descriptionText =
                "Welcome to Status Saver" // Provide a description for your channel
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == "NOTIFICATION_ACTION") {
            finish()
        }

       
    }

}












