package com.devatrii.statussaver.views.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.devatrii.statussaver.databinding.ActivityVideosPreviewBinding
import com.devatrii.statussaver.models.MediaModel
import com.devatrii.statussaver.utils.Constants
import com.devatrii.statussaver.views.adapters.VideoPreviewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideosPreview : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityVideosPreviewBinding.inflate(layoutInflater)
    }
    private var currentPlayingVideo: VideoPreviewAdapter.ViewHolder? = null

    lateinit var adapter: VideoPreviewAdapter
    private val TAG = "VideosPreview"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.apply {

            val list =
                intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            adapter = VideoPreviewAdapter(list, activity)
            videoRecyclerView.adapter = adapter
            val pageSnapHelper = PagerSnapHelper()
            pageSnapHelper.attachToRecyclerView(videoRecyclerView)
            videoRecyclerView.scrollToPosition(scrollTo)

//            videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                        Log.d(TAG, "onScrollStateChanged: Dragging")
//                        stopAllPlayers()
//                    }
//                }
//
//
//            })
            videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    // Find the most visible item
                    var mostVisiblePosition = -1
                    var maxVisibility = 0f
                    for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                        val itemView = layoutManager.findViewByPosition(i)
                        val visibility = itemView?.let {
                            it.height.toFloat() - Math.max(0f, Math.min(it.y.toFloat(), it.height.toFloat() - recyclerView.height.toFloat()))                        } ?: 0f
                        if (visibility > maxVisibility) {
                            maxVisibility = visibility
                            mostVisiblePosition = i
                        }
                    }

//                    // If the most visible item is different from the currently playing video, pause the currently playing video and play the most visible one
//                    if (mostVisiblePosition != -1 && mostVisiblePosition != currentPlayingVideo?.layoutPosition) {
//                        currentPlayingVideo?.let { adapter.pausePlayer(it) }
//                        currentPlayingVideo = recyclerView.findViewHolderForAdapterPosition(mostVisiblePosition) as VideoPreviewAdapter.ViewHolder
//                        currentPlayingVideo?.let { adapter.playPlayer(it) }
//                    }
                }
            })



        }


    }

    private fun stopAllPlayers() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.apply {
                    for (i in 0 until videoRecyclerView.childCount) {
                        val child = videoRecyclerView.getChildAt(i)
                        val viewHolder = videoRecyclerView.getChildViewHolder(child)
                        if (viewHolder is VideoPreviewAdapter.ViewHolder) {
                            viewHolder.stopPlayer()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopAllPlayers()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAllPlayers()
    }

}
















