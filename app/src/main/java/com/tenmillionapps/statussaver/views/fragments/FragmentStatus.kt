package com.tenmillionapps.statussaver.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import com.tenmillionapps.statussaver.R
import com.tenmillionapps.statussaver.data.StatusRepo
import com.tenmillionapps.statussaver.databinding.FragmentStatusBinding
import com.tenmillionapps.statussaver.utils.Constants
import com.tenmillionapps.statussaver.utils.SharedPrefKeys
import com.tenmillionapps.statussaver.utils.SharedPrefUtils
import com.tenmillionapps.statussaver.viewmodels.factories.StatusViewModel
import com.tenmillionapps.statussaver.viewmodels.factories.StatusViewModelFactory
import com.tenmillionapps.statussaver.views.activities.MainActivity
import com.tenmillionapps.statussaver.views.adapters.MediaViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat


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
    private lateinit var mediaAdapter: MediaViewPagerAdapter // Declare mediaAdapter here


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
//                        if (!isAppInstalled(requireContext(), "com.whatsapp.w4b")) {
//                            permissionLayoutHolder.visibility = View.VISIBLE
//                            permissionLayout.textView.text = "You don't have Whatsapp Business installed in your phone"
//                            permissionLayout.textView.textSize = 12f
//                            permissionLayout.btnPermission.text = "Install WhatsApp Business"
//                            permissionLayout.btnPermission.icon = null
//                            permissionLayout.btnPermission.setOnClickListener {
//                                val playStoreUri = Uri.parse("market://details?id=com.whatsapp.w4b")
//                                val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                requireContext().startActivity(intent)
//                            }
//                        }
                        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            permissionLayoutHolder.visibility = View.VISIBLE
                            permissionLayout.textView.text = "Allow Storage Permission to continue with this app"
                            binding.permissionLayout.textView.textSize = 12f
                            permissionLayout.btnPermission.text = "Allow Storage Permission"
                            permissionLayout.btnPermission.icon = null
                            permissionLayout.btnPermission.setOnClickListener {
                                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                            }
                        }
                        else
                        {
                            if (!isAppInstalled(requireContext(), "com.whatsapp")) {
                                permissionLayout.textView.text =
                                    "You don't have Whatsapp installed in your phone"
                                permissionLayout.textView.textSize = 12f
                                permissionLayout.btnPermission.icon = null
                                permissionLayout.btnPermission.text = "Install WhatsApp"
                                permissionLayout.btnPermission.setOnClickListener {
                                    val playStoreUri = Uri.parse("market://details?id=com.whatsapp")
                                    val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    requireContext().startActivity(intent)
                                }
                            }
                            else
                            {
//                                permissionLayout.textView.text = "Please Allow Permissions for Statuses"
//                                permissionLayout.textView.textSize = 16f
//                                permissionLayout.btnPermission.icon = resources.getDrawable(R.drawable.icons8_whatsapp)
//                                permissionLayout.btnPermission.text = "Allow Permission for WhatsApp"
//                                permissionLayout.btnPermission.setOnClickListener {
//                                    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_permission, null)
//                                    val dialog = Dialog(requireContext())
//                                    dialog.setContentView(dialogView)
//                                    dialog.window?.setLayout(
//                                        ViewGroup.LayoutParams.MATCH_PARENT,
//                                        ViewGroup.LayoutParams.WRAP_CONTENT
//                                    )
//                                    dialog.show()
//                                    val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn1)
//                                    dialogButton.setOnClickListener {
//                                        // Perform the desired task here
//                                        Log.d("Requesting permission of", "${Constants.WHATSAPP_PARENT_URI})")
//                                        getFolderPermissions(
//                                            context = requireActivity(),
//                                            REQUEST_CODE = WHATSAPP_REQUEST_CODE,
//                                            initialUri = Constants.getWhatsappUri()
//                                        )
//                                        dialog.dismiss()
//                                    }
//                                    val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
//                                    cancelButton.setOnClickListener {
//                                        dialog.dismiss()
//                                    }
//                                }
                                getWhatsAppStatuses()
                                getSavedStatuses()
                            }
                        }
//                        if (isPermissionGranted) {
//
//
//                        }
                        val viewPagerAdaoter = MediaViewPagerAdapter(
                            requireActivity(),
                            imagesType = Constants.MEDIA_TYPE_WHATSAPP_IMAGES,
                            videosType = Constants.MEDIA_TYPE_WHATSAPP_VIDEOS,
                            savedType = Constants.MEDIA_TYPE_WHATSAPP_SAVED
                        )
                        statusViewPager.adapter = viewPagerAdaoter
                        setupViewPager()

//                        MediaViewPagerAdapter(
//                            requireActivity(),
//                            imagesType = Constants.MEDIA_TYPE_WHATSAPP_IMAGES,
//                            videosType = Constants.MEDIA_TYPE_WHATSAPP_VIDEOS,
//                            savedType = Constants.MEDIA_TYPE_WHATSAPP_SAVED
//                        )

//                        statusViewPager.adapter = mediaAdapter
//                        setupViewPager()
//                        TabLayoutMediator(tabLayout, statusViewPager) { tab, pos ->
//                            tab.text = when (pos) {
//                                0 -> "Images"
//                                1 -> "Videos"
//                                2 -> "Saved"
//                                else -> throw IllegalStateException("Unexpected position $pos")
//                            }
//                        }.attach()
//                        statusViewPager.adapter = mediaAdapter
//                        setupViewPager()

                    }

                    Constants.TYPE_WHATSAPP_BUSINESS -> {
                        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                            false
                        )
                        Log.d("FragmentStatus", "WhatsApp Business Permission "+ isPermissionGranted)
                        binding.swipeRefreshLayout.setOnRefreshListener {
                            refreshStatuses()
                        }
                        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            permissionLayout.textView.text = "Allow Storage Permission to continue with this app"
                            binding.permissionLayout.textView.textSize = 12f
                            permissionLayout.btnPermission.text = "Allow Storage Permission"
                            permissionLayout.btnPermission.icon = null
                            permissionLayout.btnPermission.setOnClickListener {
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                                    if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                        // Show rationale and request permission
                                        requestPermissions(
                                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                            100
                                        )
                                    } else {
                                        // Permission denied permanently, show dialog and direct to settings
                                        AlertDialog.Builder(context).apply {
                                            setTitle("Permission Required")
                                            setMessage("This app needs storage permission to proceed. Please grant the permission in app settings.")
                                            setPositiveButton("Open Settings") { _, _ ->
                                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                    data = Uri.fromParts("package", context.packageName, null)
                                                }
                                                context.startActivity(intent)
                                            }
                                            setNegativeButton("Cancel") { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                            show()
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if (!isAppInstalled(requireContext(), "com.whatsapp.w4b")) {
                                permissionLayout.textView.text = "You don't have Whatsapp Business installed in your phone"
                                permissionLayout.textView.textSize = 12f
                                permissionLayout.btnPermission.text = "Install WhatsApp Business"
                                permissionLayout.btnPermission.icon = null
                                permissionLayout.btnPermission.setOnClickListener {
                                    val playStoreUri = Uri.parse("market://details?id=com.whatsapp.w4b")
                                    val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    requireContext().startActivity(intent)
                                }
                            } else {
//                                Log.d(TAG, "wbm: WhatsApp Business both conditions met")
//                                permissionLayout.textView.text = "Please Allow Permissions for Statuses"
//                                permissionLayout.textView.textSize = 16f
//                                permissionLayout.btnPermission.icon = resources.getDrawable(R.drawable.whatsapp_business)
//                                permissionLayout.btnPermission.text = "Allow Permission for WhatsApp Business"
//                                permissionLayout.btnPermission.setOnClickListener {
//                                    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_permission, null)
//                                    val dialog = Dialog(requireContext())
//                                    dialog.setContentView(dialogView)
//                                    dialog.window?.setLayout(
//                                        ViewGroup.LayoutParams.MATCH_PARENT,
//                                        ViewGroup.LayoutParams.WRAP_CONTENT
//                                    )
//                                    dialog.show()
//                                    val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn1)
//                                    dialogButton.setOnClickListener {
//                                        // Perform the desired task here
//                                        getFolderPermissions(
//                                            context = requireActivity(),
//                                            REQUEST_CODE = WHATSAPP_BUSINESS_REQUEST_CODE,
//                                            initialUri = Constants.getWhatsappUri()
//                                        )
//                                        dialog.dismiss()
//                                    }
//                                    val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
//                                    cancelButton.setOnClickListener {
//                                        dialog.dismiss()
//                                    }
//
//                                }
                                getWhatsAppBusinessStatuses()
                                getSavedStatuses()
                            }
                        }
//                        if (isPermissionGranted) {
//
//                        }
//                        permissionLayout.btnPermission.icon = resources.getDrawable(R.drawable.whatsapp_business)
//                        permissionLayout.btnPermission.text = "Allow Permission for WhatsApp Business"
//                        permissionLayout.btnPermission.setOnClickListener {
//                            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_permission, null)
//                            val dialog = Dialog(requireContext())
//                            dialog.setContentView(dialogView)
//                            dialog.window?.setLayout(
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT
//                            )
//                            dialog.show()
//                            val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn1)
//                            dialogButton.setOnClickListener {
//                                // Perform the desired task here
//                                getFolderPermissions(
//                                    context = requireActivity(),
//                                    REQUEST_CODE = WHATSAPP_BUSINESS_REQUEST_CODE,
//                                    initialUri = Constants.getWhatsappBusinessUri()
//                                )
//                                dialog.dismiss()
//                            }
//                            val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
//                            cancelButton.setOnClickListener {
//                                dialog.dismiss()
//                            }
//
//                        }
                        val viewPagerAdaoter =MediaViewPagerAdapter(
                            requireActivity(),
                            imagesType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES,
                            videosType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS,
                            savedType = Constants.MEDIA_TYPE_WHATSAPP_SAVED
                        )
                        statusViewPager.adapter = viewPagerAdaoter
                        setupViewPager()

//                        statusViewPager.adapter = mediaAdapter
//                        setupViewPager()
//                        TabLayoutMediator(tabLayout, statusViewPager) { tab, pos ->
//                            tab.text = when (pos) {
//                                0 -> "Images"
//                                1 -> "Videos"
//                                2 -> "Saved"
//                                else -> throw IllegalStateException("Unexpected position $pos")
//                            }
//                        }.attach()

                    }
                    else -> throw IllegalStateException("Unexpected type $type")
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
                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(requireActivity(), "Please grant storage permission to refresh statuses", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else if (!isAppInstalled(requireContext(), "com.whatsapp")) {
                        Toast.makeText(requireActivity(), "You don't have Whatsapp installed in your phone", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else{
                        Toast.makeText(requireActivity(), "Refreshing WP Statuses", Toast.LENGTH_SHORT)
                            .show()
                        getWhatsAppStatuses()
                    }

                }
            }

            Constants.TYPE_WHATSAPP_BUSINESS ->{
                if(!SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, false)){
                    Toast.makeText(requireActivity(), "Please grant permission to refresh statuses", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(requireActivity(), "Please grant storage permission to refresh statuses", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else if (!isAppInstalled(requireContext(), "com.whatsapp")) {
                        Toast.makeText(requireActivity(), "You don't have Whatsapp Business installed in your phone", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else{
                        Toast.makeText(
                            requireActivity(),
                            "Refreshing WP Business Statuses",
                            Toast.LENGTH_SHORT
                        ).show()
                        getWhatsAppBusinessStatuses()
                    }

                }
            }

        }

        Handler(Looper.myLooper()!!).postDelayed({
            binding.swipeRefreshLayout.isRefreshing = false
        }, 2000)
        getSavedStatuses()
    }
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            Log.d(TAG, "isAppInstalled: App Installed")
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "isAppInstalled: App Not Installed")
            false
        }
    }

    fun getWhatsAppStatuses() {
        // function to get wp statuses
            isPermissionGrantedS = true
            binding.permissionLayoutHolder.visibility = View.GONE
        Log.d(TAG, "getWhatsAppBusinessStatuses: Getting WP  Statuses")
        viewModel.getWhatsAppStatuses()
    }

    fun getWhatsAppBusinessStatuses() {
        // function to get wp statuses
            isPermissionGrantedSB = true
            binding.permissionLayoutHolder.visibility = View.GONE

        Log.d(TAG, "getWhatsAppBusinessStatuses: Getting Wp Business Statuses")
        viewModel.getWhatsAppBusinessStatuses()
    }

    private val TAG = "FragmentStatus"

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            val treeUri = data?.data!!
            requireActivity().contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            if (requestCode == WHATSAPP_REQUEST_CODE) {
                // whatsapp logic here
                SharedPrefUtils.putPrefString(
                    SharedPrefKeys.PREF_KEY_WP_TREE_URI,
//                    Constants.getWhatsappUri().toString()
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
                binding.apply {
//                    if (!isAppInstalled(requireContext(), "com.whatsapp")) {
//                        permissionLayoutHolder.visibility = View.VISIBLE
//                        permissionLayout.textView.text =
//                            "You don't have Whatsapp installed in your phone"
//                        permissionLayout.textView.textSize = 12f
//                        permissionLayout.btnPermission.icon = null
//                        permissionLayout.btnPermission.text = "Install WhatsApp"
//                        permissionLayout.btnPermission.setOnClickListener {
//                            val playStoreUri = Uri.parse("market://details?id=com.whatsapp")
//                            val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            requireContext().startActivity(intent)
//                        }
//                    }

                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED
                    )
                    {
                        binding.permissionLayout.textView.text = "Allow Storage Permission to continue with this app"
                        binding.permissionLayout.textView.textSize = 12f
                        binding.permissionLayout.btnPermission.text = "Allow Storage Permission"
                        permissionLayout.btnPermission.icon = null
                        binding.permissionLayout.btnPermission.setOnClickListener {
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    // Show rationale and request permission
                                    requestPermissions(
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        100
                                    )
                                } else {
                                    // Permission denied permanently, show dialog and direct to settings
                                    AlertDialog.Builder(context).apply {
                                        setTitle("Permission Required")
                                        setMessage("This app needs storage permission to proceed. Please grant the permission in app settings.")
                                        setPositiveButton("Open Settings") { _, _ ->
                                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts("package", context.packageName, null)
                                            }
                                            context.startActivity(intent)
                                        }
                                        setNegativeButton("Cancel") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        show()
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (!isAppInstalled(requireContext(), "com.whatsapp")) {
                            permissionLayout.textView.text =
                                "You don't have Whatsapp installed in your phone"
                            permissionLayout.textView.textSize = 12f
                            permissionLayout.btnPermission.icon = null
                            permissionLayout.btnPermission.text = "Install WhatsApp"
                            permissionLayout.btnPermission.setOnClickListener {
                                val playStoreUri = Uri.parse("market://details?id=com.whatsapp")
                                val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                requireContext().startActivity(intent)
                            }
                        } else {
//                            binding.permissionLayout.textView.text = "Please Allow Permissions for Statuses"
//                            binding.permissionLayout.textView.textSize = 16f
//                            binding.permissionLayout.btnPermission.icon = resources.getDrawable(R.drawable.icons8_whatsapp)
//                            binding.permissionLayout.btnPermission.text = "Allow Permission for WhatsApp"
//                            binding.permissionLayout.btnPermission.setOnClickListener {
//                                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_permission, null)
//                                val dialog = Dialog(requireContext())
//                                dialog.setContentView(dialogView)
//                                dialog.window?.setLayout(
//                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT
//                                )
//                                dialog.show()
//                                val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn1)
//                                dialogButton.setOnClickListener {
//                                    Log.d("Requesting permission of", "${Constants.WHATSAPP_PARENT_URI})")
//
//                                    // Perform the desired task here
//                                    getFolderPermissions(
//                                        context = requireActivity(),
//                                        REQUEST_CODE = WHATSAPP_REQUEST_CODE,
//                                        initialUri = Constants.getWhatsappUri()
//                                    )
//                                    dialog.dismiss()
//                                }
//                                val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
//                                cancelButton.setOnClickListener {
//                                    dialog.dismiss()
//                                }
//
//                            }
                            getWhatsAppStatuses()
                            viewModel.getSavedStatuses()
                            binding.swipeRefreshLayout.setOnRefreshListener {
                                refreshStatuses()
                            }
                        }
                    }
                }
//                if (isPermissionGranted) {
//
//                }

            }
            Constants.TYPE_WHATSAPP_BUSINESS -> {
                val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                    false
                )
                binding.apply {
//                    if (!isAppInstalled(requireContext(), "com.whatsapp.w4b")) {
//                        permissionLayoutHolder.visibility = View.VISIBLE
//                        permissionLayout.textView.text =
//                            "You don't have Whatsapp Business installed in your phone"
//                        permissionLayout.textView.textSize = 12f
//                        permissionLayout.btnPermission.text = "Install WhatsApp Business"
//                        permissionLayout.btnPermission.icon = null
//                        permissionLayout.btnPermission.setOnClickListener {
//                            val playStoreUri = Uri.parse("market://details?id=com.whatsapp.w4b")
//                            val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            requireContext().startActivity(intent)
//                        }
//                    }
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED
                    )
                    {
                        binding.permissionLayout.textView.text = "Allow Storage Permission to continue with this app"
                        binding.permissionLayout.textView.textSize = 12f
                        binding.permissionLayout.btnPermission.text = "Allow Storage Permission"
                        binding.permissionLayout.btnPermission.icon = null
                        binding.permissionLayout.btnPermission.setOnClickListener {
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    // Show rationale and request permission
                                    requestPermissions(
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        100
                                    )
                                } else {
                                    // Permission denied permanently, show dialog and direct to settings
                                    AlertDialog.Builder(context).apply {
                                        setTitle("Permission Required")
                                        setMessage("This app needs storage permission to proceed. Please grant the permission in app settings.")
                                        setPositiveButton("Open Settings") { _, _ ->
                                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts("package", context.packageName, null)
                                            }
                                            context.startActivity(intent)
                                        }
                                        setNegativeButton("Cancel") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        show()
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (!isAppInstalled(requireContext(), "com.whatsapp.w4b")) {
                            permissionLayout.textView.text =
                                "You don't have Whatsapp Business installed in your phone"
                            permissionLayout.textView.textSize = 12f
                            permissionLayout.btnPermission.text = "Install WhatsApp Business"
                            permissionLayout.btnPermission.icon = null
                            permissionLayout.btnPermission.setOnClickListener {
                                val playStoreUri = Uri.parse("market://details?id=com.whatsapp.w4b")
                                val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                requireContext().startActivity(intent)
                            }
                        } else {
//                            Log.d(TAG, "wbm: WhatsApp Business both conditions met")
//                            binding.permissionLayout.textView.text = "Please Allow Permissions for Statuses"
//                            binding.permissionLayout.textView.textSize = 16f
//                            binding.permissionLayout.btnPermission.icon = resources.getDrawable(R.drawable.whatsapp_business)
//                            binding.permissionLayout.btnPermission.text = "Allow Permission for WhatsApp Business"
//                            binding.permissionLayout.btnPermission.setOnClickListener {
//                                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_permission, null)
//                                val dialog = Dialog(requireContext())
//                                dialog.setContentView(dialogView)
//                                dialog.window?.setLayout(
//                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT
//                                )
//                                dialog.show()
//                                val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn1)
//                                dialogButton.setOnClickListener {
//                                    // Perform the desired task here
//                                    getFolderPermissions(
//                                        context = requireActivity(),
//                                        REQUEST_CODE = WHATSAPP_BUSINESS_REQUEST_CODE,
//                                        initialUri = Constants.getWhatsappUri()
//                                    )
//                                    dialog.dismiss()
//                                }
//                                val cancelButton =
//                                    dialogView.findViewById<ImageView>(R.id.cancel_btn)
//                                cancelButton.setOnClickListener {
//                                    dialog.dismiss()
//                                }
//
//                            }
                            getWhatsAppBusinessStatuses()
                            viewModel.getSavedStatuses()
                            binding.swipeRefreshLayout.setOnRefreshListener {
                                refreshStatuses()
                            }
                        }
                    }
                }
//                if (isPermissionGranted) {
//
//                }
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