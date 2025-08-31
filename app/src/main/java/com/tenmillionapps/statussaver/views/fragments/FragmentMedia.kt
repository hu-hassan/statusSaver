package com.tenmillionapps.statussaver.views.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.tenmillionapps.statussaver.R
import com.tenmillionapps.statussaver.data.StatusRepo
import com.tenmillionapps.statussaver.databinding.FragmentMediaBinding
import com.tenmillionapps.statussaver.models.MediaModel
import com.tenmillionapps.statussaver.utils.Constants
import com.tenmillionapps.statussaver.utils.SharedPrefKeys
import com.tenmillionapps.statussaver.utils.SharedPrefUtils
import com.tenmillionapps.statussaver.utils.isStatusExist
import com.tenmillionapps.statussaver.utils.isStatusExistInBStatuses
import com.tenmillionapps.statussaver.utils.isStatusExistInStatuses
import com.tenmillionapps.statussaver.utils.isStatusSaved
import com.tenmillionapps.statussaver.viewmodels.factories.StatusViewModel
import com.tenmillionapps.statussaver.viewmodels.factories.StatusViewModelFactory
import com.tenmillionapps.statussaver.views.adapters.MediaAdapter
import java.util.concurrent.CopyOnWriteArrayList

class FragmentMedia : Fragment() {
    private val binding by lazy {
        FragmentMediaBinding.inflate(layoutInflater)
    }
//private var _binding: FragmentMediaBinding = null!!

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
                adapter = if (mediaType == Constants.MEDIA_TYPE_WHATSAPP_SAVED) {
                    MediaAdapter(ArrayList(), requireActivity(), true)
                } else {
                    MediaAdapter(ArrayList(), requireActivity(), false)
                }
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
            if (isStatusExistInStatuses(model.fileName)) {
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
        adapter.updateData(ArrayList(list))



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
        adapter.updateData(ArrayList(list))

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
        adapter.updateData(ArrayList(list))


        // Show or hide the temporary text based on whether the final list is empty
        binding.tempMediaText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            arguments?.let {
                val mediaType = it.getString(Constants.MEDIA_TYPE_KEY, "")
                if(mediaType == Constants.MEDIA_TYPE_WHATSAPP_SAVED){
                    adapter = MediaAdapter(ArrayList(), requireActivity(), true)
                }
                else{
                    adapter = MediaAdapter(ArrayList(), requireActivity(), false)
                }
            }
        binding.mediaRecyclerView.adapter = adapter
    }

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
                        viewModel.whatsAppImagesLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_VIDEOS -> {
                        viewModel.whatsAppVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES -> {

                        viewModel.whatsAppBusinessImagesLiveData.observe(requireActivity()) { unFilteredList ->
                            Log.d("FragmentMedia", "Whatsapp Business update ui calling")
                            Log.d("FragmentMedia", "Whatsapp Business size given to update ui ${unFilteredList.size}")
                            updateUIB(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS -> {
                        viewModel.whatsAppBusinessVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            Log.d("FragmentMedia", "Whatsapp Business update ui calling")
                            Log.d("FragmentMedia", "Whatsapp Business size given to update ui ${unFilteredList.size}")
                            updateUIB(unFilteredList)
                        }
                    }
                        Constants.MEDIA_TYPE_WHATSAPP_SAVED -> {
                        viewModel.wpSavedStatusLiveData.observe(requireActivity()) { unFilteredList ->
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
