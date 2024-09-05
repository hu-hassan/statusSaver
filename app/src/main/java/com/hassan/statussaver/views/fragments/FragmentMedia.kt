package com.hassan.statussaver.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

class FragmentMedia : Fragment() {
    private val binding by lazy {
        FragmentMediaBinding.inflate(layoutInflater)
    }
    lateinit var viewModel: StatusViewModel
    lateinit var adapter: MediaAdapter

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
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_VIDEOS -> {
                        viewModel.whatsAppVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES -> {
                        viewModel.whatsAppBusinessImagesLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS -> {
                        viewModel.whatsAppBusinessVideosLiveData.observe(requireActivity()) { unFilteredList ->
                            updateUI(unFilteredList)
                        }
                    }
                    Constants.MEDIA_TYPE_WHATSAPP_SAVED-> {
                        Log.d("FragmentMedia", "onCreate: Saved")
                        viewModel.wpSavedStatusLiveData.observe(requireActivity()) { unFilteredList ->
                            Log.d("FragmentMedia", "Saved list under observation")
                            for (i in unFilteredList){
                                Log.d("FragmentMedia", "Filea present: ${i.fileName}")
                            }
                            updateUIforSaved(unFilteredList)
                        }
//                        tempMediaText.text = "Work in progress............."
                    }
                }
                val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                    false
                )
                if (!isPermissionGranted){
                    binding.tempMediaText.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUI(unFilteredList: ArrayList<MediaModel>) {
        val filteredList = unFilteredList.distinctBy { model ->
            model.fileName
        }

        val list = ArrayList<MediaModel>()
        filteredList.forEach { model ->
            if (isStatusExistInStatuses(model.fileName) || isStatusExistInBStatuses(model.fileName)) {
                list.add(model)
            }
            if (!(isStatusExistInStatuses(model.fileName) || isStatusExistInBStatuses(model.fileName))) {
                list.remove(model)
            }

        }
        adapter = MediaAdapter(list, requireActivity(),false)
        binding.mediaRecyclerView.adapter = adapter
        binding.tempMediaText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE

    }

    private fun updateUIforSaved(unFilteredList: ArrayList<MediaModel>) {
        val filteredList = unFilteredList.distinctBy { model ->
            model.fileName
        }

        val list = ArrayList<MediaModel>()
        val filteredListCopy = ArrayList(filteredList)
        filteredListCopy.forEach { model ->
            if(context?.isStatusExist(model.fileName) == true || context?.isStatusSaved(model.fileName) == true) {
                list.add(model)
            }
            if(!(context?.isStatusExist(model.fileName) == true || context?.isStatusSaved(model.fileName) == true)){
                list.remove(model)
            }
        }
        adapter = MediaAdapter(list, requireActivity(), true)
        binding.mediaRecyclerView.adapter = adapter
        binding.tempMediaText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root
}