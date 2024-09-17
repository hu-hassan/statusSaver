//
//package com.hassan.statussaver.views.activities
//
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import com.hassan.statussaver.databinding.ActivitySavedPreviewBinding
//import com.hassan.statussaver.models.MediaModel
//import com.hassan.statussaver.utils.Constants
//import com.hassan.statussaver.views.adapters.SavedPreviewAdapter
//
//class SavedMediaPreview : AppCompatActivity() {
//    private lateinit var binding: ActivitySavedPreviewBinding
//    private lateinit var adapter: SavedPreviewAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySavedPreviewBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val mediaList = intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as? ArrayList<MediaModel> ?: arrayListOf()
//        val initialPosition = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
//        adapter = SavedPreviewAdapter(this, mediaList)
//        Log.d("SavedMediaPreview", "Setting adapter with mediaList size: ${mediaList.size}")
//        binding.savedViewPager.adapter = adapter
//        binding.savedViewPager.setCurrentItem(initialPosition, false)
//    }
//}
//package com.hassan.statussaver.views.activities
//
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.viewpager2.widget.ViewPager2
//import com.hassan.statussaver.databinding.ActivitySavedPreviewBinding
//import com.hassan.statussaver.models.MediaModel
//import com.hassan.statussaver.utils.Constants
//import com.hassan.statussaver.views.adapters.SavedPreviewAdapter
//
//class SavedMediaPreview : AppCompatActivity() {
//    private lateinit var binding: ActivitySavedPreviewBinding
//    private lateinit var adapter: SavedPreviewAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySavedPreviewBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val mediaList = intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as? ArrayList<MediaModel> ?: arrayListOf()
//        val initialPosition = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
//        adapter = SavedPreviewAdapter(this, mediaList)
//        Log.d("SavedMediaPreview", "Setting adapter with mediaList size: ${mediaList.size}")
//        binding.savedViewPager.adapter = adapter
//        binding.savedViewPager.setCurrentItem(initialPosition, false)
//
//        binding.savedViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                adapter.pauseAllVideos()
//                adapter.playVideoAtPosition(position)
//            }
//        })
//    }
//}
package com.hassan.statussaver.views.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.hassan.statussaver.databinding.ActivitySavedPreviewBinding
import com.hassan.statussaver.models.MediaModel
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.views.adapters.SavedPreviewAdapter

class SavedMediaPreview : AppCompatActivity() {
    private lateinit var binding: ActivitySavedPreviewBinding
    private lateinit var adapter: SavedPreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mediaList = intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as? ArrayList<MediaModel> ?: arrayListOf()
        val initialPosition = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
        adapter = SavedPreviewAdapter(this, mediaList)
        Log.d("SavedMediaPreview", "Setting adapter with mediaList size: ${mediaList.size}")
        binding.savedViewPager.adapter = adapter
        binding.savedViewPager.setCurrentItem(initialPosition, false)

        binding.savedViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.pauseAllVideos()
                adapter.playVideoAtPosition(position)
            }
        })
    }
}