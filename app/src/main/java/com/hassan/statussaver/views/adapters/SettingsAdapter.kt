package com.hassan.statussaver.views.adapters

import android.Manifest
import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hassan.statussaver.R
import com.hassan.statussaver.databinding.DialogGuideBinding
import com.hassan.statussaver.databinding.ItemSettingsBinding
import com.hassan.statussaver.models.SettingsModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hassan.statussaver.utils.Constants
import com.hassan.statussaver.utils.getFolderPermissions
import android.provider.Settings
import com.google.android.material.button.MaterialButton


class SettingsAdapter(
    var list: ArrayList<SettingsModel>,
    var context: Context
) : RecyclerView.Adapter<SettingsAdapter.viewHolder>() {

    inner class viewHolder(var binding: ItemSettingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: SettingsModel, position: Int) {
            binding.apply {
                settingsTitle.text = model.title
                settingsDesc.text = model.desc
                switchNotifications.visibility = View.GONE
                if (model.title == "Notifications") {
                    switchNotifications.visibility = View.VISIBLE
                    checkNotificationPermission()
                    switchNotifications.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            if (isNotificationPermissionGranted()) {
                                binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                            } else {
                                binding.switchNotifications.isChecked = false
                                binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
                                showPermissionDialog()
                            }
                        } else {
                            binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
                            binding.switchNotifications.isEnabled = true
                        }
                    }
                }

                root.setOnClickListener {
                    when (position) {
                        0 -> {
                            // Handle Notifications click
                        }
                        1 -> {
                            // how to use 1st item
                            val dialog = Dialog(context)
                            val dialogBinding =
                                DialogGuideBinding.inflate((context as Activity).layoutInflater)
                            dialogBinding.okayBtn.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.setContentView(dialogBinding.root)

                            dialog.window?.setLayout(
                                ActionBar.LayoutParams.MATCH_PARENT,
                                ActionBar.LayoutParams.WRAP_CONTENT
                            )

                            dialog.show()
                            val cancelBtn = dialog.findViewById<ImageView>(R.id.cancel_btn)
                            cancelBtn.setOnClickListener {
                                dialog.dismiss()
                            }
                        }
                        2 -> {
                            // Handle Save in Folder click
                        }
                        3 -> {
                            MaterialAlertDialogBuilder(context).apply {
                                setTitle("Disclaimer")
                                setMessage("Disclaimer Here")
                                setPositiveButton("Okay", null)
                                show()
                            }
                        }
                        4 -> {
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://gimmypieapps.blogspot.com/p/status-saver-privacy-policy.html?m=1")).apply {
                                context.startActivity(this)
                            }
                        }
                        5 -> {
                            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_feedback, null)
                            val dialog = Dialog(context)
                            dialog.setContentView(dialogView)
                            dialog.window?.setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            dialog.show()
                            val dialogButton = dialogView.findViewById<MaterialButton>(R.id.okay_btn)
                            dialogButton.text = "Rate Us"
                            dialogButton.icon = null
                            dialogButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.buttonshade)
                            val ratingStars = dialogView.findViewById<RatingBar>(R.id.ratingBar)
                            ratingStars.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
                                override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                                    if (rating >= 4) {
                                        dialogButton.text = "Rate Us on Google Play store"
                                        dialogButton.icon = ContextCompat.getDrawable(context, R.drawable.playstore_svgrepo_com)
                                        dialogButton.iconTint = null
                                        dialogButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                                        dialogButton.setOnClickListener {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                                            context.startActivity(intent)
                                            dialog.dismiss()
                                        }
                                    }
                                    else{
                                        dialogButton.icon = null
                                        dialogButton.text = "Feedback"
                                        dialogButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                                        dialogButton.setOnClickListener {
                                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:hassan.hu.usman@gmail.com?subject=" + Uri.encode("Feedback of Status Saver App"))
                                            }
                                            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
                                            dialog.dismiss()
                                        }



                                    }
                                    if(rating < 1){
                                        dialogButton.text = "Rate Us"
                                        dialogButton.icon = null
                                        dialogButton.backgroundTintList = ContextCompat.getColorStateList(context, R.color.buttonshade)
                                        dialogButton.setOnClickListener{

                                        }
                                    }
                                }
                            })
                            val cancelButton = dialogView.findViewById<ImageView>(R.id.cancel_btn)
                            cancelButton.setOnClickListener {
                                dialog.dismiss()
                            }

                        }
                    }
                }
            }
        }

        private fun checkNotificationPermission() {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    binding.switchNotifications.isChecked = true
                    binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
                    binding.switchNotifications.isEnabled = false
                } else {
                    binding.switchNotifications.isChecked = false
                    binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
                    binding.switchNotifications.isEnabled = true
                }
        }

        private fun isNotificationPermissionGranted(): Boolean {
                return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        }

        private fun showPermissionDialog() {
            AlertDialog.Builder(context).apply {
                setTitle("Permission Required")
                setMessage("This app needs notification permission to proceed.")
                setPositiveButton("Okay") { _, _ ->
                    requestNotificationPermission()
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    binding.switchNotifications.isChecked = false
                    binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
                    binding.switchNotifications.isEnabled = true
                }
                show()
            }
        }

        private fun requestNotificationPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            } else {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        }

        fun onPermissionDenied() {
            binding.switchNotifications.isChecked = false
            binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.red)
            binding.switchNotifications.isEnabled = true
        }

        fun onPermissionGranted() {
            binding.switchNotifications.isChecked = true
            binding.switchNotifications.trackTintList = ContextCompat.getColorStateList(context, R.color.colorPrimary)
            binding.switchNotifications.isEnabled = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(ItemSettingsBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(model = list[position], position)
    }
}
