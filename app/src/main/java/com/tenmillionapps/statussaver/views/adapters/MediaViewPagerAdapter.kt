package com.tenmillionapps.statussaver.views.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tenmillionapps.statussaver.utils.Constants
import com.tenmillionapps.statussaver.views.fragments.FragmentMedia

class MediaViewPagerAdapter(
    private val fragmentActivity: FragmentActivity,
    private val imagesType: String = Constants.MEDIA_TYPE_WHATSAPP_IMAGES,
    private val videosType: String = Constants.MEDIA_TYPE_WHATSAPP_VIDEOS,
    private val savedType: String = Constants.MEDIA_TYPE_WHATSAPP_SAVED

) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                // images media fragment
                val mediaFragment = FragmentMedia()
                val bundle = Bundle()
                bundle.putString(Constants.MEDIA_TYPE_KEY,imagesType)
                mediaFragment.arguments = bundle
                mediaFragment
            }
            1->{
                // videos media fragment
                val mediaFragment = FragmentMedia()
                val bundle = Bundle()
                bundle.putString(Constants.MEDIA_TYPE_KEY,videosType)
                mediaFragment.arguments = bundle
                mediaFragment
            }
            else ->{
                // business media fragment
                val mediaFragment = FragmentMedia()
                val bundle = Bundle()
                bundle.putString(Constants.MEDIA_TYPE_KEY,savedType)
                mediaFragment.arguments = bundle
                mediaFragment
            }
        }
    }

}