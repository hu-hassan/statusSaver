package com.hassan.statussaver.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.hassan.statussaver.R
import com.hassan.statussaver.databinding.ActivitySplashBinding
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.utils.SharedPrefKeys
import com.hassan.statussaver.utils.SharedPrefUtils
import com.hassan.statussaver.utils.getFolderPermissions
import com.hassan.statussaver.viewmodels.factories.StatusViewModel
import kotlin.properties.Delegates

class SplashScreen : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    var isFirstRun1 : Boolean = true
    val WHATSAPP_REQUEST_CODE = 101
    var isWmSAvailable: Boolean = true
    var isWbSAvailable: Boolean = true
    private var loaderStarted = false // Flag to track if loader has started
    private var mainActivityLaunched = false
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Initialize SharedPrefUtils
        Log.d("SplashScreen", "onCreate: Initializing SharedPrefUtils")
        SharedPrefUtils.init(this@SplashScreen)
        if(isFirstRun()){
            Log.d("SplashScreen", "Clearing prefrences")
            Log.d("isFirstRun()", "${isFirstRun()}")
            SharedPrefUtils.clearPreferences()
            val sharedPreferences = getSharedPreferences("app_prefs1", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
        }
        requestPermission()


        binding.apply {
            val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                false
            )
            if (!isPermissionGranted) {
                permissionBtn.visibility = View.VISIBLE
                permissionBtn.setOnClickListener {
                    val dialogView = LayoutInflater.from(this@SplashScreen).inflate(R.layout.dialog_permission, null)
                    val dialog = Dialog(this@SplashScreen)
                    dialog.setContentView(dialogView)
                    dialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    dialog.show()
                    val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn1)
                    dialogButton.setOnClickListener {
                        Log.d("Requesting permission of", "${Constants.WHATSAPP_PARENT_URI})")

                        // Perform the desired task here
                        getFolderPermissions(
                            context = this@SplashScreen,
                            REQUEST_CODE = WHATSAPP_REQUEST_CODE,
                            initialUri = Constants.getWhatsappUri()
                        )
                        dialog.dismiss()
                    }
                    val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            } else {
                isWSExist()
                Log.d("SplashScreen", "isWmSAvailable: $isWmSAvailable")
                Log.d("SplashScreen", "isWbSAvailable: $isWbSAvailable")
                if (!isWmSAvailable) {
                    Log.d("SplashScreen omCreate", "Whatsapp Status Folder not found")
                    loadBar.visibility = View.GONE
                    permissionBtn.visibility = View.VISIBLE
                    permissionBtn.text = "View Status in Whatsapp"
                    permissionBtn.setOnClickListener{
                        val packageName = "com.whatsapp"
                        var intent =
                            this@SplashScreen.packageManager.getLaunchIntentForPackage(packageName)
                        startActivity(intent)
                    }
                    Toast.makeText(this@SplashScreen, "Please view statuses in Whatsapp", Toast.LENGTH_SHORT).show()

                } else if (!isWbSAvailable) {
                    loadBar.visibility = View.GONE
                    permissionBtn.visibility = View.VISIBLE
                    permissionBtn.text = "View Status in Whatsapp Business"
                    permissionBtn.setOnClickListener{
                        val packageName = "com.whatsapp.w4b"
                        var intent =
                            this@SplashScreen.packageManager.getLaunchIntentForPackage(packageName)
                        startActivity(intent)
                    }
                    Toast.makeText(this@SplashScreen, "Please view statuses in Whatsapp Business", Toast.LENGTH_SHORT).show()
                } else {
                    if (!loaderStarted && !mainActivityLaunched) {
                        loaderStarted = true
                        permissionBtn.visibility = View.GONE
                        loadBar.indeterminateDrawable.setColorFilter(
                            ContextCompat.getColor(this@SplashScreen, R.color.progressBar),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                        loadBar.visibility = View.VISIBLE

                        val handler = Handler(Looper.getMainLooper())
                        val delayMillis: Long = 2000 // 3 seconds delay
                        val intervalMillis: Int = 100 // Update progress every 100ms

                        val runnable = object : Runnable {
                            var progress = 0
                            override fun run() {
                                if (progress < delayMillis) {
                                    loadBar.progress = (progress * 100 / delayMillis).toInt()
                                    progress += intervalMillis
                                    handler.postDelayed(this, intervalMillis.toLong())
                                } else if (!mainActivityLaunched) { // Double-check flag before launching
                                    mainActivityLaunched = true
                                    val bundle = Bundle().apply {
                                        putString(
                                            Constants.FRAGMENT_TYPE_KEY,
                                            Constants.TYPE_WHATSAPP_MAIN
                                        )
                                    }

                                    val intent =
                                        Intent(this@SplashScreen, MainActivity::class.java).apply {
                                            putExtras(bundle)
                                        }
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                        handler.post(runnable)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        binding.apply {
            val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                false
            )
            if (!isPermissionGranted) {
                permissionBtn.visibility = View.VISIBLE
                permissionBtn.setOnClickListener {
                    val dialogView = LayoutInflater.from(this@SplashScreen).inflate(R.layout.dialog_permission, null)
                    val dialog = Dialog(this@SplashScreen)
                    dialog.setContentView(dialogView)
                    dialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    dialog.show()
                    val dialogButton = dialogView.findViewById<Button>(R.id.okay_btn1)
                    dialogButton.setOnClickListener {
                        Log.d("Requesting permission of", "${Constants.WHATSAPP_PARENT_URI})")

                        // Perform the desired task here
                        getFolderPermissions(
                            context = this@SplashScreen,
                            REQUEST_CODE = WHATSAPP_REQUEST_CODE,
                            initialUri = Constants.getWhatsappUri()
                        )
                        dialog.dismiss()
                    }
                    val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
                    cancelButton.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            } else {
                isWSExist()
                Log.d("SplashScreen", "isWmSAvailable: $isWmSAvailable")
                Log.d("SplashScreen", "isWbSAvailable: $isWbSAvailable")
                if (!isWmSAvailable) {
                    Log.d("SplashScreen omCreate", "Whatsapp Status Folder not found")
                    loadBar.visibility = View.GONE
                    permissionBtn.visibility = View.VISIBLE
                    permissionBtn.text = "View Status in Whatsapp"
                    permissionBtn.setOnClickListener{
                        val packageName = "com.whatsapp"
                        var intent =
                            this@SplashScreen.packageManager.getLaunchIntentForPackage(packageName)
                        startActivity(intent)
                    }
                    Toast.makeText(this@SplashScreen, "Please view statuses in Whatsapp", Toast.LENGTH_SHORT).show()

                } else if (!isWbSAvailable) {
                    loadBar.visibility = View.GONE
                    permissionBtn.visibility = View.VISIBLE
                    permissionBtn.text = "View Status in Whatsapp Business"
                    permissionBtn.setOnClickListener{
                        val packageName = "com.whatsapp.w4b"
                        var intent =
                            this@SplashScreen.packageManager.getLaunchIntentForPackage(packageName)
                        startActivity(intent)
                    }
                    Toast.makeText(this@SplashScreen, "Please view statuses in Whatsapp Business", Toast.LENGTH_SHORT).show()
                } else {
                    if (!loaderStarted && !mainActivityLaunched) {
                        loaderStarted = true
                        permissionBtn.visibility = View.GONE
                        loadBar.indeterminateDrawable.setColorFilter(
                            ContextCompat.getColor(this@SplashScreen, R.color.progressBar),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                        loadBar.visibility = View.VISIBLE

                        val handler = Handler(Looper.getMainLooper())
                        val delayMillis: Long = 2000 // 3 seconds delay
                        val intervalMillis: Int = 100 // Update progress every 100ms

                        val runnable = object : Runnable {
                            var progress = 0
                            override fun run() {
                                if (progress < delayMillis) {
                                    loadBar.progress = (progress * 100 / delayMillis).toInt()
                                    progress += intervalMillis
                                    handler.postDelayed(this, intervalMillis.toLong())
                                } else if (!mainActivityLaunched) { // Double-check flag before launching
                                    mainActivityLaunched = true
                                    val bundle = Bundle().apply {
                                        putString(
                                            Constants.FRAGMENT_TYPE_KEY,
                                            Constants.TYPE_WHATSAPP_MAIN
                                        )
                                    }

                                    val intent =
                                        Intent(this@SplashScreen, MainActivity::class.java).apply {
                                            putExtras(bundle)
                                        }
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                        handler.post(runnable)
                    }
                }
            }
        }
    }
    class AppSession {
        companion object {
            var isFirstRun = true
        }
    }

    // Usage
    private fun isFirstRun(): Boolean {
        val sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isFirstRun", true)
    }

    private fun setFirstRunCompleted() {
        val sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isFirstRun", false)
            apply()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            val treeUri = data?.data!!
            this.contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            if (requestCode == WHATSAPP_REQUEST_CODE) {
                // Save the permission state
                SharedPrefUtils.setPrefString(
                    SharedPrefKeys.PREF_KEY_WP_TREE_URI,
                    treeUri.toString()
                )
                SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, true)
            }

            val treeUriSegments = treeUri.pathSegments
            val desiredUriSegments = Constants.getWhatsappUri().pathSegments

            val treeUriSegment = treeUriSegments.getOrNull(treeUriSegments.indexOf("tree") + 1)
            val desiredUriSegment = desiredUriSegments.getOrNull(desiredUriSegments.indexOf("document") + 1)

            if (treeUriSegment != desiredUriSegment) {
                Log.d("PermissionUtils", "onActivityResult: Requesting folder permissions $treeUri")
                Log.d("PermissionUtils", "onActivityResult: Folder Permission must be ${Constants.getWhatsappUri()}")
                Toast.makeText(this@SplashScreen, "Please grant permission of right folder", Toast.LENGTH_SHORT).show()
                SharedPrefUtils.clearPreferences()
            }
            isWSExist()

        }
    }
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
//            Log.d(TAG, "isAppInstalled: App Installed")
            true
        } catch (e: PackageManager.NameNotFoundException) {
//            Log.d(TAG, "isAppInstalled: App Not Installed")
            false
        }
    }
    fun isWSExist(){
        val treeUriString = SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")
        val treeUri = Uri.parse(treeUriString)
        val documentFile = DocumentFile.fromTreeUri(this, treeUri)
        Log.d("SplashScreen", "isWSExist called")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val wmPath = documentFile?.findFile("com.whatsapp")?.findFile("WhatsApp")?.findFile("Media")
                ?.findFile(".Statuses")

            if(isAppInstalled(this@SplashScreen,"com.whatsapp") && (wmPath == null || !wmPath.exists())){
                    Log.d("SplashScreen", "isWSExist: Setting isWmSAvailable to false")
                isWmSAvailable = false
                Log.d("SplashScreen", "isWmSAvailable: ${isWmSAvailable}")

            }
            else{
                Log.d("SplashScreen", "isWSExist: Whatsapp Status Folder found")
                isWmSAvailable = true
            }
            val wbPath = documentFile?.findFile("com.whatsapp.w4b")?.findFile("WhatsApp Business")
                ?.findFile("Media")?.findFile(".Statuses")
            if(isAppInstalled(this@SplashScreen,"com.whatsapp.w4") && (wbPath == null || !wbPath.exists())){
                isWbSAvailable = false
            }
            else{
                isWbSAvailable = true
            }
        }
        else{
            val wmPath2 = documentFile?.findFile("WhatsApp")?.findFile("Media")
                ?.findFile(".Statuses")
            val wmPath = documentFile?.findFile("Android")?.findFile("media")?.findFile("com.whatsapp")
                ?.findFile("WhatsApp")?.findFile("Media")?.findFile(".Statuses")
            if(isAppInstalled(this@SplashScreen,"com.whatsapp") && (wmPath == null || !wmPath.exists())){

            }
                if(isAppInstalled(this@SplashScreen,"com.whatsapp") && (wmPath == null || !wmPath.exists())){
                    if((wmPath2 == null || !wmPath2.exists())){
                        isFirstRun1 = true
                        isWmSAvailable = false
                    }
                    else{
                        isWmSAvailable = true
                    }
            } else{

                    isWmSAvailable = true
            }
            val wbPath2 = documentFile?.findFile("WhatsApp")?.findFile("Media")
                ?.findFile(".Statuses")
            val wbPath = documentFile?.findFile("WhatsApp Business")
                ?.findFile("Media")?.findFile(".Statuses")
            if(isAppInstalled(this@SplashScreen,"com.whatsapp.w4b") && (wbPath == null || !wbPath.exists())){
                if((wbPath2 == null || !wbPath2.exists())){
//                    isFirstRun1 = true
                    isWbSAvailable = false
                }
                else{
                    isWbSAvailable = true
                }

            }
            else
            {
                isWbSAvailable = true
            }

        }
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

            }
        }
    }

}