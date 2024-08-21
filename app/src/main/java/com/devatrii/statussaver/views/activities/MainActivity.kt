package com.devatrii.statussaver.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.devatrii.statussaver.R
import com.devatrii.statussaver.databinding.ActivityMainBinding
import com.devatrii.statussaver.utils.Constants
import com.devatrii.statussaver.utils.SharedPrefKeys
import com.devatrii.statussaver.utils.SharedPrefUtils
import com.devatrii.statussaver.utils.replaceFragment
import com.devatrii.statussaver.utils.slideFromStart
import com.devatrii.statussaver.utils.slideToEndWithFadeOut
import com.devatrii.statussaver.views.fragments.FragmentSettings
import com.devatrii.statussaver.views.fragments.FragmentStatus

class MainActivity : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var currentSelectedItemId: Int = R.id.menu_status // Default selected item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SharedPrefUtils.init(activity)
        binding.apply {
            splashLogic()
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
                        val fragmentWhatsapp = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                        replaceFragment(fragmentWhatsapp, bundle)
                    }
                    R.id.menu_business_status -> {
                        val fragmentWhatsapp = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_BUSINESS)
                        replaceFragment(fragmentWhatsapp, bundle)
                    }
                    R.id.menu_settings -> {
                        replaceFragment(FragmentSettings())
                    }
                }
                return@setOnItemSelectedListener true
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

    }
//    override fun onStart() {
//        super.onStart()
//
//        // Check if the permission is already granted
//        if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
//            // If not, request the permission
//            ActivityCompat.requestPermissions(this, arrayOf("android.permission.POST_NOTIFICATIONS"), 101)
//        }
//    }

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