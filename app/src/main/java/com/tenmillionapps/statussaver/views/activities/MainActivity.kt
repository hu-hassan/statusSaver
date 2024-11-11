package com.tenmillionapps.statussaver.views.activities


import BaseActivity
import android.Manifest
import android.app.ActionBar
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.FileObserver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.google.android.material.appbar.AppBarLayout
import com.tenmillionapps.statussaver.R
import com.tenmillionapps.statussaver.data.StatusRepo
import com.tenmillionapps.statussaver.databinding.ActivityMainBinding
import com.tenmillionapps.statussaver.databinding.DialogGuideBinding
import com.tenmillionapps.statussaver.services.FileObserverService
import com.tenmillionapps.statussaver.utils.Constants
import com.tenmillionapps.statussaver.utils.SharedPrefKeys
import com.tenmillionapps.statussaver.utils.SharedPrefUtils
import com.tenmillionapps.statussaver.utils.replaceFragment
import com.tenmillionapps.statussaver.views.fragments.FragmentStatus
import java.io.File


class MainActivity : BaseActivity() {
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
  var isBtnPressed = 0

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {

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
//      supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
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
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_service, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setLayout(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
        val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn)
        dialogButton.setOnClickListener {
          isBtnPressed = 1
          getNotificationpermission()
          if((ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED)) {
            WorkManager.getInstance(this).cancelAllWork()
            val intent = Intent(this, FileObserverService::class.java)
            stopService(intent)
            val serviceIntent = Intent(this, FileObserverService::class.java)
            this.startForegroundService(serviceIntent)
            notification_btn.visibility = View.GONE
          }
          dialog.dismiss()
        }

        val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
        cancelButton.setOnClickListener {
          dialog.dismiss()
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
//          SharedPrefUtils.clearPreferences()
          Log.d("MainActivity", "First run")
//          clearAppSettings()
          dialogLogic()
        }
        requestPermission()
        val fragmentWhatsapp = FragmentStatus()
        val bundle = Bundle()
        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
        replaceFragment(fragmentWhatsapp, bundle)
        findViewById<TextView>(R.id.item1).setOnClickListener {
          performAction()
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
          performAction()
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
        val intent = Intent(this, Settings::class.java)
        intent.putExtra("isBusiness", isBusiness)
        startActivity(intent)
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

  private val PERMISSION_REQUEST_CODE1 = 50
  private val PERMISSION_REQUEST_CODE2 = 52
  private val PERMISSION_REQUEST_CODE3 = 51
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
          /* requestCode = */ PERMISSION_REQUEST_CODE1
        )
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE2)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE3)

//        Toast.makeText(activity, "Please Grant Permissions", Toast.LENGTH_SHORT).show()
      }
    }
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == StatusRepo.REQUEST_CODE_URI_PERMISSION && resultCode == RESULT_OK) {
      data?.data?.let { uri ->
        contentResolver.takePersistableUriPermission(
          uri,
          Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        // Save the granted URI to SharedPreferences
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

        }
        else{
          SharedPrefUtils.setPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, uri.toString())
          statusRepo.getAllStatuses()
        }

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
    if (requestCode == PERMISSION_REQUEST_CODE1) {
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

  private fun isFirstRun(): Boolean {
    val sharedPreferences = getSharedPreferences("app_prefs1", MODE_PRIVATE)
    return sharedPreferences.getBoolean("isFirstRun", true)
  }

  private fun setFirstRunCompleted() {
    val sharedPreferences = getSharedPreferences("app_prefs1", MODE_PRIVATE)
    with(sharedPreferences.edit()) {
      putBoolean("isFirstRun", false)
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
  private fun clearAppSettings() {
    // Clear shared preferences or other data
    val sharedPreferencesDir = File(applicationContext.filesDir.parentFile, "shared_prefs")
    if (sharedPreferencesDir.exists() && sharedPreferencesDir.isDirectory) {
      val files = sharedPreferencesDir.listFiles()
      if (files != null) {
        for (file in files) {
          file.delete()
        }
      }
    }
  }
  @RequiresApi(Build.VERSION_CODES.O)
  override fun onResume() {
    super.onResume()
    val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
      SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
      false
    ) || SharedPrefUtils.getPrefBoolean(
      SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
      false
    )

    val notificationBtn = findViewById<ImageButton>(R.id.notification_icon)
    if (ContextCompat.checkSelfPermission(
        this,
        "android.permission.POST_NOTIFICATIONS"
      ) == PackageManager.PERMISSION_DENIED || !isFileObserverServiceRunning()
    ) {
      Log.d("MainActivity", "Notification permission not granted")
      val intent = Intent(this, FileObserverService::class.java)
      stopService(intent)
      notificationBtn.visibility = View.VISIBLE
    }
    if (!isPermissionGranted || (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED && isFileObserverServiceRunning())) {
      Log.d("MainActivity onResume", "Permission not granted")
      notificationBtn.visibility = View.GONE
    }
    if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
      Log.d("BtnPress", "${isBtnPressed}")
      if (!isFileObserverServiceRunning()) {
        Log.d("isFileObserverServiceRunning", "${isFileObserverServiceRunning()}")
        if (isPermissionGranted) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("isFileObserverServiceRunning", "${isFileObserverServiceRunning()}")

            if(isBtnPressed == 1) {
              WorkManager.getInstance(this).cancelAllWork()
              val intent = Intent(this, FileObserverService::class.java)
              stopService(intent)
              val serviceIntent = Intent(this, FileObserverService::class.java)
              this.startForegroundService(serviceIntent)
              notificationBtn.visibility = View.GONE
            }
          }
        }
      }
    }

  }


}
