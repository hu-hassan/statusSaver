package com.devatrii.statussaver.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devatrii.statussaver.R
import com.devatrii.statussaver.databinding.ItemImagePreviewBinding
import com.devatrii.statussaver.databinding.ItemMediaBinding
import com.devatrii.statussaver.models.MEDIA_TYPE_IMAGE
import com.devatrii.statussaver.models.MediaModel
import com.devatrii.statussaver.utils.SharedPrefUtils
import com.devatrii.statussaver.utils.saveStatus

class ImagePreviewAdapter (val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(zoomableImageView)

                // Retrieve the isDownloaded property from shared preferences
//                mediaModel.isDownloaded = SharedPrefUtils.isStatusDownloaded(mediaModel.fileName)

                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)

                tools.download.setOnClickListener {
                    if (!mediaModel.isDownloaded) {
                        val isDownloaded = context.saveStatus(mediaModel)
                        if (isDownloaded) {
                            // Status is downloaded
                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                            mediaModel.isDownloaded = true
                            Log.d("ImagePreviewAdapter", "isDownloaded: ${mediaModel.isDownloaded}")
                            tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
                        } else {
                            // Unable to download status
                            Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Status is already downloaded
                        Toast.makeText(context, "Already Saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagePreviewAdapter.ViewHolder {
        return ViewHolder(ItemImagePreviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ImagePreviewAdapter.ViewHolder, position: Int) {
//        Log.d("ImagePreviewAdapter", "onBindViewHolder called for position $position")
        val model = list[position]
        holder.bind(model)
//        // Sync the app with the gallery
//                SharedPrefUtils.syncDeletionWithGallery(context)
//
//        // Check if the status is downloaded
//        if (SharedPrefUtils.isStatusDownloaded(model.fileName)) {
//            // If the status is downloaded, show the double tick icon
//            holder.binding.tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
//        } else {
//            // If the status is not downloaded, show the download icon
//            holder.binding.tools.statusDownload.setImageResource(R.drawable.ic_download)
//        }
    }

    override fun getItemCount() = list.size


}










