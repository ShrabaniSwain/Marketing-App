package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.marketing.databinding.ActivityAddCustomerBinding
import com.flyngener.marketing.databinding.ActivityMainBinding

class AddCustomer : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}