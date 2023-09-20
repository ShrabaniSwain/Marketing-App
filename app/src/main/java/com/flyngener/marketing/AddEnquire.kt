package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.marketing.databinding.ActivityAddEnquireBinding
import com.flyngener.marketing.databinding.ActivityEnquiryListingBinding

class AddEnquire : AppCompatActivity() {

    private lateinit var binding: ActivityAddEnquireBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEnquireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}