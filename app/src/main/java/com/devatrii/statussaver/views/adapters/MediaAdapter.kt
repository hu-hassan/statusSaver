package com.devatrii.statussaver.views.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devatrii.statussaver.R
import com.devatrii.statussaver.databinding.ItemMediaBinding
import com.devatrii.statussaver.models.MEDIA_TYPE_IMAGE
import com.devatrii.statussaver.models.MediaModel
import com.devatrii.statussaver.utils.Constants
import com.devatrii.statussaver.utils.isStatusExist
import com.devatrii.statussaver.utils.isStatusSaved
import com.devatrii.statussaver.utils.saveStatus
import com.devatrii.statussaver.views.activities.ImagesPreview
import com.devatrii.statussaver.views.activities.VideosPreview

class MediaAdapter(val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(statusImage)
                if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                    statusPlay.visibility = View.GONE
                }
//                statusDownload
                val downloadImage = if (context.isStatusExist(mediaModel.fileName)|| context.isStatusSaved(mediaModel.fileName)) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                statusDownload.setImageResource(downloadImage)

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