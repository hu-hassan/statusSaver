package com.hassan.statussaver.views.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
                        viewModel.whatsAppImagesLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                        binding.tempMediaBtn.setOnClickListener{
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("https://api.whatsapp.com/chat")
                            intent.setPackage("com.whatsapp")
                            try {
                                startActivity(intent)
                            } catch (e: Exception) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                                startActivity(intent)
                            }
                        }


                    }
                    Constants.MEDIA_TYPE_WHATSAPP_VIDEOS -> {
                        viewModel.whatsAppVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                        binding.tempMediaBtn.setOnClickListener{
                            val intent =
                                requireActivity().packageManager.getLaunchIntentForPackage("com.whatsapp")
                            if (intent != null) {
                                startActivity(intent) // WhatsApp is installed, open it
                            }
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES -> {
                        viewModel.whatsAppBusinessImagesLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                        binding.tempMediaBtn.icon = resources.getDrawable(R.drawable.whatsapp_business)
                        binding.tempMediaBtn.setOnClickListener {
                            val intent = context?.packageManager?.getLaunchIntentForPackage("com.whatsapp.w4b")
                            if (intent != null) {
                                startActivity(intent) // Opens WhatsApp Business if installed
                            } else {
                                // Fallback to WhatsApp if Business isn't available
                                val fallbackIntent = context?.packageManager?.getLaunchIntentForPackage("com.whatsapp")
                                if (fallbackIntent != null) {
                                    startActivity(fallbackIntent) // Opens regular WhatsApp
                                } else {
                                    // Redirect to Play Store if neither is installed
                                    val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"))
                                    startActivity(playStoreIntent)
                                }
                            }

                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS -> {
                        viewModel.whatsAppBusinessVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                        binding.tempMediaBtn.icon = resources.getDrawable(R.drawable.whatsapp_business)
                        binding.tempMediaBtn.setOnClickListener{
                            val intent =
                                requireActivity().packageManager.getLaunchIntentForPackage("com.whatsapp.w4b")
                            if (intent != null) {
                                Log.d("FragmentMedia", "Opening whatsapp for view status ")
                                startActivity(intent) // WhatsApp is installed, open it
                            }
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

//    private fun updateUI(unFilteredList: ArrayList<MediaModel>) {
//        val filteredList = unFilteredList.distinctBy { model ->
//            model.fileName
//        }
//
//        val list = ArrayList<MediaModel>()
//        filteredList.forEach { model ->
//            if (isStatusExistInStatuses(model.fileName) || isStatusExistInBStatuses(model.fileName) ||context?.isStatusExist(model.fileName) == true || context?.isStatusSaved(model.fileName) == true) {
//                list.add(model)
//            }
//            if (!(isStatusExistInStatuses(model.fileName) || isStatusExistInBStatuses(model.fileName) || context?.isStatusExist(model.fileName) == true || context?.isStatusSaved(model.fileName) == true)) {
//                list.remove(model)
//            }
//
//        }
//        if (saved){
//            adapter = MediaAdapter(ArrayList(list), requireActivity(), true)
//            binding.mediaRecyclerView.adapter = adapter
//            binding.tempMediaText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
//        }
//        else {
//            adapter = MediaAdapter(list, requireActivity(), false)
//            binding.mediaRecyclerView.adapter = adapter
//            binding.tempMediaText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
//        }
//
//
//
//    }
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

    // Update the adapter with the final list
    adapter = MediaAdapter(ArrayList(list), requireActivity(), false)
    binding.mediaRecyclerView.adapter = adapter

    // Show or hide the temporary text based on whether the final list is empty
    binding.tempMediaText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    binding.tempMediaText2.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    binding.tempMediaBtn.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
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

        // Update the adapter with the final list
        adapter = MediaAdapter(ArrayList(list), requireActivity(), true)
        binding.mediaRecyclerView.adapter = adapter

        // Show or hide the temporary text based on whether the final list is empty
        binding.tempMediaText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root
}