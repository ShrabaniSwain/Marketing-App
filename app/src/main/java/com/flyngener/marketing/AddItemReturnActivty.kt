package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.marketing.databinding.ActivityAddItemReturnActivtyBinding
import com.flyngener.marketing.databinding.ActivityAddItemSaleBinding

class AddItemReturnActivty : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemReturnActivtyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemReturnActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}