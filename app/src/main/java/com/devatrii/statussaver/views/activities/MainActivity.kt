package com.devatrii.statussaver.views.activities

//import FileObserverService
//import FolderFileObserver
import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.devatrii.statussaver.services.FileObserverService
import com.devatrii.statussaver.R
import com.devatrii.statussaver.data.StatusRepo
import com.devatrii.statussaver.databinding.ActivityMainBinding
import com.devatrii.statussaver.utils.Constants
import com.devatrii.statussaver.utils.SharedPrefKeys
import com.devatrii.statussaver.utils.SharedPrefUtils
import com.devatrii.statussaver.utils.replaceFragment
import com.devatrii.statussaver.utils.slideFromStart
import com.devatrii.statussaver.utils.slideToEndWithFadeOut
import com.devatrii.statussaver.views.fragments.FragmentSettings
import com.devatrii.statussaver.views.fragments.FragmentStatus
import com.devatrii.statussaver.workers.RestartServiceWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var currentSelectedItemId: Int = R.id.menu_status // Default selected item
    private lateinit var statusRepo: StatusRepo
    private var fileObserver: FileObserver? = null
    private var isBusiness: Boolean = false



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SharedPrefUtils.init(activity)
        statusRepo = StatusRepo(this)
//        val workRequest = PeriodicWorkRequestBuilder<RestartServiceWorker>(0, TimeUnit.MINUTES)
//            .build()
//        WorkManager.getInstance(this).enqueue(workRequest)
        val buttonIcon = findViewById<ImageButton>(R.id.button_icon)
        val text = findViewById<TextView>(R.id.toolbar_title)
        binding.apply {
            splashLogic()
            requestPermission()
            val fragmentWhatsapp = FragmentStatus()
            val bundle = Bundle()
            bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
            replaceFragment(fragmentWhatsapp, bundle)
            bottomNavigationView.setOnItemSelectedListener {
                if (it.itemId == currentSelectedItemId) {
                    // If the selected item is the same as the current item, do nothing
                    return@setOnItemSelectedListener false
                }
                currentSelectedItemId = it.itemId // Update the current selected item
                when (it.itemId) {
                    R.id.menu_status -> {
                        buttonIcon?.visibility = View.VISIBLE
                        text?.visibility = View.VISIBLE
                        val fragmentWhatsapp = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                        replaceFragment(fragmentWhatsapp, bundle)
                        isBusiness = false
                    }
                    R.id.menu_business_status -> {
                        buttonIcon?.visibility = View.VISIBLE
                        text?.visibility = View.VISIBLE
                        val fragmentWhatsapp = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_BUSINESS)
                        replaceFragment(fragmentWhatsapp, bundle)
                        isBusiness = true
                    }
                    R.id.menu_settings -> {
                        buttonIcon?.visibility = View.GONE
                        replaceFragment(FragmentSettings())
                    }
                }
                return@setOnItemSelectedListener true
            }
        }

        // Find the ImageButton and set an OnClickListener
        buttonIcon?.setOnClickListener {
            openSendMessageActivity(isBusiness)

        }
    }

    private fun openSendMessageActivity(isBusiness: Boolean) {
        val intent = Intent(this, SendMessageActivity::class.java)
        intent.putExtra("isBusiness", isBusiness)
        startActivity(intent)
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
                    /* activity = */ activity,
                    /* permissions = */ arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    /* requestCode = */ PERMISSION_REQUEST_CODE
                )
                Toast.makeText(activity, "Please Grant Permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == StatusRepo.REQUEST_CODE_URI_PERMISSION && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                // Save the granted URI to SharedPreferences
                SharedPrefUtils.setPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, uri.toString())
                statusRepo.getAllStatuses()
            }
        } else {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                // Permission was denied
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun splashLogic() {
        binding.apply {
            splashScreen.cardView.slideFromStart()
            Handler(Looper.myLooper()!!).postDelayed({
                splashScreenHolder.slideToEndWithFadeOut()
                splashScreenHolder.visibility = View.GONE
                // Check if the permission is already granted
                if (ContextCompat.checkSelfPermission(activity, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                    // If not, request the permission
                    ActivityCompat.requestPermissions(activity, arrayOf("android.permission.POST_NOTIFICATIONS"), 101)
                }
            }, 2000)
        }
    }

}