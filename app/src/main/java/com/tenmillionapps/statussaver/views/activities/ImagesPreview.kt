package com.tenmillionapps.statussaver.views.activities

import BaseActivity
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.tenmillionapps.statussaver.R
import com.tenmillionapps.statussaver.databinding.ActivityImagesPreviewBinding
import com.tenmillionapps.statussaver.models.MediaModel
import com.tenmillionapps.statussaver.utils.Constants
import com.tenmillionapps.statussaver.views.adapters.ImagePreviewAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.tenmillionapps.statussaver.models.MEDIA_TYPE_VIDEO

class ImagesPreview : BaseActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityImagesPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: ImagePreviewAdapter
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
            adapter = ImagePreviewAdapter(list, activity)
            imagesViewPager.adapter = adapter
            imagesViewPager.setCurrentItem(scrollTo, false)

            imagesViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    val media = list[position]
                    if (media.type == MEDIA_TYPE_VIDEO) {
                        // Launch VideosPreviewActivity and finish the current activity
                        val intent = Intent(this@ImagesPreview, VideosPreview::class.java)
                        intent.putExtra(Constants.MEDIA_LIST_KEY, list)
                        intent.putExtra(Constants.MEDIA_SCROLL_KEY, position)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }



            val toolbar = findViewById<MaterialToolbar>(R.id.tool_bar)
        toolbar.setNavigationOnClickListener {
            finish()
        }


    }
}