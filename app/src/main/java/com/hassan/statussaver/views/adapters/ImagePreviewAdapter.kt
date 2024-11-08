package com.hassan.statussaver.views.adapters

import BaseActivity
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
                            if (context is BaseActivity) {
                                (context as BaseActivity).performAction()
                            }
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
                    // Create the intent for sharing the media
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/octet-stream"  // Adjust the MIME type based on your media type (e.g., image/* or video/*)

                        // Replace with the actual Uri of the media you want to share
                        val mediaUri = Uri.parse(mediaModel.pathUri)
                        putExtra(Intent.EXTRA_STREAM, mediaUri)

                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Grant permission for the apps to access the file
                    }

                    // Get the package manager
                    val packageManager = context.packageManager

                    // Create intents for WhatsApp and WhatsApp Business
                    val whatsappIntent = Intent(shareIntent).setPackage("com.whatsapp")
                    val whatsappBusinessIntent = Intent(shareIntent).setPackage("com.whatsapp.w4b")

                    // Check if WhatsApp is installed
                    val resolveWhatsApp = whatsappIntent.resolveActivity(packageManager)
                    // Check if WhatsApp Business is installed
                    val resolveWhatsAppBusiness = whatsappBusinessIntent.resolveActivity(packageManager)

                    // Create a list of intents based on available apps
                    val targetedIntents = mutableListOf<Intent>()

                    if (resolveWhatsApp != null) {
                        targetedIntents.add(whatsappIntent)
                    }
                    if (resolveWhatsAppBusiness != null) {
                        targetedIntents.add(whatsappBusinessIntent)
                    }

                    // Handle cases where both, one, or none of the apps are available
                    when (targetedIntents.size) {
                        1 -> {  // If only one option is available, launch it directly
                            context.startActivity(targetedIntents[0])
                        }
                        2 -> {  // If both are available, show the chooser with the two options
                            val chooserIntent = Intent.createChooser(targetedIntents.removeAt(0), "Share media via...")
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntents.toTypedArray())
                            context.startActivity(chooserIntent)
                        }
                        else -> {  // If neither is available, fall back to a generic chooser
                            context.startActivity(Intent.createChooser(shareIntent, "Share media..."))
                        }
                    }
                }
                tools.share.setOnClickListener {
                    // Create the intent for sharing the media
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/octet-stream"  // Adjust the MIME type based on your media type (e.g., image/* or video/*)

                        // Replace with the actual Uri of the media you want to share
                        val mediaUri = Uri.parse(mediaModel.pathUri)
                        putExtra(Intent.EXTRA_STREAM, mediaUri)

                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Grant permission to the receiving app to access the file
                    }

                    // Show the chooser dialog for the user to select an app to share the media
                    context.startActivity(Intent.createChooser(shareIntent, "Share media..."))
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










