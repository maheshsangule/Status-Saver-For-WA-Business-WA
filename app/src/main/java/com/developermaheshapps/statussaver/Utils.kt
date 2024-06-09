package com.developermaheshapps.statussaver

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.pm.PackageInfoCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import java.net.URLEncoder

class Utils(context1: Context?) {
    var context: Context? = null

    init {

        var context1 = context1
        context1 = context
    }

    companion object {
        @JvmStatic
        fun isConnected(context: Activity): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
                .isConnectedOrConnecting
        }

        fun check_internet(context: Activity) {
            var sheetDialog: BottomSheetDialog? = null
            var dialog: Dialog? = null
            sheetDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)
            dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog!!.setContentView(R.layout.progressbar)
            dialog.setCanceledOnTouchOutside(false)
            if (!isConnected(context)) {
                val inflater: LayoutInflater = context.layoutInflater
                val view1: View = inflater.inflate(
                    R.layout.no_internet,
                    context.findViewById<View>(R.id.ll_no_internet) as LinearLayout?
                )
                val btn_vpn_refresh: Button
                btn_vpn_refresh = view1.findViewById<Button>(R.id.btn_no_internet2)
                sheetDialog.setContentView(view1)
                sheetDialog.setCancelable(false)
                sheetDialog.show()
                btn_vpn_refresh.setOnClickListener {
                    object : CountDownTimer(1000, 2000) {
                        override fun onTick(l: Long) {
                            dialog.show()
                            sheetDialog.dismiss()
                        }

                        override fun onFinish() {
                            dialog.dismiss()
                            sheetDialog.dismiss()
                            check_internet(context)
                        }
                    }.start()
                }
            }
        }

        @SuppressLint("MissingInflatedId", "SetTextI18n")
        fun devData(context: Activity) {
            var sheetDialog: BottomSheetDialog? = null
            val inflater: LayoutInflater = context.layoutInflater
            val view: View = inflater.inflate(
                R.layout.dev_details,
                context.findViewById<View>(R.id.ll_dev_details) as LinearLayout?
            )
            sheetDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)
            val tv_versioncode: TextView = view.findViewById(R.id.tv_version_code)

            val ll_privacy: LinearLayout = view.findViewById(R.id.ll_privacy)
            val ll_rate: LinearLayout = view.findViewById(R.id.ll_rate)
            val ll_share: LinearLayout = view.findViewById(R.id.ll_share)


            val ib_instagram: ImageButton = view.findViewById(R.id.ib_instagram)
            val ib_linkedin: ImageButton = view.findViewById(R.id.ib_linkedin)
            val ib_youtube: ImageButton = view.findViewById(R.id.ib_youtube)
            val ib_email: ImageButton = view.findViewById(R.id.ib_email)
            val ib_whatsapp: ImageButton = view.findViewById(R.id.ib_whatsapp)
            val ib_facebook: ImageButton = view.findViewById(R.id.ib_facebook)
            val ib_rate: ImageButton = view.findViewById(R.id.ib_rate)
            val ib_share: ImageButton = view.findViewById(R.id.ib_share)
            val ib_privacy: ImageButton = view.findViewById(R.id.ib_privacy)

            ll_privacy.setOnClickListener {
                linkOpen(
                    context,
                  "https://sites.google.com/view/status--saver/home/privacy-policy"
                )
            }

            ll_rate.setOnClickListener {
                linkOpen(
                    context,
                    "https://play.google.com/store/apps/details?id=" + context.packageName
                )
            }
            ll_share.setOnClickListener {
                linkOpen(context, "share")
            }



            ib_privacy.setOnClickListener {
                linkOpen(context, "https://sites.google.com/view/status--saver/home/privacy-policy")
            }

            ib_instagram.setOnClickListener {
                linkOpen(context, "https://www.instagram.com/mahesh_sangule_?igsh=NGUwZWllNGxrYnh4")
            }
            ib_linkedin.setOnClickListener {
                linkOpen(context, "https://www.linkedin.com/in/mahesh-sangule-8a7a0025b")
            }
            ib_youtube.setOnClickListener {
                linkOpen(context, "https://www.youtube.com/@developer-mahesh")
            }
            ib_email.setOnClickListener {
                linkOpen(context, "email")
            }
            ib_whatsapp.setOnClickListener {
                linkOpen(
                    context,
                    "https://whatsapp.com/channel/0029Va6b8sf4NViiLFpoe80l"
                )
            }
            ib_facebook.setOnClickListener {
                linkOpen(
                    context,
                    "https://www.facebook.com/profile.php?id=100094035831979&mibextid=ZbWKwL"
                )
            }
            ib_rate.setOnClickListener {
                linkOpen(
                    context,
                    "https://play.google.com/store/apps/details?id=" + context.packageName
                )
            }
            ib_share.setOnClickListener {
                linkOpen(context, "share")
            }


            tv_versioncode.text = "V:" + PackageInfoCompat.getLongVersionCode(
                context.packageManager.getPackageInfo(
                    context.packageName,
                    0
                )
            ) + ".0"
            sheetDialog.setContentView(view)
            sheetDialog.setCancelable(true)
            sheetDialog.show()

        }

        fun linkOpen(context: Activity, s: String) {

            if (s == "share") {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + context.packageName);
                context.startActivity(
                    Intent.createChooser(
                        intent,
                        context.getString(R.string.app_name)
                    )
                )
            } else if (s == "email") {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:")
                intent.putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf<String>(context.getString(R.string.email))
                )
                intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
                intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_body))

                try {
                    context.startActivity(Intent.createChooser(intent, "Email Earning Quiz"))
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(context, "No mail app found!!!", Toast.LENGTH_SHORT)
                        .show()
                } catch (ex: Exception) {
                    Toast.makeText(context, "Unexpected Error!!!", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(s))
                context.startActivity(intent)
            }

        }

        fun guideDialog(context: Activity) {
            var sheetDialog: BottomSheetDialog? = null
            val inflater: LayoutInflater = context.layoutInflater
            val view: View = inflater.inflate(
                R.layout.dialog_guide,
                context.findViewById<View>(R.id.ll_guide_dialog) as LinearLayout?
            )
            sheetDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)
            val btn_ok = view.findViewById<MaterialButton>(R.id.okay_btn)
            btn_ok.setOnClickListener {
                sheetDialog.dismiss()
            }

            sheetDialog.setContentView(view)
            sheetDialog.setCancelable(true)
            sheetDialog.show()
        }

        @SuppressLint("MissingInflatedId")
        fun appNotFound(context: Activity, s: String) {
            var sheetDialog: BottomSheetDialog? = null
            val inflater: LayoutInflater = context.layoutInflater
            val view: View = inflater.inflate(
                R.layout.app_not_found_layout,
                context.findViewById<View>(R.id.ll_appNotFound) as LinearLayout?
            )
            sheetDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)

            val btn_install = view.findViewById<MaterialButton>(R.id.btn_install)
            val btn_later = view.findViewById<MaterialButton>(R.id.btn_later)
            val app_name = view.findViewById<TextView>(R.id.tv_app_name)

            if (s == "w") {
                app_name.text = " Whatsapp Not Found"
            } else if(s=="w4b"){
                app_name.text = " Whatsapp Business Not Found"
            }
            else if(s=="ww")
            {
                app_name.textSize=14f
                app_name.text = " Whatsapp Not Found, You have set it as Default option for sharing and reposting"
            }
            else if(s=="w4bw")
            {
                app_name.textSize=14f
                app_name.text = " Whatsapp Business Not Found, You have set it as Default option for sharing and reposting"
            }
            btn_install.setOnClickListener {
                if (s == "w" ||s=="ww") {

                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                    ).apply {
                        context.startActivity(this)
                    }
                } else if (s == "w4b"||s=="w4bw") {

                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp.w4b")
                    ).apply {
                        context.startActivity(this)
                    }
                }
            }
            btn_later.setOnClickListener {
                sheetDialog.dismiss()
            }

            sheetDialog.setContentView(view)
            sheetDialog.setCancelable(true)
            sheetDialog.show()
        }


        fun sendMessage(context: Activity) {
            var sheetDialog: BottomSheetDialog? = null
            val inflater: LayoutInflater = context.layoutInflater
            val view: View = inflater.inflate(
                R.layout.send_msg_layout,
                context.findViewById<View>(R.id.ll_msg_layout) as LinearLayout?
            )
            sheetDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)
            val btn_whatsapp = view.findViewById<MaterialButton>(R.id.btn_whatsapp)
            val btn_w4b = view.findViewById<MaterialButton>(R.id.btn_w4b)

            val etCountryCode = view.findViewById<EditText>(R.id.et_country_code)
            val etMobileNumber = view.findViewById<EditText>(R.id.et_mobile_number)
            val etMsg = view.findViewById<EditText>(R.id.et_msg)

            btn_whatsapp.setOnClickListener {
                sendMessageToWhatsApp(context, "com.whatsapp", etCountryCode, etMobileNumber, etMsg)
            }

            btn_w4b.setOnClickListener {
                sendMessageToWhatsApp(context, "com.whatsapp.w4b", etCountryCode, etMobileNumber, etMsg)
            }

            sheetDialog.setContentView(view)
            sheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            sheetDialog.setCancelable(true)
            sheetDialog.show()
        }

        private fun sendMessageToWhatsApp(
            context: Activity,
            packageName: String,
            etCountryCode: EditText,
            etMobileNumber: EditText,
            etMsg: EditText
        ) {
            if (!etCountryCode.text.isEmpty() && !etMobileNumber.text.isEmpty() && !etMsg.text.isEmpty()) {
                val phoneNumberWithCountryCode = etCountryCode.text.toString() + etMobileNumber.text.toString()
                val message = etMsg.text.toString()

                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra("jid", phoneNumberWithCountryCode + "@s.whatsapp.net")
                    intent.setPackage(packageName)
                    intent.putExtra(Intent.EXTRA_TEXT, message)

                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    appNotFound1(context, packageName)
                }
            } else {
                Toast.makeText(context, "Please Enter All Data", Toast.LENGTH_SHORT).show()
            }
        }


        private fun appNotFound1(context: Activity, packageName: String) {
            if(packageName=="com.whatsapp")
            {
                appNotFound(context,"w")

            }
            else{
                appNotFound(context,"w4b")

            }

        }

    }


}