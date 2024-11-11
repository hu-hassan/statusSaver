package com.tenmillionapps.statussaver.views.activities

import BaseActivity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.tenmillionapps.statussaver.R
import com.tenmillionapps.statussaver.databinding.ActivityVideosPreviewBinding
import com.tenmillionapps.statussaver.models.MediaModel
import com.tenmillionapps.statussaver.utils.Constants
import com.tenmillionapps.statussaver.views.adapters.VideoPreviewAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.tenmillionapps.statussaver.models.MEDIA_TYPE_VIDEO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideosPreview : BaseActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityVideosPreviewBinding.inflate(layoutInflater)
    }
    private var currentPlayingVideo: VideoPreviewAdapter.ViewHolder? = null

    lateinit var adapter: VideoPreviewAdapter
    private val TAG = "VideosPreview"
    private val REQUEST_CODE_IMAGE_PREVIEW = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        performAction()
        val header = findViewById<MaterialToolbar>(R.id.tool_bar)
        header.setNavigationOnClickListener {
            finish()
        }
        binding.apply {
            val list = intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            adapter = VideoPreviewAdapter(list, activity)
            videoRecyclerView.adapter = adapter
            val pageSnapHelper = PagerSnapHelper()
            pageSnapHelper.attachToRecyclerView(videoRecyclerView)
            videoRecyclerView.scrollToPosition(scrollTo)

            videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    // Stop the currently playing video
                    currentPlayingVideo?.stopPlayer()

                    // Find the most visible item
                    var mostVisiblePosition = -1
                    var maxVisibility = 0f
                    for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                        val itemView = layoutManager.findViewByPosition(i)
                        val visibility = itemView?.let {
                            it.height.toFloat() - Math.max(0f, Math.min(it.y.toFloat(), it.height.toFloat() - recyclerView.height.toFloat()))
                        } ?: 0f
                        if (visibility > maxVisibility) {
                            maxVisibility = visibility
                            mostVisiblePosition = i
                        }
                    }

                    val media = list[mostVisiblePosition]
                    if (media.type == MEDIA_TYPE_VIDEO) {
                        val viewHolder = recyclerView.findViewHolderForAdapterPosition(mostVisiblePosition) as? VideoPreviewAdapter.ViewHolder
                        viewHolder?.startPlayer()
                        currentPlayingVideo = viewHolder
                    } else {
                        stopAllPlayers()
                        // Launch ImagePreviewActivity and finish the current activity
                        val intent = Intent(this@VideosPreview, ImagesPreview::class.java)
                        intent.putExtra(Constants.MEDIA_LIST_KEY, list)
                        intent.putExtra(Constants.MEDIA_SCROLL_KEY, mostVisiblePosition)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.tool_bar)
        toolbar.setNavigationOnClickListener {
            finish() // Finish the activity when the back button is pressed
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