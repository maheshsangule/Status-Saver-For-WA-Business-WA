package com.developermaheshapps.statussaver.views.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.developermaheshapps.statussaver.R
import com.developermaheshapps.statussaver.Utils
import com.developermaheshapps.statussaver.databinding.ActivitySplashBinding
import com.developermaheshapps.statussaver.utils.SharedPrefUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 2
    private val notificationPermissionRequestCode = 123
    private var dialog: AlertDialog? = null

    private val APP_UPDATE_TYPE = AppUpdateType.IMMEDIATE
    private val MY_REQUEST_CODE = 500
    private var isPermissionDialogShown = false
    val activity = this
    var appUpdateManager: AppUpdateManager? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SharedPrefUtils.init(activity)
        appUpdateManager = AppUpdateManagerFactory.create(this)

        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission()
        } else {
            // Permission already granted, proceed to the main activity logic
            updateApp()
        }
    }

    private fun checkInternet(context: Activity) {
        var sheetDialog: BottomSheetDialog? = null
        var dialog: Dialog? = null
        sheetDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setContentView(R.layout.progressbar)
        dialog.setCanceledOnTouchOutside(false)
        if (!Utils.isConnected(context)) {
            val inflater: LayoutInflater = context.getLayoutInflater()
            val view1: View = inflater.inflate(
                R.layout.no_internet,
                context.findViewById<View>(R.id.ll_no_internet) as LinearLayout?
            )
            val btn_vpn_refresh: Button
            btn_vpn_refresh = view1.findViewById<Button>(R.id.btn_no_internet2)
            sheetDialog?.setContentView(view1)
            sheetDialog?.setCancelable(false)
            sheetDialog?.show()
            btn_vpn_refresh.setOnClickListener {
                object : CountDownTimer(1000, 2000) {
                    override fun onTick(l: Long) {
                        dialog.show()
                        sheetDialog?.dismiss()
                    }

                    override fun onFinish() {
                        dialog.dismiss()
                        sheetDialog?.dismiss()
                        finish()
//                        Utils.checkInternet(context)
                    }
                }.start()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            notificationPermissionRequestCode
        )
    }

    private fun initializePermissionDialog() {
        val alertDialog =
            MaterialAlertDialogBuilder(this@SplashActivity, R.style.CustomAlertDialogStyle)
                .apply {
                    setTitle("Permission Required")
                    setMessage("This app really need to use this permission. Do you want to allow it")
                    setPositiveButton("OK") { _, _ ->
                        openAppSettings()

                    }
                    setCancelable(false)
                }
                .create()
        dialog = alertDialog


    }

    private fun showPermissionDialog() {
        if (!SharedPrefUtils.getPrefBoolean("isGranted", false)) {
            initializePermissionDialog()
            dialog?.show()
            isPermissionDialogShown = true
        }
    }

    private fun dismissPermissionDialog() {
        dialog?.dismiss()
        isPermissionDialogShown = false
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, notificationPermissionRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == notificationPermissionRequestCode) {

            val isGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

            if (isGranted) {
                SharedPrefUtils.putPrefBoolean("isGranted", true)
                dismissPermissionDialog()
                updateApp()
            } else {
                showPermissionDialog()
            }
        }
    }


    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == notificationPermissionRequestCode) {
            if (isNotificationPermissionGranted()) {

                dismissPermissionDialog()
                updateApp()
            } else {

                showPermissionDialog()
            }

        }
    }

    private fun nextActivity() {
        binding.apply {

            if (Utils.isConnected(this@SplashActivity)) {
                Handler(Looper.myLooper()!!).postDelayed({
//                splashScreenHolder.slideToEndWithFadeOut()
//                splashScreenHolder.visibility = View.GONE

                    val TAG = "WhatsApp Type"

                    val whatsappPackage = "com.whatsapp"
                    val businessWhatsAppPackage = "com.whatsapp.w4b"

                    try {

                        if (packageManager.getLaunchIntentForPackage(businessWhatsAppPackage) != null) {
                            SharedPrefUtils.putPrefInt(
                                "app_type_icon",
                                R.drawable.ic_whatsapp_buisness
                            )
                            SharedPrefUtils.putPrefString("package", "com.whatsapp.w4b")

                            SharedPrefUtils.putPrefString("whatsapp", "bwhatsapp")
                        } else {
                            Log.d(TAG, "w4b not present")
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
//                    val status = SharedPrefUtils.getPrefString("whatsapp", "").toString()

//                    if (status.equals("whatsapp")) {
//                        val intent = packageManager.getLaunchIntentForPackage(whatsappPackage)
                    try {

                        if (packageManager.getLaunchIntentForPackage(whatsappPackage) != null) {
                            SharedPrefUtils.putPrefInt("app_type_icon", R.drawable.ic_whatsapp)
                            SharedPrefUtils.putPrefString("whatsapp", "whatsapp")
                            SharedPrefUtils.putPrefString("package", "com.whatsapp")
                        } else {
                            Log.d(TAG, "whatsapp not present")
//                                    Toast.makeText(this@MainActivity, "App not found", Toast.LENGTH_SHORT).show()
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                    startActivityForResult(
                        Intent(this@SplashActivity, MainActivity::class.java),
                        notificationPermissionRequestCode
                    )
                    finish()

                }, 3000)
            } else {
                checkInternet(this@SplashActivity)
            }
//            splashLayout.imageview.slideFromStart()

        }
    }

    private fun updateApp() {
        try {
            val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(APP_UPDATE_TYPE)
                ) {
                    try {
                        appUpdateManager!!.startUpdateFlowForResult(
                            appUpdateInfo, APP_UPDATE_TYPE, this, MY_REQUEST_CODE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                } else {
                    nextActivity()
                }
            }.addOnFailureListener { e: Exception ->
                e.printStackTrace()
                nextActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}