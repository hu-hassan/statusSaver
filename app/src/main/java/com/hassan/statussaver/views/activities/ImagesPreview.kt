package com.hassan.statussaver.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.hassan.statussaver.R
import com.hassan.statussaver.databinding.ActivityImagesPreviewBinding
import com.hassan.statussaver.models.MediaModel
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.views.adapters.ImagePreviewAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.hassan.statussaver.models.MEDIA_TYPE_VIDEO

class ImagesPreview : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityImagesPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: ImagePreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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