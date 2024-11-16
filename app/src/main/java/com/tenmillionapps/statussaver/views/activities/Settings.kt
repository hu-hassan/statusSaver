package com.tenmillionapps.statussaver.views.activities

import BaseActivity
import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tenmillionapps.statussaver.R
import com.tenmillionapps.statussaver.databinding.ActivitySettingsBinding
import com.tenmillionapps.statussaver.databinding.DialogGuideBinding
import com.tenmillionapps.statussaver.databinding.ItemSettingsBinding
import com.tenmillionapps.statussaver.models.SettingsModel
import com.tenmillionapps.statussaver.services.FileObserverService
import com.tenmillionapps.statussaver.utils.SharedPrefUtils

class Settings : BaseActivity() {
    private val activity = this
    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private val list = ArrayList<SettingsModel>()
    private val adapter by lazy {
        SettingsAdapter(list, this)
    }
    private var isBusiness: Boolean = false
    private var notificationState: Boolean = false

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SharedPrefUtils.init(this)
        performAction()
        isBusiness = intent.getBooleanExtra("isBusiness", false)
        if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED){
            notificationState = true
        }
        else{
            notificationState = false
        }

//        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (isBusiness) {
//                    val intent = Intent(this@Settings, MainActivity::class.java)
//                    intent.putExtra(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_BUSINESS)
//                    startActivity(intent)
//                } else {
//                    val intent = Intent(this@Settings, MainActivity::class.java)
//                    intent.putExtra(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
//                    startActivity(intent)
//                }
//            }
//        }
//        onBackPressedDispatcher.addCallback(this, callback)

        binding.apply {
            val toolbar = binding.root.findViewById<MaterialToolbar>(R.id.tool_bar)
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            settingsRecyclerView.adapter = adapter
            list.add(
                SettingsModel(
                    title = "Notifications",
                    desc = "Get notified when new statuses are available"
                )
            )
            list.add(
                SettingsModel(
                    title = "How to use",
                    desc = "Know how to download statuses"
                )
            )
            list.add(
                SettingsModel(
                    title = "Save in Folder",
                    desc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/${getString(R.string.app_name)} & " +
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString() + "/${getString(R.string.app_name)}"
                )
            )
            list.add(
                SettingsModel(
                    title = "Feedback",
                    desc = "Report bugs and tell us what to improve"
                )
            )
            list.add(
                SettingsModel(
                    title = "Privacy Policy",
                    desc = "Read Our Terms & Conditions"
                )
            )
            list.add(
                SettingsModel(
                    title = "Rate our app",
                    desc = "Please support our work by your rating"
                )
            )
            list.add(
                SettingsModel(
                    title = "Share our app",
                    desc = "Share our app with your friends and family"
                )
            )
        }
    }


    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    inner class SettingsAdapter(
        var list: ArrayList<SettingsModel>,
        var context: Context
    ) : RecyclerView.Adapter<SettingsAdapter.viewHolder>() {

        inner class viewHolder(var binding: ItemSettingsBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(model: SettingsModel, position: Int) {
                binding.apply {
                    settingsTitle.text = model.title
                    settingsDesc.text = model.desc
                    switchNotifications.visibility = View.GONE
                    if (model.title == "Notifications") {
                        switchNotifications.visibility = View.VISIBLE
                        checkNotificationPermission()
                        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                if (isNotificationPermissionGranted()) {
                                    binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                                } else {
                                    binding.switchNotifications.isChecked = false
                                    binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
                                    showPermissionDialog()
                                }
                            } else {
                                binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
                                binding.switchNotifications.isEnabled = true
                            }
                        }
                    }

                    root.setOnClickListener {
                        when (position) {
                            0 -> {
                                // Handle Notifications click
                            }
                            1 -> {
                                // how to use 1st item
                                val dialog = Dialog(context)
                                val dialogBinding =
                                    DialogGuideBinding.inflate((context as Activity).layoutInflater)
                                dialogBinding.okayBtn.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.setContentView(dialogBinding.root)

                                dialog.window?.setLayout(
                                    ActionBar.LayoutParams.MATCH_PARENT,
                                    ActionBar.LayoutParams.WRAP_CONTENT
                                )

                                dialog.show()
                                val cancelBtn = dialog.findViewById<ImageView>(R.id.cancel_btn)
                                cancelBtn.setOnClickListener {
                                    dialog.dismiss()
                                }
                            }
                            2 -> {
                                // Handle Save in Folder click
                            }
                            3 -> {
//                                MaterialAlertDialogBuilder(context).apply {
//                                    setTitle("Disclaimer")
//                                    setMessage("We do not collect any data from user")
//                                    setPositiveButton("Okay", null)
//                                    show()
//                                }
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:10millionapps@gmail.com?subject=" + Uri.encode("Feedback of Status Saver App"))
                                }
                                context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
//                                dialog.dismiss()
                            }
                            4 -> {
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://tenmillionapps.blogspot.com/2024/11/status-saver-privacy-policy.html")).apply {
                                    context.startActivity(this)
                                }
                            }
                            5 -> {
                                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_feedback, null)
                                val dialog = Dialog(context)
                                dialog.setContentView(dialogView)
                                dialog.window?.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                dialog.show()
                                val dialogButton = dialogView.findViewById<MaterialButton>(R.id.okay_btn)
                                dialogButton.text = "Rate Us"
                                dialogButton.icon = null
                                dialogButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.buttonshade)
                                val ratingStars = dialogView.findViewById<RatingBar>(R.id.ratingBar)
                                ratingStars.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
                                    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                                        if (rating >= 4) {
                                            dialogButton.text = "Rate Us on Google Play store"
                                            dialogButton.icon = ContextCompat.getDrawable(context, R.drawable.playstore_svgrepo_com)
                                            dialogButton.iconTint = null
                                            dialogButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                                            dialogButton.setOnClickListener {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                                                context.startActivity(intent)
                                                dialog.dismiss()
                                            }
                                        } else {
                                            dialogButton.icon = null
                                            dialogButton.text = "Feedback"
                                            dialogButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                                            dialogButton.setOnClickListener {
                                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                    data = Uri.parse("mailto:10millionapps@gmail.com?subject=" + Uri.encode("Feedback of Status Saver App"))
                                                }
                                                context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
                                                dialog.dismiss()
                                            }
                                        }
                                        if (rating < 1) {
                                            dialogButton.text = "Rate Us"
                                            dialogButton.icon = null
                                            dialogButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.buttonshade)
                                            dialogButton.setOnClickListener {
                                            }
                                        }
                                    }
                                })
                                val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
                                cancelButton.setOnClickListener {
                                    dialog.dismiss()
                                }
                            }
                            6 -> {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Status Saver")
                                    putExtra(Intent.EXTRA_TEXT,"Let me recommend you this application :\n \n https://play.google.com/store/apps/details?id=${context.packageName}")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share via"))
                            }
                        }
                    }
                }
            }

            val isServiceRunning = isFileObserverServiceRunning()

//            private fun checkNotificationPermission() {
//                val isPermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
//                val isServiceRunning = isFileObserverServiceRunning()
//
//                if (isPermissionGranted && isServiceRunning) {
//                    binding.switchNotifications.isChecked = true
//                    binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
//                    binding.switchNotifications.isEnabled = false
//                } else {
//                    binding.switchNotifications.isChecked = false
//                    binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
//                    binding.switchNotifications.isEnabled = true
//                }
//            }
@SuppressLint("NewApi")
private fun checkNotificationPermission() {
    val isPermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    val isServiceRunning = isFileObserverServiceRunning()

    if (isPermissionGranted && isServiceRunning) {
        binding.switchNotifications.isChecked = true
        binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
        binding.switchNotifications.isEnabled = false
    } else {
        binding.switchNotifications.isChecked = false
        binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
        binding.switchNotifications.isEnabled = true
    }
}
            private fun isNotificationPermissionGranted(): Boolean {
                return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED && isFileObserverServiceRunning()
            }

            private fun showPermissionDialog() {
                val dialogView = LayoutInflater.from(this@Settings).inflate(R.layout.dialog_service, null)
                val dialog = Dialog(this@Settings)
                dialog.setContentView(dialogView)
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.show()
                val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn)
                dialogButton.setOnClickListener {
                    requestNotificationPermission()
                    dialog.dismiss()
                }
                val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                    binding.switchNotifications.isChecked = false
                    binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
                    binding.switchNotifications.isEnabled = true
                }
//                AlertDialog.Builder(context).apply {
//                    setTitle("Permission Required")
//                    setMessage("This app needs notification permission to proceed.")
//                    setPositiveButton("Okay") { _, _ ->
//
//                    }
//                    setNegativeButton("Cancel") { dialog, _ ->
//                        dialog.dismiss()
//
//                    }
//                    show()
//                }
            }
//
//            @SuppressLint("NewApi")
//            private fun requestNotificationPermission() {
//                if(ContextCompat.checkSelfPermission(this@Settings, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_DENIED){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
//                    val intent = Intent(this@Settings, FileObserverService::class.java)
//                    stopService(intent)
//                    val serviceIntent = Intent(this@Settings, FileObserverService::class.java)
//                    this@Settings.startForegroundService(serviceIntent)
//                } else {
//                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
//                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                    }
//                    context.startActivity(intent)
//                    val intent2 = Intent(this@Settings, FileObserverService::class.java)
//                    stopService(intent2)
//                    val serviceIntent = Intent(this@Settings, FileObserverService::class.java)
//                    this@Settings.startForegroundService(serviceIntent)
//                }
//                }
//                if(ContextCompat.checkSelfPermission(this@Settings, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED){
//                    Log.d("Settings", "Permission already granted just restarting service")
//                    val intent = Intent(this@Settings, FileObserverService::class.java)
//                    stopService(intent)
//                    val serviceIntent = Intent(this@Settings, FileObserverService::class.java)
//                    this@Settings.startForegroundService(serviceIntent)
//                }
//            }
@SuppressLint("NewApi")
private fun requestNotificationPermission() {
    if (ContextCompat.checkSelfPermission(this@Settings, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_DENIED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            val intent = Intent(this@Settings, FileObserverService::class.java)
            stopService(intent)
            val serviceIntent = Intent(this@Settings, FileObserverService::class.java)
            this@Settings.startForegroundService(serviceIntent)
            // Update the switch state after starting the service
//            checkNotificationPermission()
        } else {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            val intent2 = Intent(this@Settings, FileObserverService::class.java)
            stopService(intent2)
            val serviceIntent = Intent(this@Settings, FileObserverService::class.java)
            this@Settings.startForegroundService(serviceIntent)
            // Update the switch state after starting the service
//            checkNotificationPermission()
            context.startActivity(intent)
        }
    }

    if (ContextCompat.checkSelfPermission(this@Settings, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
        Log.d("Settings", "Permission already granted, just restarting service")
        val intent = Intent(this@Settings, FileObserverService::class.java)
        stopService(intent)
        val serviceIntent = Intent(this@Settings, FileObserverService::class.java)
        this@Settings.startForegroundService(serviceIntent)
        // Update the switch state after starting the service
        checkNotificationPermission()
    }
}

            fun onPermissionDenied() {
                binding.switchNotifications.isChecked = false
                binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
                binding.switchNotifications.isEnabled = true
            }

            fun onPermissionGranted() {
                binding.switchNotifications.isChecked = true
                binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                binding.switchNotifications.isEnabled = false
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
            return viewHolder(ItemSettingsBinding.inflate(LayoutInflater.from(context), parent, false))
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: viewHolder, position: Int) {
            holder.bind(model = list[position], position)
        }
    }
    private fun isFileObserverServiceRunning(): Boolean {
        var returnValue = false
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (FileObserverService::class.java.name == service.service.className) {
                returnValue = true
            }
            else{
                returnValue = false
            }
        }
        return returnValue
    }

//    @SuppressLint("NewApi")
//    override fun onResumeFragments() {
//        super.onResumeFragments()
//        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
//            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
//            false
//        ) || SharedPrefUtils.getPrefBoolean(
//            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
//            false
//        )
//
//        if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
//            if (!isFileObserverServiceRunning()) {
//                Log.d("isFileObserverServiceRunning", "${isFileObserverServiceRunning()}")
//                if (isPermissionGranted) {
//                    Log.d("isFileObserverServiceRunning", "${isFileObserverServiceRunning()}")
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || !notificationState) {
//                        WorkManager.getInstance(this).cancelAllWork()
//                        val intent = Intent(this, FileObserverService::class.java)
//                        stopService(intent)
//                        val serviceIntent = Intent(this, FileObserverService::class.java)
//                        this.startForegroundService(serviceIntent)
//                    }
//                }
//            }
//        }
//    }
}