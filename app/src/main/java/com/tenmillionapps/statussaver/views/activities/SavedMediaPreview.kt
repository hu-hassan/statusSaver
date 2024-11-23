package com.tenmillionapps.statussaver.views.activities

import BaseActivity
import android.os.Bundle
import android.util.Log
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
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.pauseAllVideosOnDestroy()
    }
}