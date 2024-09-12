package com.hassan.statussaver.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.hassan.statussaver.R
import com.hassan.statussaver.databinding.FragmentSettingsBinding
import com.hassan.statussaver.models.SettingsModel
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.utils.replaceFragment
import com.hassan.statussaver.views.adapters.SettingsAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSettings.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSettings : Fragment() {
    private val binding by lazy {
        FragmentSettingsBinding.inflate(layoutInflater)
    }
    private val list = ArrayList<SettingsModel>()
    private val adapter by lazy {
        SettingsAdapter(list, requireActivity())
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentWhatsapp = FragmentStatus()
                val bundle = Bundle()
                bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                replaceFragment(fragmentWhatsapp, bundle)
//                val header = findViewById<AppBarLayout>(R.id.appBarLayout)
//                header.visibility = View.VISIBLE
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.apply {
            val toolbar = binding.root.findViewById<MaterialToolbar>(R.id.tool_bar)
            toolbar.setNavigationOnClickListener {
                val fragmentWhatsapp = FragmentStatus()
                val bundle = Bundle()
                bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                replaceFragment(fragmentWhatsapp, bundle)
            }

        settingsRecyclerView.adapter = adapter

            list.add(
                SettingsModel(
                    title = "How to use",
                    desc = "Know how to download statuses"
                )
            )
            list.add(
                SettingsModel(
                    title = "Save in Folder",
                    desc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/${getString(R.string.app_name)} & " +
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString()+"/${getString(R.string.app_name)}"
                )
            )
            list.add(
                SettingsModel(
                    title = "Disclaimer",
                    desc = "Read Our Disclaimer"
                )
            )
            list.add(
                SettingsModel(
                    title = "Privacy Policy",
                    desc = "Read Our Terms & Conditions"
                )
            )
//            list.add(
//                SettingsModel(
//                    title = "Share",
//                    desc = "Sharing is caring"
//                )
//            )
//            list.add(
//                SettingsModel(
//                    title = "Rate Us",
//                    desc = "Please support our work by rating on PlayStore"
//                )
//            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root
    fun replaceFragment(fragment: Fragment, args: Bundle? = null) {
        val fragmentActivity = requireActivity() as FragmentActivity
        fragmentActivity.supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            args?.let {
                fragment.arguments = it
            }
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null)
        }.commit()
    }
}