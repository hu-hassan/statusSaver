//package com.hassan.statussaver.views.adapters
//
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.annotation.OptIn
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.hassan.statussaver.databinding.SavedMediaPreviewBinding
//import com.hassan.statussaver.models.MediaModel
//import androidx.media3.common.MediaItem
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.ui.PlayerView
//import com.hassan.statussaver.R
//
//class SavedPreviewAdapter(
//    private val context: Context,
//    private val mediaList: ArrayList<MediaModel>
//) : RecyclerView.Adapter<SavedPreviewAdapter.MediaViewHolder>() {
//
//    private val players = mutableListOf<ExoPlayer>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
//        val binding = SavedMediaPreviewBinding.inflate(LayoutInflater.from(context), parent, false)
//        return MediaViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
//        val mediaModel = mediaList[position]
//        holder.bind(mediaModel)
//    }
//
//    override fun getItemCount() = mediaList.size
//
//    inner class MediaViewHolder(private val binding: SavedMediaPreviewBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        @OptIn(UnstableApi::class)
//        fun bind(mediaModel: MediaModel) {
//            Log.d("SavedPreviewAdapter", "bind: $mediaModel")
//            if (mediaModel.type == "video") {
//                binding.imageStub.visibility = View.GONE
//                val videoView = binding.videoStub.inflate()
//                val player = ExoPlayer.Builder(context).setSeekBackIncrementMs(5000).setSeekForwardIncrementMs(5000).build()
//                videoView.findViewById<PlayerView>(R.id.player_view).player = player
//                val mediaItem = MediaItem.fromUri(mediaModel.pathUri)
//                player.setMediaItem(mediaItem)
//                player.prepare()
//                players.add(player)
//            } else {
//                binding.videoStub.visibility = View.GONE
//                val imageView = binding.imageStub.inflate()
//                Glide.with(context)
//                    .load(mediaModel.pathUri)
//                    .into(imageView.findViewById(R.id.zoomable_image_view))
//            }
//            // Hide the tools_holder
//            binding.root.findViewById<View>(R.id.tools_holder)?.visibility = View.GONE
//        }
//    }
//
//    fun pauseAllVideos() {
//        players.forEach { it.playWhenReady = false }
//    }
//
//    fun playVideoAtPosition(position: Int) {
//        if (position in 0 until players.size) {
//            val mediaModel = mediaList[position]
//            if (mediaModel.type == "video") {
//                players[position].playWhenReady = true
//            }
//        }
//    }
//}
package com.hassan.statussaver.views.adapters

import android.content.Context
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

//    inner class MediaViewHolder(private val binding: SavedMediaPreviewBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        @OptIn(UnstableApi::class)
//        fun bind(mediaModel: MediaModel, position: Int) {
//            Log.d("SavedPreviewAdapter", "bind: $mediaModel")
//            if (mediaModel.type == "video") {
//                binding.imageStub.visibility = View.GONE
//                if (binding.videoStub.parent != null) {
//                    val videoView = binding.videoStub.inflate()
//                    val player = ExoPlayer.Builder(context).setSeekBackIncrementMs(5000)
//                        .setSeekForwardIncrementMs(5000).build()
//                    videoView.findViewById<PlayerView>(R.id.player_view).player = player
//                    val mediaItem = MediaItem.fromUri(mediaModel.pathUri)
//                    player.setMediaItem(mediaItem)
//                    player.prepare()
//                    player.repeatMode = Player.REPEAT_MODE_ONE
//                    players[position] = player
//                    val downloadImage = if (context.isStatusSaved(mediaModel.fileName)) {
//                        binding.tools.text.text = "Saved"
//                        R.drawable.ic_downloaded
//                    } else {
//                        binding.tools.text.text = "Save"
//                        R.drawable.ic_download
//                    }
//                    binding.tools.statusDownload.setImageResource(downloadImage)
//
//                    binding.tools.download.setOnClickListener {
//                        if (!context.isStatusSaved(mediaModel.fileName)) {
//                            val isDownloaded = context.saveStatus(mediaModel)
//                            if (isDownloaded) {
//                                // Status is downloaded
//                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
//                                mediaModel.isDownloaded = true
//                                binding.tools.text.text = "Saved"
//                                binding.tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
//                            } else {
//                                // Unable to download status
//                                Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            // Status is already downloaded
//                            Toast.makeText(context, "Already Saved", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                }
//            }
//            else {
//                binding.videoStub.visibility = View.GONE
//                if (binding.imageStub.parent != null) {
//                    val imageView = binding.imageStub.inflate()
//
//                    Glide.with(context)
//                        .load(mediaModel.pathUri)
//                        .into(imageView.findViewById(R.id.zoomable_image_view))
//                    val downloadImage = if (context.isStatusExist(mediaModel.fileName)) {
//                        binding.tools.text.text = "Saved"
//                        R.drawable.ic_downloaded
//                    } else {
//                        binding.tools.text.text = "Save"
//                        R.drawable.ic_download
//                    }
//                    binding.tools.statusDownload.setImageResource(downloadImage)
//
//                    binding.tools.download.setOnClickListener {
//                        if (!context.isStatusExist(mediaModel.fileName)) {
//                            val isDownloaded = context.saveStatus(mediaModel)
//                            if (isDownloaded) {
//                                // Status is downloaded
//                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
//                                mediaModel.isDownloaded = true
//                                Log.d(
//                                    "ImagePreviewAdapter",
//                                    "isDownloaded: ${mediaModel.isDownloaded}"
//                                )
//                                binding.tools.text.text = "Saved"
//                                binding.tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
//                            } else {
//                                // Unable to download status
//                                Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            // Status is already downloaded
//                            Toast.makeText(context, "Already Saved", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    binding.tools.repost.setOnClickListener {
//
//                    }
////                binding.tools.share.setOnClickListener {
////                    shareMedia(context, mediaModel)
////                }
//                }
//            }
//            binding.root.findViewById<View>(R.id.tools_holder)?.visibility = View.GONE
//        }
//    }
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
        val downloadImage = if (context.isStatusSaved(mediaModel.fileName)||context.isStatusExist(mediaModel.fileName)) {
            binding.tools.text.text = "Saved"
            R.drawable.ic_downloaded
        } else {
            binding.tools.text.text = "Save"
            R.drawable.ic_download
        }
        binding.tools.statusDownload.setImageResource(downloadImage)

        binding.tools.download.setOnClickListener {
                Toast.makeText(context, "Already Saved", Toast.LENGTH_SHORT).show()
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