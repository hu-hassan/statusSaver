package com.hassan.statussaver.views.activities


import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.FileObserver
import android.os.StrictMode
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.afollestad.materialdialogs.BuildConfig
import com.hassan.statussaver.R
import com.hassan.statussaver.data.StatusRepo
import com.hassan.statussaver.databinding.ActivityMainBinding
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.utils.SharedPrefKeys
import com.hassan.statussaver.utils.SharedPrefUtils
import com.hassan.statussaver.utils.replaceFragment
import com.hassan.statussaver.views.fragments.FragmentSettings
import com.hassan.statussaver.views.fragments.FragmentStatus
import com.hassan.statussaver.workers.RestartServiceWorker
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hassan.statussaver.databinding.DialogGuideBinding
import java.util.concurrent.TimeUnit
import androidx.work.*
import com.google.common.util.concurrent.ListenableFuture
import com.hassan.statussaver.databinding.ItemSettingsBinding
import com.hassan.statussaver.services.FileObserverService
import com.hassan.statussaver.views.adapters.SettingsAdapter


class MainActivity : AppCompatActivity() {
  private val activity = this
  private val binding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
  }
  private var currentSelectedItemId: Int = R.id.menu_status // Default selected item
  private lateinit var statusRepo: StatusRepo
  private var fileObserver: FileObserver? = null
  private var isBusiness: Boolean = false
  private lateinit var bottomSheet: LinearLayout
  private lateinit var grayShade: View
  private lateinit var settingsAdapter: SettingsAdapter

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    settingsAdapter = SettingsAdapter(arrayListOf(), this)

    SharedPrefUtils.init(activity) // Ensure this is called early
    val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
      SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
      false
    ) || SharedPrefUtils.getPrefBoolean(
      SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
      false
    )

    if (!isPermissionGranted){
      Log.d("MainActivity", "Permissions not granted")
      WorkManager.getInstance(this).cancelAllWork()
    }
    super.onCreate(savedInstanceState)
    try {
      setContentView(binding.root)
      supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
      SharedPrefUtils.init(activity)
      statusRepo = StatusRepo(this)
      bottomSheet = findViewById(R.id.bottomSheet)
      grayShade = findViewById(R.id.gray_shade)
      grayShade.setOnClickListener {
        bottomSheet.visibility = View.GONE
        grayShade.visibility = View.GONE
      }
      val buttonIcon = findViewById<ImageButton>(R.id.button_icon)
      val buttonIcon2 = findViewById<ImageButton>(R.id.button_icon2)
      val settingIcon = findViewById<ImageButton>(R.id.settings_icon)
      val text = findViewById<TextView>(R.id.toolbar_title)
      val header = findViewById<AppBarLayout>(R.id.appBarLayout)
      val notification_btn = findViewById<ImageButton>(R.id.notification_icon)
      val cancelBtn = findViewById<ImageView>(R.id.cancel_btn)
      cancelBtn.setOnClickListener {
          bottomSheet.visibility = View.GONE
          grayShade.visibility = View.GONE
      }
      if(isPermissionGranted== false){
        Log.d("MainActivity", "Permission not granted")
        notification_btn.visibility = View.GONE
      }
      if (isFileObserverServiceRunning() && ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
        Log.d("MainActivity", "Permission granted for service")
        // Your code here
        notification_btn.visibility = View.GONE
      }
      notification_btn.setOnClickListener {
        getNotificationpermission()
        if(!(isFileObserverServiceRunning())&& (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED)) {
          WorkManager.getInstance(this).cancelAllWork()
          val workRequest = PeriodicWorkRequestBuilder<RestartServiceWorker>(0, TimeUnit.MINUTES)
            .build()
          WorkManager.getInstance(this).enqueue(workRequest)
        }
        notification_btn.visibility = View.GONE
        if (isFileObserverServiceRunning() == false|| ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_DENIED) {
          notification_btn.visibility = View.VISIBLE
        }
      }
      binding.apply {
        buttonIcon2.setOnClickListener {
          if (bottomSheet.visibility == View.GONE) {
            bottomSheet.visibility = View.VISIBLE
            grayShade.visibility = View.VISIBLE
          } else {
            bottomSheet.visibility = View.GONE
            grayShade.visibility = View.GONE
          }
        }
        if (isFirstRun()) {
          dialogLogic()
        }
        requestPermission()
        val fragmentWhatsapp = FragmentStatus()
        val bundle = Bundle()
        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
        replaceFragment(fragmentWhatsapp, bundle)
        findViewById<TextView>(R.id.item1).setOnClickListener {
          grayShade.visibility = View.GONE
          buttonIcon?.visibility = View.VISIBLE
          text?.visibility = View.VISIBLE
          val fragmentWhatsapp = FragmentStatus()
          val bundle = Bundle()
          bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
          replaceFragment(fragmentWhatsapp, bundle)
          isBusiness = false
          bottomSheet.visibility = View.GONE

        }
        findViewById<TextView>(R.id.item2).setOnClickListener {
          grayShade.visibility = View.GONE
          buttonIcon?.visibility = View.VISIBLE
          text?.visibility = View.VISIBLE
          val fragmentWhatsapp = FragmentStatus()
          val bundle = Bundle()
          bundle.putString(
            Constants.FRAGMENT_TYPE_KEY,
            Constants.TYPE_WHATSAPP_BUSINESS
          )
          replaceFragment(fragmentWhatsapp, bundle)
          isBusiness = true
          bottomSheet.visibility = View.GONE
        }
      }

      // Find the ImageButton and set an OnClickListener
      buttonIcon?.setOnClickListener {
        openSendMessageActivity(isBusiness)

      }
      settingIcon?.setOnClickListener {
        header.visibility = View.GONE
        replaceFragment(FragmentSettings())
      }
    }
    catch (e: Exception) {
      Log.e("TAG", "Exception in onCreate", e)
      throw e
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
    if (requestCode == StatusRepo.REQUEST_CODE_URI_PERMISSION && resultCode == RESULT_OK) {
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

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
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
  private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
      super.onFragmentResumed(fm, f)
      if (f is FragmentStatus) {
        findViewById<AppBarLayout>(R.id.appBarLayout).visibility = View.VISIBLE
      }
    }
  }


  private fun isFirstRun(): Boolean {
    val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
    return sharedPreferences.getBoolean("is_first_run", true)
  }

  private fun setFirstRunCompleted() {
    val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
    with(sharedPreferences.edit()) {
      putBoolean("is_first_run", false)
      apply()
    }
  }
  private fun getNotificationpermission(){
    if (ContextCompat.checkSelfPermission(
        activity,
        "android.permission.POST_NOTIFICATIONS"
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      // If not, request the permission
      ActivityCompat.requestPermissions(
        activity,
        arrayOf("android.permission.POST_NOTIFICATIONS"),
        101
      )
    }
  }

  private fun dialogLogic() {
    val dialog = Dialog(this)
    val dialogBinding = DialogGuideBinding.inflate((this as Activity).layoutInflater)
    dialogBinding.okayBtn.setOnClickListener {
      dialog.dismiss()
    }
    dialog.setContentView(dialogBinding.root)

    dialog.window?.setLayout(
      ActionBar.LayoutParams.MATCH_PARENT,
      ActionBar.LayoutParams.WRAP_CONTENT
    )

    dialog.show()
    setFirstRunCompleted()
    val cancelBtn = dialog.findViewById<ImageView>(R.id.cancel_btn)
    cancelBtn.setOnClickListener {
      dialog.dismiss()
    }
  }
  private fun isFileObserverServiceRunning(): Boolean {
    val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
      if (FileObserverService::class.java.name == service.service.className) {
        return true
      }
    }
    return false
  }

  override fun onResume() {
    super.onResume()
    val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
      SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
      false
    ) || SharedPrefUtils.getPrefBoolean(
      SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
      false
    )

    val notification_btn = findViewById<ImageButton>(R.id.notification_icon)
    if (!isFileObserverServiceRunning() || ContextCompat.checkSelfPermission(
        this,
        "android.permission.POST_NOTIFICATIONS"
      ) == PackageManager.PERMISSION_DENIED
    ) {
      Log.d("MainActivity", "Permission granted for service")
      notification_btn.visibility = View.VISIBLE
    }

    if (!isPermissionGranted) {
      Log.d("MainActivity onResume", "Permission not granted")
      notification_btn.visibility = View.GONE
    }
  }


}
