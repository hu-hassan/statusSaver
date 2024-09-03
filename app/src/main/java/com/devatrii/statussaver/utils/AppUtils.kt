package com.devatrii.statussaver.utils

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.devatrii.statussaver.R

fun Activity.replaceFragment(fragment: Fragment, args: Bundle? = null) {
    val fragmentActivity = this as FragmentActivity
    val fragmentManager = fragmentActivity.supportFragmentManager

    // Check if the fragment is already in the fragment manager
    if (fragmentManager.findFragmentById(R.id.fragmentContainer) == fragment) {
        return
    }

    // Disable navigation temporarily to prevent rapid clicks
    val transaction = fragmentManager.beginTransaction().apply {
        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        args?.let {
            fragment.arguments = it
        }
        replace(R.id.fragmentContainer, fragment)
    }

    // Commit the transaction immediately allowing state loss if needed
    fragmentActivity.runOnUiThread {
        transaction.commitNowAllowingStateLoss()
    }
}