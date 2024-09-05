package com.devatrii.statussaver.views.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devatrii.statussaver.R
import com.devatrii.statussaver.databinding.ItemImagePreviewBinding
import com.devatrii.statussaver.models.MediaModel
import com.devatrii.statussaver.utils.isStatusExist
import com.devatrii.statussaver.utils.saveStatus
import java.io.File

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
                val downloadImage = if (context.isStatusExist(mediaModel.fileName)) {
                    tools.text.text = "Saved"
                    R.drawable.ic_downloaded
                } else {
                    tools.text.text = "Save"
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)

                tools.download.setOnClickListener {
                    if (!context.isStatusExist(mediaModel.fileName)) {
                        val isDownloaded = context.saveStatus(mediaModel)
                        if (isDownloaded) {
                            // Status is downloaded
                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                            mediaModel.isDownloaded = true
                            Log.d("ImagePreviewAdapter", "isDownloaded: ${mediaModel.isDownloaded}")
                            tools.text.text = "Saved"
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
                tools.repost.setOnClickListener {

                }
                tools.share.setOnClickListener {
                    shareMedia(context, mediaModel)
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
    private fun shareMedia(context: Context, media: MediaModel) {
        val file = File(media.pathUri.toString())
        val uri = Uri.fromFile(file)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/" // or "video/" depending on the media type
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

}










