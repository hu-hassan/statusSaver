package com.devatrii.statussaver.views.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.devatrii.statussaver.R
import com.devatrii.statussaver.data.StatusRepo
import com.devatrii.statussaver.databinding.FragmentStatusBinding
import com.devatrii.statussaver.utils.Constants
import com.devatrii.statussaver.utils.SharedPrefKeys
import com.devatrii.statussaver.utils.SharedPrefUtils
import com.devatrii.statussaver.utils.getFolderPermissions
import com.devatrii.statussaver.utils.isStatusExistInStatuses
//import com.devatrii.statussaver.viewmodels.factories.SharedViewModel
import com.devatrii.statussaver.viewmodels.factories.StatusViewModel
import com.devatrii.statussaver.viewmodels.factories.StatusViewModelFactory
import com.devatrii.statussaver.views.activities.MainActivity
import com.devatrii.statussaver.views.adapters.MediaAdapter
import com.devatrii.statussaver.views.adapters.MediaViewPagerAdapter
import com.devatrii.statussaver.workers.RestartServiceWorker
import com.google.android.material.tabs.TabLayoutMediator
import java.util.concurrent.TimeUnit

class FragmentStatus : Fragment() {
    private val binding by lazy {
        FragmentStatusBinding.inflate(layoutInflater)
    }
    private var type: String? = null
    private val WHATSAPP_REQUEST_CODE = 101
    private val WHATSAPP_BUSINESS_REQUEST_CODE = 102
    lateinit var viewModel: StatusViewModel
    var isPermissionGrantedS = false
    var isPermissionGrantedSB = false
//    private val sharedViewModel: SharedViewModel by lazy {
//        ViewModelProvider(this).get(SharedViewModel::class.java)
//    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        binding.apply {


            arguments?.let {
                val repo = StatusRepo(requireActivity())
                viewModel = ViewModelProvider(
                    requireActivity(),
                    StatusViewModelFactory(repo)
                )[StatusViewModel::class.java]

                type = it.getString(Constants.FRAGMENT_TYPE_KEY, "")

                when (type) {
                    Constants.TYPE_WHATSAPP_MAIN -> {
                        // check permission
                        // granted then fetch statuses
                        // get permission
                        // fetch statuses
                        Log.d("FragmentStatus", "WhatsApp Main Created")
                        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                            false
                        )
                        binding.swipeRefreshLayout.setOnRefreshListener {
                            refreshStatuses()
                        }
                        if (isPermissionGranted) {
                            getWhatsAppStatuses()
                            getSavedStatuses()
//                            val workRequest = PeriodicWorkRequestBuilder<RestartServiceWorker>(0, TimeUnit.MINUTES)
//                                .build()
//                            WorkManager.getInstance(requireActivity()).enqueue(workRequest)

                        }
                        permissionLayout.btnPermission.setOnClickListener {
                            getFolderPermissions(
                                context = requireActivity(),
                                REQUEST_CODE = WHATSAPP_REQUEST_CODE,
                                initialUri = Constants.getWhatsappUri()
                            )
                        }


                        val mediaAdapter = MediaViewPagerAdapter(
                            requireActivity(),
                            imagesType = Constants.MEDIA_TYPE_WHATSAPP_IMAGES,
                            videosType = Constants.MEDIA_TYPE_WHATSAPP_VIDEOS,
                            savedType = Constants.MEDIA_TYPE_WHATSAPP_SAVED
                        )

                        statusViewPager.adapter = mediaAdapter
                        setupViewPager()
                        TabLayoutMediator(tabLayout, statusViewPager) { tab, pos ->
                            tab.text = when (pos) {
                                0 -> "Images"
                                1 -> "Videos"
                                2 -> "Saved"
                                else -> throw IllegalStateException("Unexpected position $pos")
                            }
                        }.attach()

                    }

                    Constants.TYPE_WHATSAPP_BUSINESS -> {
                        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                            false
                        )
                        Log.d("FragmentStatus", "WhatsApp Business Permission "+ isPermissionGranted)
                        binding.swipeRefreshLayout.setOnRefreshListener {
                            refreshStatuses()
                        }
                        if (isPermissionGranted) {
                            getWhatsAppBusinessStatuses()
                            getSavedStatuses()
//                            val workRequest = PeriodicWorkRequestBuilder<RestartServiceWorker>(0, TimeUnit.MINUTES)
//                                .build()
//                            WorkManager.getInstance(requireActivity()).enqueue(workRequest)
                        }
                        permissionLayout.btnPermission.setOnClickListener {
                            getFolderPermissions(
                                context = requireActivity(),
                                REQUEST_CODE = WHATSAPP_BUSINESS_REQUEST_CODE,
                                initialUri = Constants.getWhatsappBusinessUri()
                            )
                        }
                        val mediaAdapter = MediaViewPagerAdapter(
                            requireActivity(),
                            imagesType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES,
                            videosType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS,
                            savedType = Constants.MEDIA_TYPE_WHATSAPP_SAVED
                        )

                        statusViewPager.adapter = mediaAdapter
                        setupViewPager()
                        TabLayoutMediator(tabLayout, statusViewPager) { tab, pos ->
                            tab.text = when (pos) {
                                0 -> "Images"
                                1 -> "Videos"
                                2 -> "Saved"
                                else -> throw IllegalStateException("Unexpected position $pos")
                            }
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
                if(!SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, false)){
                    Toast.makeText(requireActivity(), "Please grant permission to refresh statuses", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    Toast.makeText(requireActivity(), "Refreshing WP Statuses", Toast.LENGTH_SHORT)
                        .show()
                    getWhatsAppStatuses()
                }
            }

            Constants.TYPE_WHATSAPP_BUSINESS ->{
                if(!SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED, false)){
                    Toast.makeText(requireActivity(), "Please grant permission to refresh statuses", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    Toast.makeText(
                        requireActivity(),
                        "Refreshing WP Business Statuses",
                        Toast.LENGTH_SHORT
                    ).show()
                    getWhatsAppBusinessStatuses()
                }
            }

        }

        Handler(Looper.myLooper()!!).postDelayed({
            binding.swipeRefreshLayout.isRefreshing = false
        }, 2000)
        getSavedStatuses()
    }

    fun getWhatsAppStatuses() {
        // function to get wp statuses
        isPermissionGrantedS = true
        binding.permissionLayoutHolder.visibility = View.GONE
        Log.d(TAG, "getWhatsAppBusinessStatuses: Getting WP  Statuses")
        viewModel.getWhatsAppStatuses()
    }

    private val TAG = "FragmentStatus"
    fun getWhatsAppBusinessStatuses() {
        // function to get wp statuses
        isPermissionGrantedSB = true
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
    fun getSavedStatuses() {
        // function to get saved statuses
        Log.d(TAG, "getSavedStatuses: Getting Saved Statuses")
        viewModel.getSavedStatuses()
    }

    override fun onResume() {
        super.onResume()
        when (type) {
            Constants.TYPE_WHATSAPP_MAIN -> {
                val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                    false
                )
                if (isPermissionGranted) {
                    getWhatsAppStatuses()
                    viewModel.getSavedStatuses()
//                    val workRequest = PeriodicWorkRequestBuilder<RestartServiceWorker>(0, TimeUnit.MINUTES)
//                        .build()
//                    WorkManager.getInstance(requireActivity()).enqueue(workRequest)
                    binding.swipeRefreshLayout.setOnRefreshListener {
                        refreshStatuses()
                    }
                }

            }
            Constants.TYPE_WHATSAPP_BUSINESS -> {
                val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                    false
                )
                if (isPermissionGranted) {
                    getWhatsAppBusinessStatuses()
                    viewModel.getSavedStatuses()
//                    val workRequest =
//                        PeriodicWorkRequestBuilder<RestartServiceWorker>(0, TimeUnit.MINUTES)
//                            .build()
//                    WorkManager.getInstance(requireActivity()).enqueue(workRequest)
                    binding.swipeRefreshLayout.setOnRefreshListener {
                        refreshStatuses()
                    }
                }
            }

        }
        getSavedStatuses()

    }
    private fun setupViewPager() {

        TabLayoutMediator(binding.tabLayout, binding.statusViewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "Images"
                1 -> "Videos"
                2 -> "Saved"
                else -> throw IllegalStateException("Unexpected position $pos")
            }
        }.attach()

        binding.statusViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 2) { // "Saved" tab
                    binding.permissionLayoutHolder.visibility = View.GONE
                    viewModel.getSavedStatuses()
                } else {
                    when (type) {
                        Constants.TYPE_WHATSAPP_MAIN -> {
                            if (!isPermissionGrantedS) {
                                Log.d(TAG, "onPageSelected: WhatsApp Main Permission Not Granted")
                                binding.permissionLayoutHolder.visibility = View.VISIBLE
                            }
                        }
                        Constants.TYPE_WHATSAPP_BUSINESS -> {
                            if (!isPermissionGrantedSB) {
                                Log.d(TAG, "onPageSelected: WhatsApp Business Permission Not Granted")
                                binding.permissionLayoutHolder.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        })
    }
    companion object{
        fun sendNotification(context: Context, title: String, message: String) {
            Log.d("TAG", "sendNotification: $title $message")

            // Create an intent for the notification tap action
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            // Define the notification channel ID
            val channelId = "default_channel"

            // Build the notification
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_download)  // Replace with your own icon
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for sound/vibration/alerts
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Enable sound, vibration, and lights

            // Get the NotificationManager service
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // For Android Oreo and above, create a notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH // High importance for sound/vibration/alerts
                ).apply {
                    enableVibration(true) // Enable vibration
                    vibrationPattern = longArrayOf(0, 500, 500, 500) // Custom vibration pattern
                    setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null) // Default sound
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Show the notification
            notificationManager.notify(0, notificationBuilder.build())
        }
    }

}
