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
import com.devatrii.statussaver.R
import com.devatrii.statussaver.data.StatusRepo
import com.devatrii.statussaver.databinding.FragmentStatusBinding
import com.devatrii.statussaver.utils.Constants
import com.devatrii.statussaver.utils.SharedPrefKeys
import com.devatrii.statussaver.utils.SharedPrefUtils
import com.devatrii.statussaver.utils.getFolderPermissions
import com.devatrii.statussaver.viewmodels.factories.StatusViewModel
import com.devatrii.statussaver.viewmodels.factories.StatusViewModelFactory
import com.devatrii.statussaver.views.activities.MainActivity
import com.devatrii.statussaver.views.adapters.MediaViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class FragmentStatus : Fragment() {
    private val binding by lazy {
        FragmentStatusBinding.inflate(layoutInflater)
    }
    private lateinit var type: String
    private val WHATSAPP_REQUEST_CODE = 101
    private val WHATSAPP_BUSINESS_REQUEST_CODE = 102
    private val YOUR_REQUEST_CODE = 1002  // Define your request code here
    private val viewPagerTitles = arrayListOf("Images", "Videos")
    lateinit var viewModel: StatusViewModel


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
                            getFolderPermissions(
                                context = requireActivity(),
                                REQUEST_CODE = WHATSAPP_REQUEST_CODE,
                                initialUri = Constants.getWhatsappUri()
                            )
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
                            getFolderPermissions(
                                context = requireActivity(),
                                REQUEST_CODE = WHATSAPP_BUSINESS_REQUEST_CODE,
                                initialUri = Constants.getWhatsappBusinessUri()
                            )
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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Check if the permission is already granted
//        if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
//            // If not, request the permission
//            requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), YOUR_REQUEST_CODE)
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == YOUR_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission was granted
//                Toast.makeText(requireContext(), "Notification permission granted!", Toast.LENGTH_SHORT).show()
//            } else {
//                // Permission was denied
//                Toast.makeText(requireContext(), "Notification permission denied!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

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
        Log.d(TAG, "getWhatsAppBusinessStatuses: Getting WP  Statuses")
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

                    binding.swipeRefreshLayout.setOnRefreshListener {
                        refreshStatuses()
                    }

                }
            }

        }

    }

}

















