package com.hassan.statussaver.views.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hassan.statussaver.R
import com.hassan.statussaver.databinding.ItemImagePreviewBinding
import com.hassan.statussaver.models.MediaModel
import com.hassan.statussaver.utils.isStatusExist
import com.hassan.statussaver.utils.saveStatus
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
//                tools.share.setOnClickListener {
//                    shareMedia(context, mediaModel)
//                }
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
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size
    private fun shareMedia(context: Context, media: MediaModel) {
        val file = File(media.pathUri)
        val uri = Uri.fromFile(file)
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT,context.getString(R.string.app_name))
            putExtra(Intent.EXTRA_STREAM,uri)
            context.startActivity(this)
        }

    }

}










