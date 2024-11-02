package com.hassan.statussaver.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.hassan.statussaver.R
import com.hassan.statussaver.data.StatusRepo
import com.hassan.statussaver.databinding.FragmentMediaBinding
import com.hassan.statussaver.models.MediaModel
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.utils.SharedPrefKeys
import com.hassan.statussaver.utils.SharedPrefUtils
import com.hassan.statussaver.utils.isStatusExist
import com.hassan.statussaver.utils.isStatusExistInBStatuses
import com.hassan.statussaver.utils.isStatusExistInStatuses
import com.hassan.statussaver.utils.isStatusSaved
import com.hassan.statussaver.viewmodels.factories.StatusViewModel
import com.hassan.statussaver.viewmodels.factories.StatusViewModelFactory
import com.hassan.statussaver.views.adapters.MediaAdapter
import java.util.concurrent.CopyOnWriteArrayList

class FragmentMedia : Fragment() {
    private val binding by lazy {
        FragmentMediaBinding.inflate(layoutInflater)
    }
    lateinit var viewModel: StatusViewModel
    lateinit var adapter: MediaAdapter
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            arguments?.let {
                val repo = StatusRepo(requireActivity())
                viewModel = ViewModelProvider(
                    requireActivity(),
                    StatusViewModelFactory(repo)
                )[StatusViewModel::class.java]
                val mediaType = it.getString(Constants.MEDIA_TYPE_KEY, "")
                when (mediaType) {
                    Constants.MEDIA_TYPE_WHATSAPP_IMAGES -> {
                        binding.tempMediaText.visibility = View.GONE
                        binding.tempMediaText2.visibility = View.GONE
                        binding.tempMediaBtn.visibility = View.GONE
                        viewModel.whatsAppImagesLiveData.observe(this@FragmentMedia) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                        binding.tempMediaBtn.setOnClickListener {
                            val packageName = "com.whatsapp"
                            var intent =
                                requireActivity().packageManager.getLaunchIntentForPackage(packageName)
                            startActivity(intent)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_VIDEOS -> {
                        binding.tempMediaText.visibility = View.GONE
                        binding.tempMediaText2.visibility = View.GONE
                        binding.tempMediaBtn.visibility = View.GONE
                        viewModel.whatsAppVideosLiveData.observe(this@FragmentMedia) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                        binding.tempMediaBtn.setOnClickListener {
                            val packageName = "com.whatsapp"
                            var intent =
                                requireActivity().packageManager.getLaunchIntentForPackage(packageName)
                            startActivity(intent)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES -> {
                        binding.tempMediaText.visibility = View.GONE
                        binding.tempMediaText2.visibility = View.GONE
                        binding.tempMediaBtn.visibility = View.GONE
                        viewModel.whatsAppBusinessImagesLiveData.observe(requireActivity()) { unFilteredList ->
                            Log.d("FragmentMedia", "Whatsapp Business update ui calling")
                            Log.d("FragmentMedia", "Whatsapp Business size given to update ui ${unFilteredList.size}")
                            updateUIB(unFilteredList)
                        }

                        binding.tempMediaBtn.icon = resources.getDrawable(R.drawable.whatsapp_business)
                        binding.tempMediaBtn.setOnClickListener {
                            val packageName = "com.whatsapp.w4b"
                            var intent =
                                requireActivity().packageManager.getLaunchIntentForPackage(packageName)
                            startActivity(intent)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS -> {
                        binding.tempMediaText.visibility = View.GONE
                        binding.tempMediaText2.visibility = View.GONE
                        binding.tempMediaBtn.visibility = View.GONE
                        viewModel.whatsAppBusinessVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            Log.d("FragmentMedia", "Whatsapp Business update ui calling")
                            Log.d("FragmentMedia", "Whatsapp Business size given to update ui ${unFilteredList.size}")
                            updateUIB(unFilteredList)
                        }

                        binding.tempMediaBtn.icon = resources.getDrawable(R.drawable.whatsapp_business)
                        binding.tempMediaBtn.setOnClickListener {
                            val packageName = "com.whatsapp.w4b"
                            var intent =
                                requireActivity().packageManager.getLaunchIntentForPackage(packageName)
                            startActivity(intent)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_SAVED-> {
                        viewModel.wpSavedStatusLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUIforSaved(unFilteredList)
                        }
                        tempMediaText.text = "No Media Saved"
                        binding.tempMediaText2.visibility = View.GONE
                        binding.tempMediaBtn.visibility = View.GONE
                    }
                }
                val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                    false
                ) && SharedPrefUtils.getPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                    false
                )
                if (!isPermissionGranted){
                    binding.tempMediaText.visibility = View.GONE
                    binding.tempMediaText2.visibility = View.GONE
                    binding.tempMediaBtn.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUI(unFilteredList: ArrayList<MediaModel>) {
        // Create a copy of the unfiltered list to avoid concurrent modification issues
        val filteredList = ArrayList(unFilteredList).distinctBy { model -> model.fileName }

        // Use a thread-safe collection for modifications
        val list = CopyOnWriteArrayList<MediaModel>()

        // Iterate over the filtered list and add/remove elements based on conditions
        filteredList.forEach { model ->
            if (isStatusExistInStatuses(model.fileName) || isStatusExistInBStatuses(model.fileName)) {
                Log.d("FragmentMedia", "updateUI: ${model.fileName}")
                list.add(model)
            } else {
                list.remove(model)
            }
        }
        var activity = FragmentActivity();
        if (isAdded) {
            // Your existing code that requires the fragment to be attached to an activity
            activity = requireActivity()
            // ... rest of your code
        } else {
            Log.w("FragmentMedia", "Fragment is not attached to an activity")
        }
        // Update the adapter with the final list
        adapter = MediaAdapter(ArrayList(list), activity, false)
        binding.mediaRecyclerView.adapter = adapter

        // Show or hide the temporary text based on whether the final list is empty
        binding.tempMediaText.visibility = if (list.isEmpty() && isAppInstalled(requireContext(),"com.whatsapp")) View.VISIBLE else View.GONE
        binding.tempMediaText2.visibility = if (list.isEmpty() && isAppInstalled(requireContext(),"com.whatsapp")) View.VISIBLE else View.GONE
        binding.tempMediaBtn.visibility = if (list.isEmpty() && isAppInstalled(requireContext(),"com.whatsapp")) View.VISIBLE else View.GONE
    }
    private fun updateUIB(unFilteredList: ArrayList<MediaModel>) {
        // Create a copy of the unfiltered list to avoid concurrent modification issues
        val filteredList = ArrayList(unFilteredList).distinctBy { model -> model.fileName }

        // Use a thread-safe collection for modifications
        val list = CopyOnWriteArrayList<MediaModel>()

        // Iterate over the filtered list and add/remove elements based on conditions
        filteredList.forEach { model ->
            if (isStatusExistInBStatuses(model.fileName)) {
                Log.d("FragmentMedia", "updateUI: ${model.fileName}")
                list.add(model)
            } else {
                list.remove(model)
            }
        }
        var activity = FragmentActivity();
        if (isAdded) {
            // Your existing code that requires the fragment to be attached to an activity
            activity = requireActivity()
            // ... rest of your code
        } else {
            Log.w("FragmentMedia", "Fragment is not attached to an activity")
        }
        // Update the adapter with the final list
        adapter = MediaAdapter(ArrayList(list), activity, false)
        binding.mediaRecyclerView.adapter = adapter

        // Show or hide the temporary text based on whether the final list is empty
        binding.tempMediaText.visibility = if (list.isEmpty() && isAppInstalled(requireContext(),"com.whatsapp.w4b")) View.VISIBLE else View.GONE
        binding.tempMediaText2.visibility = if (list.isEmpty() && isAppInstalled(requireContext(),"com.whatsapp.w4b")) View.VISIBLE else View.GONE
        binding.tempMediaBtn.visibility = if (list.isEmpty() && isAppInstalled(requireContext(),"com.whatsapp.w4b")) View.VISIBLE else View.GONE
    }

    private fun updateUIforSaved(unFilteredList: ArrayList<MediaModel>) {
        // Create a copy of the unfiltered list to avoid concurrent modification issues
        val filteredList = ArrayList(unFilteredList).distinctBy { model -> model.fileName }

        // Use a thread-safe collection for modifications
        val list = CopyOnWriteArrayList<MediaModel>()

        // Iterate over the filtered list and add/remove elements based on conditions
        filteredList.forEach { model ->
            if (context?.isStatusExist(model.fileName) == true || context?.isStatusSaved(model.fileName) == true) {
                list.add(model)
            } else {
                list.remove(model)
            }
        }
        var activity = FragmentActivity();
        if (isAdded) {
            // Your existing code that requires the fragment to be attached to an activity
             activity = requireActivity()
            // ... rest of your code
        } else {
            Log.w("FragmentMedia", "Fragment is not attached to an activity")
        }
        // Update the adapter with the final list
        adapter = MediaAdapter(ArrayList(list), activity, true)
        binding.mediaRecyclerView.adapter = adapter

        // Show or hide the temporary text based on whether the final list is empty
        binding.tempMediaText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root
    override fun onResume() {
        super.onResume()
        binding.tempMediaText.visibility = View.GONE
        binding.tempMediaText2.visibility = View.GONE
        binding.tempMediaBtn.visibility = View.GONE

        binding.apply {
            arguments?.let {
                val repo = StatusRepo(requireActivity())
                viewModel = ViewModelProvider(
                    requireActivity(),
                    StatusViewModelFactory(repo)
                )[StatusViewModel::class.java]
                val mediaType = it.getString(Constants.MEDIA_TYPE_KEY, "")
                when (mediaType) {
                    Constants.MEDIA_TYPE_WHATSAPP_IMAGES -> {
                        viewModel.whatsAppImagesLiveData.observe(viewLifecycleOwner) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_VIDEOS -> {
                        viewModel.whatsAppVideosLiveData.observe(viewLifecycleOwner) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES -> {

                        viewModel.whatsAppBusinessImagesLiveData.observe(viewLifecycleOwner) { unFilteredList ->
                            Log.d("FragmentMedia", "Whatsapp Business update ui calling")
                            Log.d("FragmentMedia", "Whatsapp Business size given to update ui ${unFilteredList.size}")
                            updateUIB(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS -> {
                        viewModel.whatsAppBusinessVideosLiveData.observe(viewLifecycleOwner) { unFilteredList ->
                            Log.d("FragmentMedia", "Whatsapp Business update ui calling")
                            Log.d("FragmentMedia", "Whatsapp Business size given to update ui ${unFilteredList.size}")
                            updateUIB(unFilteredList)
                        }
                    }
                        Constants.MEDIA_TYPE_WHATSAPP_SAVED -> {
                        viewModel.wpSavedStatusLiveData.observe(viewLifecycleOwner) { unFilteredList ->
                            updateUIforSaved(unFilteredList)
                        }
                    }
                }
            }
        }
    }
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            Log.d("FragmentMedia", "isAppInstalled: App Installed")
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("FragmentMedia", "isAppInstalled: App Not Installed")
            false
        }
    }
}