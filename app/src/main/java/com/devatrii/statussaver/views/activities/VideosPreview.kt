package com.devatrii.statussaver.views.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.devatrii.statussaver.R
import com.devatrii.statussaver.databinding.ActivityVideosPreviewBinding
import com.devatrii.statussaver.models.MediaModel
import com.devatrii.statussaver.utils.Constants
import com.devatrii.statussaver.views.adapters.VideoPreviewAdapter
import com.google.android.material.appbar.MaterialToolbar
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
















