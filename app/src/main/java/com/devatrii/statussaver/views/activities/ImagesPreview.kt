package com.devatrii.statussaver.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devatrii.statussaver.R
import com.devatrii.statussaver.databinding.ActivityImagesPreviewBinding
import com.devatrii.statussaver.models.MediaModel
import com.devatrii.statussaver.utils.Constants
import com.devatrii.statussaver.views.adapters.ImagePreviewAdapter
import com.google.android.material.appbar.MaterialToolbar

class ImagesPreview : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityImagesPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: ImagePreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val list = intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            adapter = ImagePreviewAdapter(list, activity)
            imagesViewPager.adapter = adapter
            imagesViewPager.setCurrentItem(scrollTo, false)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.tool_bar)
        toolbar.setNavigationOnClickListener {
            finish() // Finish the activity when the back button is pressed
        }


    }
}