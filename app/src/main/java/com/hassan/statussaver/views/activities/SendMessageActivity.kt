package com.hassan.statussaver.views.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hassan.statussaver.R
import com.hassan.statussaver.databinding.ActivitySendMessageBinding
import com.google.android.material.appbar.MaterialToolbar
import com.hbb20.CountryCodePicker

class SendMessageActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySendMessageBinding.inflate(layoutInflater)
    }

    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private var isBusiness: Boolean = false

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        isBusiness = intent.getBooleanExtra("isBusiness", false)

        countryCodePicker = findViewById(R.id.country_code)
        editTextPhoneNumber = findViewById(R.id.edit_text_phone_number)
        editTextMessage = findViewById(R.id.edit_text_message)
        buttonSend = findViewById(R.id.button_send)

        buttonSend.setOnClickListener { sendMessage() }
        val toolbar = findViewById<MaterialToolbar>(R.id.tool_bar)
        toolbar.setNavigationOnClickListener {
            finish() // Finish the activity when the back button is pressed
        }
    }

    private fun sendMessage() {
        val countryCode = countryCodePicker.selectedCountryCodeWithPlus
        val phoneNumber = editTextPhoneNumber.text.toString().trim()
        val message = editTextMessage.text.toString().trim()

        if (countryCode.isEmpty()) {
            showToast("Country code is required")
            return
        }

        if (phoneNumber.isEmpty()) {
            showToast("Phone number is required")
            return
        }

        if (phoneNumber.length != 10) {
            showToast("Phone number must be 10 digits")
            return
        }

        if (message.isEmpty()) {
            showToast("Message is required")
            return
        }

        val fullPhoneNumber = countryCode + phoneNumber
        val url = "https://api.whatsapp.com/send?phone=$fullPhoneNumber&text=${Uri.encode(message)}"
        val packageName = if (isBusiness) "com.whatsapp.w4b" else "com.whatsapp"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.setPackage(packageName)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            if(isBusiness) {
                showToast("WhatsApp Business is not installed")
            } else {
                showToast("WhatsApp is not installed")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}