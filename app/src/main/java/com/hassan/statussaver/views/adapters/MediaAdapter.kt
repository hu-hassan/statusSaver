package com.hassan.statussaver.views.adapters

import BaseActivity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hassan.statussaver.R
import com.hassan.statussaver.databinding.ItemMediaBinding
import com.hassan.statussaver.models.MEDIA_TYPE_IMAGE
import com.hassan.statussaver.models.MediaModel
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.utils.isStatusExist
import com.hassan.statussaver.utils.isStatusSaved
import com.hassan.statussaver.utils.saveStatus
import com.hassan.statussaver.views.activities.ImagesPreview
import com.hassan.statussaver.views.activities.SavedMediaPreview
import com.hassan.statussaver.views.activities.VideosPreview

class MediaAdapter(val list: ArrayList<MediaModel>, val context: Context,val isSavedTab: Boolean) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {
    class MediaDiffCallback(
        private val oldList: List<MediaModel>,
        private val newList: List<MediaModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].fileName == newList[newItemPosition].fileName

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
    fun updateData(newList: List<MediaModel>) {
        val diffCallback = MediaDiffCallback(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }


    inner class ViewHolder(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(statusImage)
                if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                    statusPlay.visibility = View.GONE
                }
                val downloadImage = if (context.isStatusExist(mediaModel.fileName)|| context.isStatusSaved(mediaModel.fileName)) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                statusDownload.setImageResource(downloadImage)
                if(isSavedTab){
                    statusDownload.visibility = View.GONE
                    cardStatus.setOnClickListener {

                        Intent().apply {
                            putExtra(Constants.MEDIA_LIST_KEY,list)
                            putExtra(Constants.MEDIA_SCROLL_KEY,layoutPosition)
                            setClass(context, SavedMediaPreview::class.java)
                            context.startActivity(this)
                        }
                    }
                }
                else{
                    cardStatus.setOnClickListener {
                        if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                            // goto image preview activity
                            Intent().apply {
                                putExtra(Constants.MEDIA_LIST_KEY,list)
                                putExtra(Constants.MEDIA_SCROLL_KEY,layoutPosition)
                                setClass(context,ImagesPreview::class.java)
                                context.startActivity(this)
                            }
                        } else {
                            // goto video preview activity
                            Intent().apply {
                                putExtra(Constants.MEDIA_LIST_KEY,list)
                                putExtra(Constants.MEDIA_SCROLL_KEY,layoutPosition)
                                setClass(context,VideosPreview::class.java)
                                context.startActivity(this)
                            }
                        }
                    }
                }



                statusDownload.setOnClickListener {
                    // Check if the status is already downloaded
                    if (context.isStatusExist(mediaModel.fileName)||context.isStatusSaved(mediaModel.fileName)) {
                        // Show a message that the status is already downloaded
                        Toast.makeText(context, "Already saved", Toast.LENGTH_SHORT).show()
                    } else {
                        // Attempt to download the status
                        val isDownloaded = context.saveStatus(mediaModel)
                        if (isDownloaded) {
                            // Mark the status as downloaded
                            mediaModel.isDownloaded = true
                            // Update the icon to indicate it's downloaded
                            statusDownload.setImageResource(R.drawable.ic_downloaded)
                            // Optionally show a toast message for confirmation
                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                            if (context is BaseActivity) {
                                (context as BaseActivity).performAction()
                            }
                        } else {
                            // Handle the failure to download status
                            Toast.makeText(context, "Unable to save status", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMediaBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

}