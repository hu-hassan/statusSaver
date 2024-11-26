package com.tenmillionapps.statussaver.views.activities

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.tenmillionapps.statussaver.R
import com.tenmillionapps.statussaver.databinding.ActivitySavedPreviewBinding
import com.tenmillionapps.statussaver.models.MediaModel
import com.tenmillionapps.statussaver.utils.Constants
import com.tenmillionapps.statussaver.views.adapters.SavedPreviewAdapter

class SavedMediaPreview : BaseActivity() {
    private lateinit var binding: ActivitySavedPreviewBinding
    private lateinit var adapter: SavedPreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedPreviewBinding.inflate(layoutInflater)
        performAction()
        setContentView(binding.root)
        val header = findViewById<MaterialToolbar>(R.id.tool_bar)
        header.setNavigationOnClickListener {
            finish()
        }

        Log.d("SavedMediaPreview", "Doing rest of onCreate")
        val mediaList =
            intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as? ArrayList<MediaModel>
                ?: arrayListOf()
        val initialPosition = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
        adapter = SavedPreviewAdapter(this, mediaList)
        Log.d("SavedMediaPreview", "Setting adapter with mediaList size: ${mediaList.size}")
        binding.savedViewPager.adapter = adapter
        binding.savedViewPager.setCurrentItem(initialPosition, false)

        binding.savedViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("SavedMediaPreview", "Page selected: $position")
                adapter.swipe = false
                adapter.pauseAllVideos()
                adapter.pauseAllVideosExcept(position)
                adapter.playVideoAtPosition(position)


            }
        })
        binding.savedViewPager.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.savedViewPager.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val mediaType = getCurrentMediaType()

                if (mediaType == "video") {
                    Log.d("SavedMediaPreview", "Playing video at position: ${binding.savedViewPager.currentItem}")
                    adapter.pauseAllVideos()
                    adapter.pauseAllVideosExcept(binding.savedViewPager.currentItem)
                    adapter.playVideoAtPosition(binding.savedViewPager.currentItem)
                }
            }
        })

    }


    override fun onDestroy() {
        super.onDestroy()
        adapter.pauseAllVideosOnDestroy()
    }
    fun getCurrentMediaType(): String? {
        val currentPosition = binding.savedViewPager.currentItem
        return adapter.getMediaTypeAtPosition(currentPosition)
    }
}