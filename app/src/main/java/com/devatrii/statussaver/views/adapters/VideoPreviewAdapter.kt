package com.devatrii.statussaver.views.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.devatrii.statussaver.R
import com.devatrii.statussaver.databinding.ItemVideoPreviewBinding
import com.devatrii.statussaver.models.MediaModel
import com.devatrii.statussaver.utils.SharedPrefUtils
import com.devatrii.statussaver.utils.isStatusExist
import com.devatrii.statussaver.utils.isStatusSaved
import com.devatrii.statussaver.utils.saveStatus
import java.io.File

class VideoPreviewAdapter(val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<VideoPreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UnsafeOptInUsageError")
        fun bind(mediaModel: MediaModel) {

            binding.apply {

                val player = ExoPlayer.Builder(context).setSeekBackIncrementMs(5000).setSeekForwardIncrementMs(5000).build()
                player.seekTo(player.currentPosition + 5000)
                playerView.player = player
                val mediaItem = MediaItem.fromUri(mediaModel.pathUri)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.repeatMode = Player.REPEAT_MODE_ONE

                val downloadImage = if (context.isStatusSaved(mediaModel.fileName)) {
                    tools.text.text = "Saved"
                    R.drawable.ic_downloaded
                } else {
                    tools.text.text = "Save"
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)

                tools.download.setOnClickListener {
                    if (!context.isStatusSaved(mediaModel.fileName)) {
                        val isDownloaded = context.saveStatus(mediaModel)
                        if (isDownloaded) {
                            // Status is downloaded
                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                            mediaModel.isDownloaded = true
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


            }
        }

        fun startPlayer() {
            binding.playerView.player?.play()
        }

        fun stopPlayer() {
            binding.playerView.player?.pause()
        }


    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoPreviewAdapter.ViewHolder {
        return ViewHolder(
            ItemVideoPreviewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoPreviewAdapter.ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
        holder.startPlayer()//        val fileInGallery = File(model.pathUri)
//        if (fileInGallery.exists()) {
//            // If the file exists in the gallery, show the double tick icon
//            holder.binding.tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
//        } else {
//            // If the file does not exist in the gallery, show the download icon
//            holder.binding.tools.statusDownload.setImageResource(R.drawable.ic_download)
//        }
//        SharedPrefUtils.syncDeletionWithGallery(context)

    }

    override fun getItemCount() = list.size



}











