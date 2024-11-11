import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.tenmillionapps.statussaver.R

open class BaseActivity : AppCompatActivity() {
    private var actionCounter = 0
    private val WHATSAPP_REQUEST_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun performAction() {
        actionCounter++
        if (actionCounter == 3) {
            actionCounter = 0
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_feedback, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
        val dialogButton = dialogView.findViewById<MaterialButton>(R.id.okay_btn)
        dialogButton.text = "Rate Us"
        dialogButton.icon = null
        dialogButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonshade)
        val ratingStars = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        ratingStars.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                if (rating >= 4) {
                    dialogButton.text = "Rate Us on Google Play store"
                    dialogButton.icon = ContextCompat.getDrawable(this@BaseActivity, R.drawable.playstore_svgrepo_com)
                    dialogButton.iconTint = null
                    dialogButton.backgroundTintList = ContextCompat.getColorStateList(this@BaseActivity, R.color.colorPrimary)
                    dialogButton.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${this@BaseActivity.packageName}"))
                        this@BaseActivity.startActivity(intent)
                        dialog.dismiss()
                    }
                } else {
                    dialogButton.icon = null
                    dialogButton.text = "Feedback"
                    dialogButton.backgroundTintList = ContextCompat.getColorStateList(this@BaseActivity, R.color.colorPrimary)
                    dialogButton.setOnClickListener {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:hassan.hu.usman@gmail.com?subject=" + Uri.encode("Feedback of Status Saver App"))
                        }
                        this@BaseActivity.startActivity(Intent.createChooser(emailIntent, "Send email..."))
                        dialog.dismiss()
                    }
                }
                if (rating < 1) {
                    dialogButton.text = "Rate Us"
                    dialogButton.icon = null
                    dialogButton.backgroundTintList = ContextCompat.getColorStateList(this@BaseActivity, R.color.buttonshade)
                    dialogButton.setOnClickListener {
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