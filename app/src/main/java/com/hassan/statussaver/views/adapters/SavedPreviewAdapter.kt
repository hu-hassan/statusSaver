package com.hassan.statussaver.views.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hassan.statussaver.databinding.SavedMediaPreviewBinding
import com.hassan.statussaver.models.MediaModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.google.android.material.appbar.MaterialToolbar
import com.hassan.statussaver.R
import com.hassan.statussaver.utils.isStatusExist
import com.hassan.statussaver.utils.isStatusSaved
import com.hassan.statussaver.utils.saveStatus
import java.io.File

class SavedPreviewAdapter(
    private val context: Context,
    private val mediaList: ArrayList<MediaModel>
) : RecyclerView.Adapter<SavedPreviewAdapter.MediaViewHolder>() {

    private val players = mutableMapOf<Int, ExoPlayer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = SavedMediaPreviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaModel = mediaList[position]
        holder.bind(mediaModel, position)
    }

    override fun getItemCount() = mediaList.size

inner class MediaViewHolder(private val binding: SavedMediaPreviewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @OptIn(UnstableApi::class)
    fun bind(mediaModel: MediaModel, position: Int) {
        binding.mediaContainer.removeAllViews()

        if (mediaModel.type == "video") {
            val videoView = LayoutInflater.from(context).inflate(R.layout.item_video_preview, binding.mediaContainer, false)
            val player = ExoPlayer.Builder(context).build()
            videoView.findViewById<PlayerView>(R.id.player_view).player = player
            val mediaItem = MediaItem.fromUri(mediaModel.pathUri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.repeatMode = Player.REPEAT_MODE_ONE
            players[position] = player
            binding.mediaContainer.addView(videoView)
        } else {
            val imageView = LayoutInflater.from(context).inflate(R.layout.item_image_preview, binding.mediaContainer, false)
            Glide.with(context)
                .load(mediaModel.pathUri)
                .into(imageView.findViewById(R.id.zoomable_image_view))
            binding.mediaContainer.addView(imageView)
        }

        setupDownloadButton(mediaModel)
    }

    private fun setupDownloadButton(mediaModel: MediaModel) {
        binding.tools.text.text = "Delete"

        binding.tools.statusDownload.setImageResource(R.drawable.delete)

        binding.tools.download.setOnClickListener {
            val directories = listOf(
                File(Environment.getExternalStorageDirectory(), "Pictures/Status Saver"),
                File(Environment.getExternalStorageDirectory(), "Movies/Status Saver")
            )
            val filenamesToDelete = mediaModel.fileName

            directories.forEach { directory ->
                if (directory.exists() && directory.isDirectory) {
                    directory.listFiles()?.forEach { file ->
                        if (filenamesToDelete.contains(file.name)) {
                            Log.d("SavedPreviewAdapter", "Deleting file: ${file.name}")
                            file.delete()
                        }
                    }
                }
            }
            Toast.makeText(context, "This item has been deleted", Toast.LENGTH_SHORT).show()
            if (context is Activity) {
                context.finish()
            }

        }
        binding.tools.repost.setOnClickListener {
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
        binding.tools.share.setOnClickListener {
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
        binding.root.findViewById<View>(R.id.tools_holder)?.visibility = View.GONE

    }
}

    fun pauseAllVideosOnDestroy() {
        players.forEach { (_, player) ->
            player.playWhenReady = false
        }
    }

    fun pauseAllVideos(exceptPosition: Int? = null) {
        players.forEach { (index, player) ->
            if (index != exceptPosition) {
                player.playWhenReady = false
            }
        }
    }

    fun playVideoAtPosition(position: Int) {
        Log.d("SavedPreviewAdapter", "playVideoAtPosition: position=$position, players.size=${players.size}")
        val mediaModel = mediaList[position]
        if (mediaModel.type == "video") {
            if (!players.containsKey(position)) {
                initializePlayerForPosition(position)
            }
            players[position]?.playWhenReady = true
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayerForPosition(position: Int) {
        val mediaModel = mediaList[position]
        if (mediaModel.type == "video") {
            val player = ExoPlayer.Builder(context).setSeekBackIncrementMs(5000).setSeekForwardIncrementMs(5000).build()
            val mediaItem = MediaItem.fromUri(mediaModel.pathUri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.repeatMode = Player.REPEAT_MODE_ONE
            players[position] = player
        }
    }
    fun pauseAllVideosExcept(position: Int) {
        players.forEach { (index, player) ->
            player.playWhenReady = index == position
        }
    }

}